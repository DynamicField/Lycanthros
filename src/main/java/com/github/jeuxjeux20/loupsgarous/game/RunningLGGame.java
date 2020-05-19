package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public final class RunningLGGame implements LGGame {
    private static final Random random = new Random();

    private final ImmutableSet<LGPlayer> players;
    private final MutableLGGameTurn turn = new MutableLGGameTurn();

    public RunningLGGame(Set<LGPlayer> players) {
        this.players = ImmutableSet.copyOf(players);
    }

    public static RunningLGGame create(List<Player> minecraftPlayers, List<LGCard> cardComposition,
                                       MultiverseCore multiverse) {
        Preconditions.checkArgument(minecraftPlayers.size() == cardComposition.size(),
                "Il n'y a pas le meme nombre de joueurs et de cartes");

        ArrayList<LGCard> cardsToDistribute = new ArrayList<>(cardComposition);

        int[] count = new int[1];

        Set<LGPlayer> lgPlayers = minecraftPlayers.stream()
                .map(player -> {
                    // Create a dummy player
                    if (player == null) {
                        int c = ++count[0];
                        String name = "Dummy_" + c;
                        return new MutableLGPlayer(new UUID(0, 0), null, getRandomCardAndRemove(cardsToDistribute)) {
                            @Override
                            public String getName() {
                                return name;
                            }
                        };
                    }
                    MultiverseWorld playerWorld = multiverse.getMVWorldManager().getMVWorld(player.getWorld());
                    return new MutableLGPlayer(player.getUniqueId(), playerWorld, getRandomCardAndRemove(cardsToDistribute));
                })
                .collect(Collectors.toSet());

        return new RunningLGGame(lgPlayers);
    }

    private static LGCard getRandomCardAndRemove(List<LGCard> cards) {
        int index = random.nextInt(cards.size());
        LGCard randomCard = cards.get(index);
        cards.remove(index);
        return randomCard;
    }

    @Override
    public ImmutableSet<LGPlayer> getPlayers() {
        return players;
    }

    @Override
    public MutableLGGameTurn getTurn() {
        return turn;
    }
}
