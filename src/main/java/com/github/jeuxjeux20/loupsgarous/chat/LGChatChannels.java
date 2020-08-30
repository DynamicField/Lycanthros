package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameTurnTime;
import com.github.jeuxjeux20.loupsgarous.interaction.Pick;
import com.github.jeuxjeux20.loupsgarous.phases.LoupGarouVotePhase;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeams;

import java.util.Arrays;

public final class LGChatChannels {
    public static final ChatChannel DAY = new ChatChannel("day", "Jour") {
        @Override
        protected void setupView(ChatContext context, ChatChannelView view) {
            if (context.getOrchestrator().getTurn().getTime() == LGGameTurnTime.DAY) {
                view.makeFullyAccessible();
            }
            if (context.getPlayer().isDead()) {
                view.setWritable(false);
            }
        }
    };
    public static final ChatChannel DEAD = new ChatChannel("dead", "Morts") {
        @Override
        protected void setupView(ChatContext context, ChatChannelView view) {
            if (context.getPlayer().isDead()) {
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
        protected void setupView(ChatContext context, ChatChannelView view) {
            view.getAnonymizedNames().addAll(Arrays.asList(anonymizedNames));
            view.setNameDisplayed(true);

            if (context.getOrchestrator().phases().current() instanceof LoupGarouVotePhase &&
                context.getPlayer().teams().has(LGTeams.LOUPS_GAROUS)) {
                view.makeFullyAccessible();
            }
            if (context.getPlayer().isDead()) {
                view.setWritable(false);
            }
        }
    };
    public static final ChatChannel LOUPS_GAROUS_VOTE = new ChatChannel("loups_garous_vote",
            "Vote des Loups-garous") {
        @Override
        protected void setupView(ChatContext context, ChatChannelView view) {
            ChatChannelView loupsGarousView = LOUPS_GAROUS.getView(context);

            view.setReadable(loupsGarousView.isReadable());
            view.setWritable(false);
        }
    };

    private LGChatChannels() {
    }

    public static ChatChannel createPickChannel(Pick<?> pick) {
        return new ChatChannel("pick_channel_" + pick.hashCode(), pick.toString()) {
            @Override
            protected void setupView(ChatContext context, ChatChannelView view) {
                if (pick.conditions().checkPicker(context.getPlayer()).isSuccess()) {
                    view.setReadable(true);
                }
            }
        };
    }
}
