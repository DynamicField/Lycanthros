package com.github.jeuxjeux20.loupsgarous.game.atmosphere;

import com.github.jeuxjeux20.loupsgarous.Plugin;
import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayerAndGame;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickRemovedEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Votable;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.Events;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class VoteStructure implements Structure {
    public static final MetadataKey<LGPlayer> ARMOR_STAND_PLAYER_KEY =
            MetadataKey.create("armor_stand_vote", LGPlayer.class);

    private final LGGameOrchestrator orchestrator;
    private final Location location;
    private final World world;
    private final Votable votable;
    private final LGGameManager gameManager;
    private final Logger logger;

    private ArmorStand @Nullable [] armorStands;
    private Location @Nullable [] blockLocations;

    @Inject
    VoteStructure(@Assisted LGGameOrchestrator orchestrator, @Assisted Location location, @Assisted Votable votable,
                  LGGameManager gameManager, @Plugin Logger logger) {
        this.orchestrator = orchestrator;
        this.location = location;
        this.world = location.getWorld();
        this.votable = votable;
        this.gameManager = gameManager;
        this.logger = logger;
    }

    public void build() {
        remove();

        Votable.VoteState voteState = votable.getCurrentState();

        List<LGPlayer> players = orchestrator.game().getPlayers().stream()
                .filter(Check.predicate(voteState::canPickTarget))
                .collect(Collectors.toList());
        @Nullable LGPlayer playerWithMostVotes = votable.getCurrentState().getPlayerWithMostVotes();

        BuildingContext buildingContext = new BuildingContext(players, playerWithMostVotes);

        placeBlocks(buildingContext);
        placeArmorStands(buildingContext);
    }

    private void placeArmorStands(BuildingContext context) {
        armorStands = new ArmorStand[context.players.size()];

        Location armorStandLocation = location.clone();
        for (int i = 0; i < context.players.size(); i++) {
            LGPlayer player = context.players.get(i);

            Location correctedLocation = armorStandLocation.clone().add(0, 1, 0);
            ArmorStand armorStand = createArmorStand(player, correctedLocation, context.playerWithMostVotes);
            armorStands[i] = armorStand;

            armorStandLocation.add(2, 0, 0);
        }
    }

    private void placeBlocks(BuildingContext context) {
        blockLocations = new Location[context.players.size() * 2];

        Location blockLocation = location.clone();
        for (int i = 0; i < blockLocations.length; i++) {
            Block block = world.getBlockAt(blockLocation);
            block.setType(Material.BIRCH_WOOD);
            blockLocations[i] = block.getLocation();

            blockLocation.add(1, 0, 0);
        }
    }

    private ArmorStand createArmorStand(LGPlayer player, Location armorStandLocation,
                                        @Nullable LGPlayer playerWithMostVotes) {
        int voteCount = votable.getCurrentState().getPlayersVoteCount().getOrDefault(player, 0);
        String color = playerWithMostVotes == player ? ChatColor.RED.toString() + ChatColor.BOLD : "";

        ArmorStand armorStand = world.spawn(armorStandLocation, ArmorStand.class);
        armorStand.setCustomName(color + player.getName() + "(" + voteCount + ")");
        armorStand.setCustomNameVisible(true);

        EntityEquipment equipment = armorStand.getEquipment();
        if (equipment != null) {
            ItemStack head = ItemStackBuilder.of(Material.PLAYER_HEAD).transformMeta(meta -> {
                SkullMeta skullMeta = (SkullMeta) meta;

                skullMeta.setOwningPlayer(player.getOfflineMinecraftPlayer());
            }).build();

            equipment.setHelmet(head);
        } else {
            logger.warning("What? The EntityEquipment on VoteStructure's armor stand is null??");
        }

        Metadata.provideForEntity(armorStand).put(ARMOR_STAND_PLAYER_KEY, player);

        return armorStand;
    }

    public void remove() {
        if (armorStands != null) {
            for (ArmorStand armorStand : armorStands) {
                armorStand.remove();
                Metadata.provideForEntity(armorStand).remove(ARMOR_STAND_PLAYER_KEY);
            }
            armorStands = null;
        }
        if (blockLocations != null) {
            for (Location blockLocation : blockLocations) {
                blockLocation.getBlock().setType(Material.AIR);
            }
            blockLocations = null;
        }
    }

    public TerminableModule createInteractionModule() {
        return consumer -> {
            Events.merge(LGPickEvent.class, LGPickEvent.class, LGPickRemovedEvent.class)
                    .filter(votable::isMyEvent)
                    .handler(e -> build())
                    .bindWith(consumer);

            Events.subscribe(PlayerInteractAtEntityEvent.class)
                    .handler(e -> {
                        LGPlayer player = gameManager.getPlayerInGame(e.getPlayer())
                                .filter(x -> x.getOrchestrator() == orchestrator)
                                .map(LGPlayerAndGame::getPlayer)
                                .orElse(null);

                        if (player == null) {
                            return;
                        }

                        Entity rightClicked = e.getRightClicked();

                        Metadata.provideForEntity(rightClicked).get(ARMOR_STAND_PLAYER_KEY)
                                .ifPresent(target -> {
                                    Votable.VoteState voteState = votable.getCurrentState();
                                    Check check = voteState.canPick(player, target);

                                    if (check.isSuccess()) {
                                        voteState.togglePick(player, target);
                                    } else {
                                        e.getPlayer().sendMessage(ChatColor.RED + check.getErrorMessage());
                                    }
                                });
                    })
                    .bindWith(consumer);
        };
    }

    private static final class BuildingContext {
        final List<LGPlayer> players;
        final @Nullable LGPlayer playerWithMostVotes;

        private BuildingContext(List<LGPlayer> players, @Nullable LGPlayer playerWithMostVotes) {
            this.players = players;
            this.playerWithMostVotes = playerWithMostVotes;
        }
    }

    public interface Factory {
        VoteStructure create(LGGameOrchestrator orchestrator, Location location, Votable votable);
    }
}
