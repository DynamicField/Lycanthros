package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.cards.CupidonCard;
import com.github.jeuxjeux20.loupsgarous.game.interaction.*;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.FunctionalPickConditions;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.PickConditions;
import com.github.jeuxjeux20.loupsgarous.game.teams.CoupleTeam;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.bukkit.boss.BarColor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.*;

public class CupidonCoupleStage extends CountdownLGStage implements InteractableProvider {
    private final Random random;

    private final CupidonCoupleCreator coupleCreator = new CupidonCoupleCreator();

    @Inject
    CupidonCoupleStage(@Assisted LGGameOrchestrator orchestrator, Random random) {
        super(orchestrator);
        this.random = random;
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.of(30);
    }

    @Override
    public boolean shouldRun() {
        return coupleCreator.getEligibleCupidons().findAny().isPresent();
    }

    @Override
    protected void start() {
        coupleCreator.getEligibleCupidons().forEach(this::sendTipNotification);
    }

    @Override
    protected void finish() {
        coupleCreator.createRandomCouples();
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

    private void sendTipNotification(LGPlayer cupidon) {
        cupidon.getMinecraftPlayer().ifPresent(player -> {
            String message = importantTip(
                    "Vous êtes Cupidon ! Faites /lgcouple <partenaire1> <partenaire2> pour créer un couple.");

            player.sendMessage(message);
            LGSoundStuff.ding(player);
        });
    }

    public CupidonCoupleCreator couples() {
        return coupleCreator;
    }

    @Override
    public Set<InteractableEntry<?>> getInteractables() {
        return ImmutableSet.of(
                new InteractableEntry<>(LGInteractableKeys.COUPLE_CREATOR, coupleCreator)
        );
    }

    public final class CupidonCoupleCreator implements Pickable<Couple> {
        private final Map<LGPlayer, Couple> couplePicks = new HashMap<>();

        private CupidonCoupleCreator() {
        }

        @Override
        public PickConditions<Couple> conditions() {
            return FunctionalPickConditions.<Couple>builder()
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
                    .and(partner.getCard().getTeams().stream().noneMatch(LGTeams::isCouple), partner.getName()
                                                                                             + " est déjà en couple !");
        }

        @Override
        public void pick(LGPlayer cupidon, Couple couple) {
            conditions().throwIfInvalid(cupidon, couple);

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

        void sendCoupleMessages(LGPlayer cupidon, Couple couple) {
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

        Stream<LGPlayer> getEligibleCupidons() {
            return orchestrator.game().getPlayers().stream().filter(Check.predicate(conditions()::checkPicker));
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
            return picker.getCard() instanceof CupidonCard;
        }

        private boolean isPowerAvailable(LGPlayer picker) {
            return !couplePicks.containsKey(picker);
        }
    }
}
