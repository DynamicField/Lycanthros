package com.github.jeuxjeux20.loupsgarous.game.interaction.vote;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.VillageVoteStage;
import com.github.jeuxjeux20.loupsgarous.game.tags.LGTags;

public class MaireVoteOutcomeModifier implements VoteOutcomeModifier<LGPlayer> {
    @Override
    public VoteOutcome<LGPlayer> modifyOutcome(VoteOutcomeContext<LGPlayer> context, VoteOutcome<LGPlayer> outcome) {
        if (context.getVotableClass() != VillageVoteStage.VillageVotable.class) {
            return outcome;
        }

        return outcome.accept(new TransformingVoteOutcomeVisitor<LGPlayer>() {
            @Override
            public VoteOutcome<LGPlayer> visit(IndecisiveVoteOutcome<LGPlayer> indecisiveVoteOutcome) {
                for (LGPlayer conflictingPlayer : indecisiveVoteOutcome.getConflictingCandidates()) {
                    if (conflictingPlayer.getTags().contains(LGTags.MAIRE)) {
                        return new RelativeMajorityVoteOutcome<>(conflictingPlayer);
                    }
                }

                return indecisiveVoteOutcome;
            }
        });
    }
}
