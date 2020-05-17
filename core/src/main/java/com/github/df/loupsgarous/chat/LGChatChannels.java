package com.github.df.loupsgarous.chat;

import com.github.df.loupsgarous.game.LGGameTurnTime;
import com.github.df.loupsgarous.interaction.Pick;
import com.github.df.loupsgarous.phases.LoupsGarousVotePhase;
import com.github.df.loupsgarous.teams.LGTeams;

import java.util.Arrays;

public final class LGChatChannels {
    public static final ChatChannel DAY = new ChatChannel("day", "Jour") {
        @Override
        protected void setupView(ChatChannelView view) {
            if (view.getOrchestrator().getTurn().getTime() == LGGameTurnTime.DAY) {
                view.makeFullyAccessible();
            }
            if (view.getViewer().isDead()) {
                view.setWritable(false);
            }
        }
    };
    public static final ChatChannel DEAD = new ChatChannel("dead", "Morts") {
        @Override
        protected void setupView(ChatChannelView view) {
            if (view.getViewer().isDead()) {
                view.makeFullyAccessible();
            }
        }
    };
    public static final ChatChannel LOUPS_GAROUS = new ChatChannel("loups_garous", "Loups-garous") {
        private final String[] anonymizedNames = {
                "Loup sympa",
                "Loup bizarre",
                "Loup qui adore int",
                "Loup normal",
                "Loup blanc",
                "Loup noir",
                "Loup arc-en-ciel",
                "Loup terrifiant",
                "Loup éco+",
                "Loup gris",
                "Loup dominant",
                "Loup allemand",
                "Loup speedrunner",
                "Mario LOUPdyssey"
        };

        @Override
        protected void setupView(ChatChannelView view) {
            view.getAnonymizedNames().addAll(Arrays.asList(anonymizedNames));
            view.setNameDisplayed(true);

            if (view.getOrchestrator().phases().current() instanceof LoupsGarousVotePhase &&
                view.getViewer().teams().has(LGTeams.LOUPS_GAROUS)) {
                view.makeFullyAccessible();
            }
            if (view.getViewer().isDead()) {
                view.setWritable(false);
            }
        }
    };
    public static final ChatChannel LOUPS_GAROUS_VOTE = new ChatChannel("loups_garous_vote",
            "Vote des Loups-garous") {
        @Override
        protected void setupView(ChatChannelView view) {
            ChatChannelView loupsGarousView = LOUPS_GAROUS.getView(view.getViewer());

            view.setReadable(loupsGarousView.isReadable());
            view.setWritable(false);
        }
    };

    private LGChatChannels() {
    }

    public static ChatChannel createPickChannel(Pick<?> pick) {
        return new ChatChannel("pick_channel_" + pick.hashCode(), pick.toString()) {
            @Override
            protected void setupView(ChatChannelView view) {
                if (pick.conditions().checkPicker(view.getViewer()).isSuccess()) {
                    view.setReadable(true);
                }
            }
        };
    }
}
