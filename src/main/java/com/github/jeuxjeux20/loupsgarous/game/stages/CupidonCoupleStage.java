package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.LGTeams;
import com.github.jeuxjeux20.loupsgarous.game.cards.CupidonCard;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.bukkit.boss.BarColor;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.*;

public class CupidonCoupleStage extends AsyncLGGameStage implements CountdownTimedStage {
    private final Random random;
    private final LoupsGarous plugin;
    private final TickEventCountdown countdown;

    private final Map<LGPlayer, Couple> couplePicks = new HashMap<>();

    @Inject
    CupidonCoupleStage(@Assisted LGGameOrchestrator orchestrator, Random random, LoupsGarous plugin) {
        super(orchestrator);
        this.random = random;
        this.plugin = plugin;

        countdown = new TickEventCountdown(this, 30);
    }

    @Override
    public boolean shouldRun() {
        return getEligibleCupidons().findAny().isPresent();
    }

    @Override
    public CompletableFuture<Void> run() {
        getEligibleCupidons().forEach(this::sendTipNotification);

        return countdown.start().thenRun(this::createRandomCouples);
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

    public Check canCreateCouple(LGPlayer cupidon, Couple couple) {
        return canPlayerCreateCouple(cupidon)
                .and(couple.partner1 != couple.partner2, "Impossible de faire un couple avec deux mêmes partenaires.")
                .and(partnerCheck(couple.partner1))
                .and(partnerCheck(couple.partner2));

    }

    public void createCouple(LGPlayer cupidon, Couple couple) {
        canCreateCouple(cupidon, couple).ifError(error -> {
            throw new IllegalArgumentException("Cannot create couple: " + error);
        });

        couplePicks.put(cupidon, couple);

        String coupleTeam = LGTeams.newCouple();

        for (LGPlayer partner : couple.partners) {
            orchestrator.cards().addTeam(partner.getCard(), coupleTeam);
        }

        sendCoupleMessages(cupidon, couple);

        if (couplePicks.keySet().containsAll(getEligibleCupidons().collect(Collectors.toList())) &&
            countdown.isRunning()) {
            countdown.interrupt(); // All cupidons have chosen their couples.
        }
    }

    private void sendCoupleMessages(LGPlayer cupidon, Couple couple) {
        cupidon.getMinecraftPlayer().ifPresent(player -> {
            String message = info(HEART_SYMBOL + " ") +
                             player(couple.partner1.getName()) + info(" et ") + player(couple.partner2.getName()) +
                             info(" sont maintenant en couple !");
            player.sendMessage(message);

            LGSoundStuff.ding(player);
        });

        for (LGPlayer partner : couple.partners) {
            partner.getMinecraftPlayer().ifPresent(player -> {
                LGPlayer otherPartner = couple.partner1 == partner ? couple.partner2 : couple.partner1;

                String message = info(HEART_SYMBOL + " Vous êtes maintenant en couple avec ") +
                                 player(otherPartner.getName()) + info("!");
                player.sendMessage(message);

                if (partner != cupidon) // Avoid double DING
                    LGSoundStuff.ding(player);
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
        return orchestrator.getGame().getPlayers().stream().filter(Check.predicate(this::canPlayerCreateCouple));
    }

    private Stream<LGPlayer> getEligiblePartners() {
        return orchestrator.getGame().getPlayers().stream()
                .filter(Check.predicate(this::partnerCheck));
    }

    private Optional<Couple> createRandomCouple() {
        List<LGPlayer> eligiblePartners = getEligiblePartners().collect(Collectors.toCollection(ArrayList::new));
        if (eligiblePartners.size() < 2) {
            plugin.getLogger().warning("Not enough eligible partners! (" + eligiblePartners.size() + ")");
            return Optional.empty();
        }

        LGPlayer partner1 = eligiblePartners.get(random.nextInt(eligiblePartners.size()));
        eligiblePartners.remove(partner1);
        LGPlayer partner2 = eligiblePartners.get(random.nextInt(eligiblePartners.size()));

        return Optional.of(new Couple(partner1, partner2));
    }

    @Override
    public @Nullable String getName() {
        return "Cupidon";
    }

    @Override
    public Optional<String> getTitle() {
        return Optional.of("Cupidon va tirer sa flèche et former un couple.");
    }

    @Override
    public boolean isTemporary() {
        return true;
    }

    @Override
    public BarColor getBarColor() {
        return BarColor.BLUE;
    }

    @Override
    public Countdown getCountdown() {
        return countdown;
    }

    public static class Couple {
        public final LGPlayer partner1;
        public final LGPlayer partner2;
        public final LGPlayer[] partners;

        public Couple(LGPlayer partner1, LGPlayer partner2) {
            this.partner1 = Objects.requireNonNull(partner1);
            this.partner2 = Objects.requireNonNull(partner2);
            partners = new LGPlayer[]{partner1, partner2};
        }
    }
}
