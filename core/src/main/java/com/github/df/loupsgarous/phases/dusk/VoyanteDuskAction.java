package com.github.df.loupsgarous.phases.dusk;

import com.github.df.loupsgarous.Check;
import com.github.df.loupsgarous.LGSoundStuff;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.interaction.PlayerPick;
import com.github.df.loupsgarous.interaction.LGInteractableKeys;
import com.github.df.loupsgarous.interaction.condition.PickConditions;
import com.github.df.loupsgarous.powers.VoyantePower;
import com.github.df.loupsgarous.util.OptionalUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.github.df.loupsgarous.chat.LGChatStuff.VOYANTE_SYMBOL;
import static com.github.df.loupsgarous.chat.LGChatStuff.importantTip;

public class VoyanteDuskAction extends DuskAction {
    private final VoyanteLookable look;

    public VoyanteDuskAction(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        this.look = new VoyanteLookable(orchestrator);
    }

    @Override
    protected boolean shouldRun() {
        return orchestrator.getPlayers().stream()
                .anyMatch(Check.predicate(look.conditions()::checkPicker));
    }

    @Override
    protected void onDuskStart() {
        look.register(LGInteractableKeys.LOOK).bindWith(this);

        orchestrator.getPlayers().stream()
                .filter(Check.predicate(look.conditions()::checkPicker))
                .map(LGPlayer::minecraft)
                .flatMap(OptionalUtils::stream)
                .forEach(this::sendNotification);
    }

    private void sendNotification(Player player) {
        String text = VOYANTE_SYMBOL +
                      "Vous êtes une voyante ! Faites /lglook <joueur> pour voir le rôle de quelqu'un !";
        player.sendMessage(importantTip(text));
        LGSoundStuff.ding(player);
    }

    @Override
    protected String getMessage() {
        return "La voyante va découvrir le rôle de quelqu'un...";
    }

    public VoyanteLookable look() {
        return look;
    }

    private static final class VoyanteLookable extends PlayerPick {
        private final List<LGPlayer> playersWhoLooked = new ArrayList<>();

        public VoyanteLookable(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        public PickConditions<LGPlayer> pickConditions() {
            return conditionsBuilder()
                    .ensurePicker(this::isVoyante, "Vous n'êtes pas voyante !")
                    .ensurePicker(LGPlayer::isAlive, "Vous êtes mort !")
                    .ensurePicker(this::isPowerAvailable, "Vous avez déjà utilisé votre pouvoir.")
                    .ensureTarget(LGPlayer::isAlive, "La cible est déjà morte !")
                    .build();
        }

        @Override
        protected void safePick(LGPlayer picker, LGPlayer target) {
            playersWhoLooked.add(picker);

            picker.getStored(VoyantePower.PLAYERS_SAW_PROPERTY).add(target);

            picker.minecraft(player -> {
                player.sendMessage(
                        ChatColor.DARK_PURPLE.toString() + "Votre boule de cristal indique... que " +
                        ChatColor.BOLD + target.getName() +
                        ChatColor.DARK_PURPLE + " est " + ChatColor.BOLD + target.getCard().getName() +
                        ChatColor.DARK_PURPLE + "."
                );
                LGSoundStuff.enchant(player);
            });
        }

        private boolean isPowerAvailable(LGPlayer picker) {
            return !playersWhoLooked.contains(picker);
        }

        private boolean isVoyante(LGPlayer player) {
            return player.powers().has(VoyantePower.class);
        }
    }
}
