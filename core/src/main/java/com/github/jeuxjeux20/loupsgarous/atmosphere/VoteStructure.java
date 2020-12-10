package com.github.jeuxjeux20.loupsgarous.atmosphere;

import com.github.jeuxjeux20.loupsgarous.Check;
import com.github.jeuxjeux20.loupsgarous.event.interaction.LGPickAddedEvent;
import com.github.jeuxjeux20.loupsgarous.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.event.interaction.LGPickRemovedEvent;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.Vote;
import me.lucko.helper.Events;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import me.lucko.helper.metadata.TransientValue;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class VoteStructure implements Structure {
    public static final MetadataKey<LGPlayer> ARMOR_STAND_PLAYER_KEY =
            MetadataKey.create("armor_stand_vote", LGPlayer.class);

    private final LGGameOrchestrator orchestrator;
    private final Location location;
    private final World world;
    private final Vote<LGPlayer> vote;

    private final int spacing = 3;
    private final Material blockMaterial = Material.OAK_WOOD;

    private BackedStructure backedStructure = BackedStructure.EMPTY;

    public VoteStructure(LGGameOrchestrator orchestrator, Location location, Vote<LGPlayer> vote) {
        this.orchestrator = orchestrator;
        this.location = location;
        this.world = location.getWorld();
        this.vote = vote;
    }

    public void build() {
        remove();

        BuildingContext buildingContext = createBuildingContext();

        placeBlocks(buildingContext);
        placeArmorStands(buildingContext);

        backedStructure = buildingContext.structureBuilder.build();
        backedStructure.build();
    }

    private BuildingContext createBuildingContext() {
        List<LGPlayer> players = orchestrator.getPlayers().stream()
                .filter(Check.predicate(vote.conditions()::checkTarget))
                .collect(Collectors.toList());
        LGPlayer elected = vote.getOutcome().getElected().orElse(null);

        return new BuildingContext(players, elected);
    }

    private void placeBlocks(BuildingContext context) {
        Location blockLocation = location.clone();
        for (int i = 0; i < context.blockCount; i++) {
            context.structureBuilder.transformBlock(blockLocation, block -> block.setType(blockMaterial));

            blockLocation.add(1, 0, 0);
        }
    }

    private void placeArmorStands(BuildingContext context) {
        Location armorStandLocation = location.clone();
        for (LGPlayer player : context.players) {
            Location correctedLocation = armorStandLocation.clone().add(0.5, 1, 0.5);

            context.structureBuilder.spawnEntity(() -> createArmorStand(player, correctedLocation, context));

            armorStandLocation.add(spacing, 0, 0);
        }
    }

    private ArmorStand createArmorStand(LGPlayer player, Location armorStandLocation, BuildingContext context) {
        int voteCount = vote.getVotes().count(player);
        String color = context.playerWithMostVotes == player ? ChatColor.RED.toString() + ChatColor.BOLD : "";

        ArmorStand armorStand = world.spawn(armorStandLocation, ArmorStand.class);

        armorStand.setCustomName(color + player.getName() + " (" + voteCount + ")");
        armorStand.setCustomNameVisible(true);
        armorStand.setArms(true);

        dressUpArmorStand(player, armorStand);

        Metadata.provideForEntity(armorStand).put(ARMOR_STAND_PLAYER_KEY, new TransientValue<LGPlayer>() {
            @Override
            public LGPlayer getOrNull() {
                return player;
            }

            @Override
            public boolean shouldExpire() {
                return armorStand.isDead();
            }
        });

        return armorStand;
    }

    private void dressUpArmorStand(LGPlayer player, ArmorStand armorStand) {
        EntityEquipment equipment = Objects.requireNonNull(armorStand.getEquipment());

        ItemStack head = ItemStackBuilder.of(Material.PLAYER_HEAD).transformMeta(meta -> {
            SkullMeta skullMeta = (SkullMeta) meta;

            skullMeta.setOwningPlayer(player.minecraftOffline());
        }).build();

        equipment.setHelmet(head);
        equipment.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        equipment.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        equipment.setBoots(new ItemStack(Material.LEATHER_BOOTS));
    }

    public void remove() {
        backedStructure.remove();
        backedStructure = BackedStructure.EMPTY;
    }

    public TerminableModule createInteractionModule() {
        return new InteractionModule();
    }

    private class InteractionModule implements TerminableModule {
        @Override
        public void setup(@Nonnull TerminableConsumer consumer) {
            Events.merge(LGPickEvent.class, LGPickAddedEvent.class, LGPickRemovedEvent.class)
                    .filter(vote::isMyEvent)
                    .handler(e -> build())
                    .bindWith(consumer);

            Events.subscribe(PlayerInteractAtEntityEvent.class)
                    .handler(this::handleEntityInteraction)
                    .bindWith(consumer);
        }

        private void handleEntityInteraction(PlayerInteractAtEntityEvent event) {
            LGPlayer player = orchestrator.getPlayer(event.getPlayer().getUniqueId())
                    .orElse(null);

            if (player == null) {
                return;
            }

            Entity rightClicked = event.getRightClicked();

            Metadata.provideForEntity(rightClicked).get(ARMOR_STAND_PLAYER_KEY)
                    .ifPresent(target -> {
                        event.setCancelled(true);

                        Check check = vote.conditions().checkPick(player, target);

                        if (check.isSuccess()) {
                            vote.togglePick(player, target);
                        } else {
                            event.getPlayer().sendMessage(ChatColor.RED + check.getErrorMessage());
                        }
                    });
        }
    }

    private final class BuildingContext {
        final List<LGPlayer> players;
        final @Nullable LGPlayer playerWithMostVotes;
        final BackedStructure.Builder structureBuilder = BackedStructure.builder();
        final int blockCount;

        BuildingContext(List<LGPlayer> players, @Nullable LGPlayer playerWithMostVotes) {
            this.players = players;
            this.playerWithMostVotes = playerWithMostVotes;

            blockCount = 1 + spacing * (players.size() - 1);
        }
    }

}
