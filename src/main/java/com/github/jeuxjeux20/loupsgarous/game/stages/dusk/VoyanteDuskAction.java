package com.github.jeuxjeux20.loupsgarous.game.stages.dusk;

import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.github.jeuxjeux20.loupsgarous.game.interaction.AbstractPlayerPick;
import com.github.jeuxjeux20.loupsgarous.game.interaction.InteractableRegisterer;
import com.github.jeuxjeux20.loupsgarous.game.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.FunctionalPickConditions;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.PickConditions;
import com.github.jeuxjeux20.loupsgarous.game.powers.VoyantePower;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import com.google.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.VOYANTE_SYMBOL;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.importantTip;

public class VoyanteDuskAction extends DuskAction {
    private final VoyanteLookable look;

    @Inject
    VoyanteDuskAction(LGGameOrchestrator orchestrator,
                      InteractableRegisterer<VoyanteLookable> look) {
        super(orchestrator);

        this.look = look.as(LGInteractableKeys.LOOK).boundWith(this);
    }

    @Override
    protected boolean shouldRun() {
        return orchestrator.game().getPlayers().stream()
                .anyMatch(Check.predicate(look.conditions()::checkPicker));
    }

    @Override
    protected void onDuskStart() {
        orchestrator.game().getPlayers().stream()
                .filter(Check.predicate(look.conditions()::checkPicker))
                .map(LGPlayer::getMinecraftPlayer)
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

    @OrchestratorScoped
    private static final class VoyanteLookable extends AbstractPlayerPick {
        private final List<LGPlayer> playersWhoLooked = new ArrayList<>();

        @Inject
        VoyanteLookable(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        public PickConditions<LGPlayer> pickConditions() {
            return FunctionalPickConditions.<LGPlayer>builder()
                    .ensurePicker(this::isVoyante, "Vous n'êtes pas voyante !")
                    .ensurePicker(LGPlayer::isAlive, "Vous êtes mort !")
                    .ensurePicker(this::isPowerAvailable, "Vous avez déjà utilisé votre pouvoir.")
                    .ensureTarget(LGPlayer::isAlive, "La cible est déjà morte !")
                    .build();
        }

        @Override
        protected void safePick(LGPlayer picker, LGPlayer target) {
            playersWhoLooked.add(picker);

            picker.metadata().getOrPut(VoyantePower.PLAYERS_SAW_KEY, HashSet::new).add(target);

            picker.getMinecraftPlayer().ifPresent(player -> {
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
