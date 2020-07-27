package com.github.jeuxjeux20.loupsgarous.stages;

import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.github.jeuxjeux20.loupsgarous.interaction.AbstractCouplePick;
import com.github.jeuxjeux20.loupsgarous.interaction.Couple;
import com.github.jeuxjeux20.loupsgarous.interaction.InteractableRegisterer;
import com.github.jeuxjeux20.loupsgarous.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.interaction.condition.FunctionalPickConditions;
import com.github.jeuxjeux20.loupsgarous.interaction.condition.PickConditions;
import com.github.jeuxjeux20.loupsgarous.powers.CupidonPower;
import com.github.jeuxjeux20.loupsgarous.teams.CoupleTeam;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeams;
import com.github.jeuxjeux20.loupsgarous.Check;
import com.google.inject.Inject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.*;

@StageInfo(
        name = "Cupidon",
        title = "Cupidon va tirer sa flèche et former un couple.",
        color = StageColor.BLUE,
        isTemporary = true
)
public final class CupidonCoupleStage extends CountdownLGStage {
    private final CupidonCoupleCreator coupleCreator;

    @Inject
    CupidonCoupleStage(LGGameOrchestrator orchestrator,
                       InteractableRegisterer<CupidonCoupleCreator> coupleCreator) {
        super(orchestrator);
        this.coupleCreator = coupleCreator.as(LGInteractableKeys.COUPLE_CREATOR).boundWith(this);
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.of(30);
    }

    @Override
    public boolean shouldRun() {
        return coupleCreator.canSomeonePick();
    }

    @Override
    protected void start() {
        coupleCreator.getEligibleCupidons().forEach(this::sendTipNotification);
    }

    @Override
    protected void finish() {
        coupleCreator.createRandomCouples();
    }

    private void sendTipNotification(LGPlayer cupidon) {
        cupidon.minecraft(player -> {
            String message = importantTip(
                    "Vous êtes Cupidon ! Faites /lgcouple <partenaire1> <partenaire2> pour créer un couple.");

            player.sendMessage(message);
            LGSoundStuff.ding(player);
        });
    }

    public CupidonCoupleCreator couples() {
        return coupleCreator;
    }

    @OrchestratorScoped
    public static class CupidonCoupleCreator extends AbstractCouplePick {
        private final Map<LGPlayer, Couple> couplePicks = new HashMap<>();
        private final Random random;

        @Inject
        CupidonCoupleCreator(LGGameOrchestrator orchestrator, Random random) {
            super(orchestrator);
            this.random = random;
        }

        @Override
        public PickConditions<Couple> pickConditions() {
            return conditionsBuilder()
                    .ensurePicker(this::isCupidon, "Vous n'êtes pas cupidon !")
                    .ensurePicker(LGPlayer::isAlive, "Vous êtes mort !")
                    .ensurePicker(this::isPowerAvailable, "Vous avez déjà formé un couple !")
                    .apply(this::checkAllPartners)
                    .build();
        }

        void checkAllPartners(FunctionalPickConditions.Builder<Couple> builder) {
            builder.ensureTarget(couple -> partnerCheck(couple.getPartner1()))
                    .ensureTarget(couple -> partnerCheck(couple.getPartner2()));
        }

        Check partnerCheck(LGPlayer partner) {
            return Check.ensure(partner.isAlive(), partner.getName() + " est mort !")
                    .and(partner.teams().get().stream().noneMatch(LGTeams::isCouple), partner.getName()
                                                                                             + " est déjà en couple !");
        }

        @Override
        protected void safePick(LGPlayer cupidon, Couple couple) {
            couplePicks.put(cupidon, couple);

            CoupleTeam coupleTeam = LGTeams.newCouple();

            for (LGPlayer partner : couple.getPartners()) {
                partner.teams().add(coupleTeam);
            }

            sendCoupleMessages(cupidon, couple);

            if (couplePicks.keySet().containsAll(getEligibleCupidons().collect(Collectors.toList()))) {
                orchestrator.stages().current().stop();
            }
        }

        void sendCoupleMessages(LGPlayer cupidon, Couple couple) {
            cupidon.minecraft(player -> {
                String message = info(HEART_SYMBOL + " ") +
                                 player(couple.getPartner1().getName()) + info(" et ") +
                                 player(couple.getPartner2().getName()) +
                                 info(" sont maintenant en couple !");
                player.sendMessage(message);

                LGSoundStuff.ding(player);
            });

            for (LGPlayer partner : couple.getPartners()) {
                partner.minecraft(player -> {
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

        Stream<LGPlayer> getEligibleCupidons() {
            return getEligiblePickers();
        }

        Stream<LGPlayer> getEligiblePartners() {
            return orchestrator.game().getPlayers().stream()
                    .filter(Check.predicate(this::partnerCheck));
        }

        void createRandomCouples() {
            List<LGPlayer> cupidonsWithoutCouple = getEligibleCupidons()
                    .filter(this::isPowerAvailable)
                    .collect(Collectors.toList());

            for (LGPlayer cupidon : cupidonsWithoutCouple) {
                createRandomCouple().ifPresent(couple -> pick(cupidon, couple));
            }
        }

        public Optional<Couple> createRandomCouple() {
            List<LGPlayer> eligiblePartners = getEligiblePartners().collect(Collectors.toCollection(ArrayList::new));
            if (eligiblePartners.size() < 2) {
                orchestrator.logger().warning("Not enough eligible partners! (" + eligiblePartners.size() + ")");
                return Optional.empty();
            }

            LGPlayer partner1 = eligiblePartners.get(random.nextInt(eligiblePartners.size()));
            eligiblePartners.remove(partner1);
            LGPlayer partner2 = eligiblePartners.get(random.nextInt(eligiblePartners.size()));

            return Optional.of(new Couple(partner1, partner2));
        }

        private boolean isCupidon(LGPlayer picker) {
            return picker.powers().has(CupidonPower.class);
        }

        private boolean isPowerAvailable(LGPlayer picker) {
            return !couplePicks.containsKey(picker);
        }
    }
}
