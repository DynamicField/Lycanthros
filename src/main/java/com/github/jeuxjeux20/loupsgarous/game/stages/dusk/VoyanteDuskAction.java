package com.github.jeuxjeux20.loupsgarous.game.stages.dusk;

import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.cards.VoyanteCard;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Lookable;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.VOYANTE_SYMBOL;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.importantTip;

public class VoyanteDuskAction extends DuskStage.Action implements Lookable {
    private final List<LGPlayer> playersWhoLooked = new ArrayList<>();

    @Override
    protected boolean shouldRun(LGGameOrchestrator orchestrator) {
        return orchestrator.game().getAlivePlayers().anyMatch(x -> canPlayerLook(x).isSuccess());
    }

    @Override
    protected void onDuskStart(LGGameOrchestrator orchestrator) {
        orchestrator.game().getPlayers().stream()
                .filter(Check.predicate(this::canPlayerLook))
                .map(LGPlayer::getMinecraftPlayer)
                .flatMap(OptionalUtils::stream)
                .forEach(this::sendNotification);
    }

    private void sendNotification(Player player) {
        String text = VOYANTE_SYMBOL + "Vous êtes une voyante ! Faites /lglook <joueur> pour voir le rôle de quelqu'un !";
        player.sendMessage(importantTip(text));
        LGSoundStuff.ding(player);
    }

    @Override
    protected String getMessage() {
        return "La voyante va découvrir le rôle de quelqu'un...";
    }

    @Override
    public Check canPlayerLook(LGPlayer looker) {
        return Check.ensure(looker.getCard() instanceof VoyanteCard, "Vous n'êtes pas voyante !")
                .and(looker.isAlive(), "Vous êtes mort !")
                .and(!playersWhoLooked.contains(looker), "Vous avez déjà utilisé votre pouvoir.");
    }

    @Override
    public Check canLookTarget(LGPlayer target) {
        return Check.ensure(target.isAlive(),
                target.getName() + " est déjà mort ! (Il est " + target.getCard().getName() + ")");
    }

    @Override
    public void look(LGPlayer looker, LGPlayer target) {
        playersWhoLooked.add(looker);

        VoyanteCard voyanteCard = (VoyanteCard) looker.getCard();
        voyanteCard.getPlayersSaw().add(target);

        looker.getMinecraftPlayer().ifPresent(player -> {
            player.sendMessage(
                    ChatColor.DARK_PURPLE.toString() + "Votre boule de cristal indique... que " +
                    ChatColor.BOLD + target.getName() +
                    ChatColor.DARK_PURPLE + " est " + ChatColor.BOLD + target.getCard().getName() +
                    ChatColor.DARK_PURPLE + "."
            );
            LGSoundStuff.enchant(player);
        });
    }
}
