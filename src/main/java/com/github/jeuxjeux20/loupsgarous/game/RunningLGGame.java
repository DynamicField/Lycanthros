package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
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

    public static RunningLGGame create(Set<Player> minecraftPlayers, List<LGCard> cards) {
        Preconditions.checkArgument(minecraftPlayers.size() == cards.size(),
                "Il n'y a pas le meme nombre de joueurs et de cartes");

        ArrayList<LGCard> cardsToDistribute = new ArrayList<>(cards);

        int[] count = new int[1];

        Set<LGPlayer> lgPlayers = minecraftPlayers.stream()
                .map(player -> {
                    // Create a dummy player
                    if (player == null) {
                        int c = ++count[0];
                        String name = "Dummy_" + c;
                        return new MutableLGPlayer(new UUID(0, 0), getRandomCardAndRemove(cardsToDistribute)) {
                            @Override
                            public String getName() {
                                return name;
                            }
                        };
                    }
                    return new MutableLGPlayer(player.getUniqueId(), getRandomCardAndRemove(cardsToDistribute));
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
