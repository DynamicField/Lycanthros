package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.error;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;

public interface Votable extends StatefulPickableProvider {
    @Override
    VoteState getCurrentState();

    String getIndicator();

    class VoteState extends PickState {
        public VoteState(LGGameOrchestrator orchestrator, Votable me) {
            super(orchestrator, me);
        }

        /**
         * Gets the player with the most votes, if there are multiple players
         * with the same (highest) count of votes, or if there are no votes at all,
         * this methods returns {@code null}.
         *
         * @return the most voted player, or {@code null} if there isn't a clear distinction
         */
        public synchronized @Nullable LGPlayer getPlayerWithMostVotes() {
            // No votes
            if (picks.size() == 0) return null;
            // Only one vote
            // ---
            // MdrJeNinja -> LGCramé
            if (picks.size() == 1) return picks.values().iterator().next();

            Map<LGPlayer, Integer> votedPlayerCount = getPlayersVoteCount();

            // Unanimous vote
            // ---
            // ElFamosoLG : 10 votes
            if (votedPlayerCount.size() == 1) return votedPlayerCount.keySet().iterator().next(); // First item

            List<Map.Entry<LGPlayer, Integer>> highestVoteCounts = getTwoHighestVotes(votedPlayerCount);

            Map.Entry<LGPlayer, Integer> highestVote = highestVoteCounts.get(0);
            Map.Entry<LGPlayer, Integer> secondHighestVote = highestVoteCounts.get(1);

            // If the two highest votes are the same, nobody gets elected.
            // ---
            // ChatonDouteux  : 5 votes    | highestVote
            // SuperMangeChat : 5 votes    | secondHighestVote
            // JeSuisInno     : 4 votes    --------------------
            if (highestVote.getValue().equals(secondHighestVote.getValue())) return null;

            // If it's not the same count then it's the highestVote gets elected, for good or for worse.
            // ---
            // LGCramé        : 8 votes    | highestVote
            // EncoreUnLG     : 5 votes    | secondHighestVote
            return highestVote.getKey();
        }

        /**
         * Gets a map with the value representing how much votes the player (the key) got.
         * <p>
         * <b>Example:</b>
         * <pre>{@code
         * PykeDeMort    -> ElFamosoLG       | ElFamosoLG : 2 votes
         * AnnieDeMort   -> ElFamosoLG       | Woufe      : 1 vote
         * InsererPseudo -> Woufe            | LoL        : 1 vote
         * CETAIT_SUR    -> LoL              - }</pre>
         *
         * @return a map representing the players and the count of votes they got
         */
        @NotNull
        public Map<LGPlayer, Integer> getPlayersVoteCount() {
            Map<LGPlayer, Integer> votedPlayerCount = new HashMap<>();

            // Fill the votes count map
            picks.forEach((from, to) -> {
                int count = votedPlayerCount.getOrDefault(to, 0) + 1;
                votedPlayerCount.put(to, count);
            });
            return votedPlayerCount;
        }

        public int getTotalVoteCount() {
            return getPlayersVoteCount().values().stream().reduce(0, Integer::sum);
        }

        @Override
        protected @NotNull String getTargetDeadError(@NotNull LGPlayer target) {
            return error("Impossible de voter pour ") + player(target.getName()) + error(" car il est mort !");
        }

        @Override
        protected @NotNull String getPickerDeadError() {
            return "Impossible de voter, car vous êtes mort !";
        }

        @NotNull
        private List<Map.Entry<LGPlayer, Integer>> getTwoHighestVotes(Map<LGPlayer, Integer> votedPlayerCount) {
            return votedPlayerCount.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(2)
                    .collect(Collectors.toList());
        }
    }
}
