package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.kill.causes.LGKillCause;
import com.github.jeuxjeux20.loupsgarous.game.powers.LGPower;
import com.github.jeuxjeux20.loupsgarous.game.powers.PowerRegistry;
import com.github.jeuxjeux20.loupsgarous.game.tags.LGTag;
import com.github.jeuxjeux20.loupsgarous.game.tags.TagRegistry;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeam;
import com.github.jeuxjeux20.loupsgarous.game.teams.TeamRegistry;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MutableClassToInstanceMap;
import me.lucko.helper.metadata.MetadataMap;

import java.util.*;

public class OrchestratedLGPlayer implements LGPlayer {
    private final BackingLGPlayer backingPlayer;
    private final LGGameOrchestrator orchestrator;

    private final Set<LGTeam> implicitCardTeams = new HashSet<>();
    private final ClassToInstanceMap<LGPower> implicitCardPowers = MutableClassToInstanceMap.create();

    public OrchestratedLGPlayer(BackingLGPlayer backingPlayer, LGGameOrchestrator orchestrator) {
        this.backingPlayer = backingPlayer;
        this.orchestrator = orchestrator;
    }

    @Override
    public UUID getPlayerUUID() {
        return backingPlayer.getPlayerUUID();
    }

    @Override
    public LGCard getCard() {
        return backingPlayer.getCard();
    }

    @Override
    public boolean isDead() {
        return backingPlayer.isDead();
    }

    @Override
    public boolean isAway() {
        return backingPlayer.isAway();
    }

    @Override
    public MetadataMap metadata() {
        return backingPlayer.metadata();
    }

    @Override
    public TeamRegistry teams() {
        return new TeamRegistry() {
            @Override
            public ImmutableSet<LGTeam> get() {
                return ImmutableSet.copyOf(backingPlayer.getTeams());
            }

            @Override
            public boolean add(LGTeam item) {
                orchestrator.state().mustBe(LGGameState.STARTED);

                // Mark our team as explicit.
                implicitCardTeams.remove(item);

                return backingPlayer.getTeams().add(item);
            }

            @Override
            public boolean has(LGTeam item) {
                return backingPlayer.getTeams().contains(item);
            }

            @Override
            public boolean remove(LGTeam item) {
                orchestrator.state().mustBe(LGGameState.STARTED);

                implicitCardTeams.remove(item);

                return backingPlayer.getTeams().remove(item);
            }
        };
    }

    @Override
    public TagRegistry tags() {
        return new TagRegistry() {
            @Override
            public ImmutableSet<LGTag> get() {
                return ImmutableSet.copyOf(backingPlayer.getTags());
            }

            @Override
            public boolean add(LGTag item) {
                orchestrator.state().mustBe(LGGameState.STARTED);

                return backingPlayer.getTags().add(item);
            }

            @Override
            public boolean has(LGTag item) {
                return backingPlayer.getTags().contains(item);
            }

            @Override
            public boolean remove(LGTag item) {
                orchestrator.state().mustBe(LGGameState.STARTED);

                return backingPlayer.getTags().remove(item);
            }
        };
    }

    @Override
    public PowerRegistry powers() {
        return new PowerRegistry() {
            @Override
            public ImmutableClassToInstanceMap<LGPower> get() {
                return ImmutableClassToInstanceMap.copyOf(backingPlayer.getPowers());
            }

            @Override
            public <T extends LGPower> Optional<T> get(Class<T> powerClass) {
                return Optional.ofNullable(backingPlayer.getPowers().getInstance(powerClass));
            }

            @Override
            public <T extends LGPower> T getOrThrow(Class<T> powerClass) {
                T instance = backingPlayer.getPowers().getInstance(powerClass);
                if (instance == null) {
                    throw new NoSuchElementException("No power " + powerClass + " found.");
                }
                return instance;
            }

            @Override
            public void put(LGPower power) {
                orchestrator.state().mustBe(LGGameState.STARTED);

                implicitCardPowers.remove(power.getClass());

                backingPlayer.getPowers().put(power.getClass(), power);
            }

            @Override
            public boolean has(Class<? extends LGPower> powerClass) {
                return backingPlayer.getPowers().containsKey(powerClass);
            }

            @Override
            public boolean remove(Class<? extends LGPower> powerClass) {
                orchestrator.state().mustBe(LGGameState.STARTED);

                implicitCardPowers.remove(powerClass);

                return backingPlayer.getPowers().remove(powerClass) != null;
            }
        };
    }

    @Override
    public void changeCard(LGCard newCard) {
        if (newCard == getCard()) {
            return;
        }

        removeImplicitCardProperties();

        backingPlayer.setCard(newCard);

        addImplicitCardProperties(newCard);
    }

    private void removeImplicitCardProperties() {
        for (Class<? extends LGPower> powerClass : implicitCardPowers.keySet()) {
            backingPlayer.getPowers().remove(powerClass);
        }
        for (LGTeam team : implicitCardTeams) {
            backingPlayer.getTeams().remove(team);
        }

        implicitCardPowers.clear();
        implicitCardTeams.clear();
    }

    private void addImplicitCardProperties(LGCard card) {
        ImmutableSet<LGPower> cardPowers = card.createPowers();

        // Here we make sure that any explicit power are not
        // marked as implicit
        for (LGPower power : cardPowers) {
            if (!backingPlayer.getPowers().containsKey(power.getClass())) {
                implicitCardPowers.put(power.getClass(), power);
            }
        }

        // Same thing for teams
        ImmutableSet<LGTeam> cardTeams = card.getTeams();
        for (LGTeam team : cardTeams) {
            if (!backingPlayer.getTeams().contains(team)) {
                implicitCardTeams.add(team);
            }
        }

        // Now add them all in the player's properties.
        backingPlayer.getPowers().putAll(implicitCardPowers);
        backingPlayer.getTeams().addAll(implicitCardTeams);
    }

    @Override
    public boolean willDie() {
        return orchestrator.kills().pending().contains(this);
    }

    @Override
    public void die(LGKillCause cause) {
        orchestrator.kills().instantly(this, cause);
    }

    @Override
    public void dieLater(LGKillCause cause) {
        orchestrator.kills().pending().add(this, cause);
    }

    @Override
    public void cancelFutureDeath() {
        orchestrator.kills().pending().remove(this);
    }

    // Internal methods

    public void dieSilently() {
        if (backingPlayer.isDead()) {
            throw new IllegalStateException("This player is already dead."); // NANI???
        }
        backingPlayer.setDead(true);
    }

    public void goAway() {
        if (backingPlayer.isAway()) {
            throw new IllegalStateException("This player is already away.");
        }
        backingPlayer.setAway(true);
    }
}
