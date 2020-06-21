package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.Plugin;
import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.cards.CupidonCard;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Couple;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.CoupleCreator;
import com.github.jeuxjeux20.loupsgarous.game.teams.CoupleTeam;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.bukkit.boss.BarColor;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.*;

public class CupidonCoupleStage extends CountdownLGStage implements CoupleCreator {
    private final Random random;
    private final Logger logger;

    private final Map<LGPlayer, Couple> couplePicks = new HashMap<>();

    @Inject
    CupidonCoupleStage(@Assisted LGGameOrchestrator orchestrator, Random random, @Plugin Logger logger) {
        super(orchestrator);
        this.random = random;
        this.logger = logger;
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.of(30);
    }

    @Override
    public boolean shouldRun() {
        return getEligibleCupidons().findAny().isPresent();
    }

    @Override
    protected void start() {
        getEligibleCupidons().forEach(this::sendTipNotification);
    }

    @Override
    protected void finish() {
        createRandomCouples();
    }

    @Override
    public String getName() {
        return "Cupidon";
    }

    @Override
    public String getTitle() {
        return "Cupidon va tirer sa flèche et former un couple.";
    }

    @Override
    public boolean isTemporary() {
        return true;
    }

    @Override
    public BarColor getBarColor() {
        return BarColor.BLUE;
    }

    private void createRandomCouples() {
        List<LGPlayer> cupidonsWithoutCouple = getEligibleCupidons()
                .filter(x -> !couplePicks.containsKey(x))
                .collect(Collectors.toList());

        for (LGPlayer cupidon : cupidonsWithoutCouple) {
            createRandomCouple().ifPresent(couple -> createCouple(cupidon, couple));
        }
    }

    public Check canPlayerCreateCouple(LGPlayer cupidon) {
        return Check.ensure(cupidon.getCard() instanceof CupidonCard, "Vous n'êtes pas cupidon!")
                .and(cupidon.isAlive(), "Vous êtes mort !")
                .and(!couplePicks.containsKey(cupidon), "Vous avez déjà formé un couple !");
    }

    @Override
    public Check canCreateThatCouple(Couple couple) {
        return Check.ensure(couple.getPartner1() != couple.getPartner2(), "Impossible de faire un couple avec deux mêmes partenaires.")
                .and(partnerCheck(couple.getPartner1()))
                .and(partnerCheck(couple.getPartner2()));
    }

    public void createCouple(LGPlayer cupidon, Couple couple) {
        canCreateCouple(cupidon, couple).ifError(error -> {
            throw new IllegalArgumentException("Cannot create couple: " + error);
        });

        couplePicks.put(cupidon, couple);

        CoupleTeam coupleTeam = LGTeams.newCouple();

        for (LGPlayer partner : couple.getPartners()) {
            orchestrator.cards().addTeam(partner.getCard(), coupleTeam);
        }

        sendCoupleMessages(cupidon, couple);

        if (couplePicks.keySet().containsAll(getEligibleCupidons().collect(Collectors.toList()))) {
            getCountdown().tryInterrupt(); // All cupidons have chosen their couples.
        }
    }

    private void sendCoupleMessages(LGPlayer cupidon, Couple couple) {
        cupidon.getMinecraftPlayer().ifPresent(player -> {
            String message = info(HEART_SYMBOL + " ") +
                             player(couple.getPartner1().getName()) + info(" et ") + player(couple.getPartner2().getName()) +
                             info(" sont maintenant en couple !");
            player.sendMessage(message);

            LGSoundStuff.ding(player);
        });

        for (LGPlayer partner : couple.getPartners()) {
            partner.getMinecraftPlayer().ifPresent(player -> {
                LGPlayer otherPartner = couple.getOtherPartner(partner);

                String message = info(HEART_SYMBOL + " Vous êtes maintenant en couple avec ") +
                                 player(otherPartner.getName()) + info("!");
                player.sendMessage(message);

                // Avoid double DING
                if (partner != cupidon) {
                    LGSoundStuff.ding(player);
                }
            });
        }
    }

    private void sendTipNotification(LGPlayer cupidon) {
        cupidon.getMinecraftPlayer().ifPresent(player -> {
            String message = importantTip(
                    "Vous êtes Cupidon ! Faites /lgcouple <partenaire1> <partenaire2> pour créer un couple.");

            player.sendMessage(message);
            LGSoundStuff.ding(player);
        });
    }

    private Check partnerCheck(LGPlayer partner) {
        return Check.ensure(partner.isAlive(), partner.getName() + " est mort !")
                .and(partner.getCard().getTeams().stream().noneMatch(LGTeams::isCouple), partner.getName()
                                                                                         + " est déjà en couple !");
    }

    private Stream<LGPlayer> getEligibleCupidons() {
        return orchestrator.game().getPlayers().stream().filter(Check.predicate(this::canPlayerCreateCouple));
    }

    private Stream<LGPlayer> getEligiblePartners() {
        return orchestrator.game().getPlayers().stream()
                .filter(Check.predicate(this::partnerCheck));
    }

    private Optional<Couple> createRandomCouple() {
        List<LGPlayer> eligiblePartners = getEligiblePartners().collect(Collectors.toCollection(ArrayList::new));
        if (eligiblePartners.size() < 2) {
            logger.warning("Not enough eligible partners! (" + eligiblePartners.size() + ")");
            return Optional.empty();
        }

        LGPlayer partner1 = eligiblePartners.get(random.nextInt(eligiblePartners.size()));
        eligiblePartners.remove(partner1);
        LGPlayer partner2 = eligiblePartners.get(random.nextInt(eligiblePartners.size()));

        return Optional.of(new Couple(partner1, partner2));
    }

}
