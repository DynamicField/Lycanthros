package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.cards.UnknownCard;
import com.github.jeuxjeux20.loupsgarous.kill.causes.LGKillCause;
import com.github.jeuxjeux20.loupsgarous.powers.LGPower;
import com.github.jeuxjeux20.loupsgarous.powers.PowerRegistry;
import com.github.jeuxjeux20.loupsgarous.tags.LGTag;
import com.github.jeuxjeux20.loupsgarous.tags.TagRegistry;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeam;
import com.github.jeuxjeux20.loupsgarous.teams.TeamRegistry;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MutableClassToInstanceMap;
import me.lucko.helper.metadata.MetadataMap;

import java.util.*;

public class OrchestratedLGPlayer implements LGPlayer {
    private final UUID playerUUID;
    private final Set<LGTag> tags = new HashSet<>();
    private final Set<LGTeam> teams = new HashSet<>();
    private final ClassToInstanceMap<LGPower> powers = MutableClassToInstanceMap.create();
    private LGCard card = UnknownCard.INSTANCE;
    private boolean isDead;
    private boolean isAway;
    private final MetadataMap metadataMap = MetadataMap.create();

    private final LGGameOrchestrator orchestrator;

    private final Set<LGTeam> implicitCardTeams = new HashSet<>();
    private final ClassToInstanceMap<LGPower> implicitCardPowers = MutableClassToInstanceMap.create();

    OrchestratedLGPlayer(UUID playerUUID, LGGameOrchestrator orchestrator) {
        this.playerUUID = playerUUID;
        this.orchestrator = orchestrator;
    }

    @Override
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @Override
    public LGCard getCard() {
        return card;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    @Override
    public boolean isAway() {
        return isAway;
    }

    @Override
    public MetadataMap metadata() {
        return metadataMap;
    }

    @Override
    public TeamRegistry teams() {
        return new TeamRegistry() {
            @Override
            public ImmutableSet<LGTeam> get() {
                return ImmutableSet.copyOf(teams);
            }

            @Override
            public boolean add(LGTeam item) {
                orchestrator.getState().mustBe(LGGameState.STARTED);

                // Mark our team as explicit.
                implicitCardTeams.remove(item);

                return teams.add(item);
            }

            @Override
            public boolean has(LGTeam item) {
                return teams.contains(item);
            }

            @Override
            public boolean remove(LGTeam item) {
                orchestrator.getState().mustBe(LGGameState.STARTED);

                implicitCardTeams.remove(item);

                return teams.remove(item);
            }
        };
    }

    @Override
    public TagRegistry tags() {
        return new TagRegistry() {
            @Override
            public ImmutableSet<LGTag> get() {
                return ImmutableSet.copyOf(tags);
            }

            @Override
            public boolean add(LGTag item) {
                orchestrator.getState().mustBe(LGGameState.STARTED);

                return tags.add(item);
            }

            @Override
            public boolean has(LGTag item) {
                return tags.contains(item);
            }

            @Override
            public boolean remove(LGTag item) {
                orchestrator.getState().mustBe(LGGameState.STARTED);

                return tags.remove(item);
            }
        };
    }

    @Override
    public PowerRegistry powers() {
        return new PowerRegistry() {
            @Override
            public ImmutableClassToInstanceMap<LGPower> get() {
                return ImmutableClassToInstanceMap.copyOf(powers);
            }

            @Override
            public <T extends LGPower> Optional<T> get(Class<T> powerClass) {
                return Optional.ofNullable(powers.getInstance(powerClass));
            }

            @Override
            public <T extends LGPower> T getOrThrow(Class<T> powerClass) {
                T instance = powers.getInstance(powerClass);
                if (instance == null) {
                    throw new NoSuchElementException("No power " + powerClass + " found.");
                }
                return instance;
            }

            @Override
            public void put(LGPower power) {
                orchestrator.getState().mustBe(LGGameState.STARTED);

                implicitCardPowers.remove(power.getClass());

                powers.put(power.getClass(), power);
            }

            @Override
            public boolean has(Class<? extends LGPower> powerClass) {
                return powers.containsKey(powerClass);
            }

            @Override
            public boolean remove(Class<? extends LGPower> powerClass) {
                orchestrator.getState().mustBe(LGGameState.STARTED);

                implicitCardPowers.remove(powerClass);

                return powers.remove(powerClass) != null;
            }
        };
    }

    @Override
    public void setCard(LGCard card) {
        if (card == getCard()) {
            return;
        }

        removeImplicitCardProperties();

        this.card = card;

        addImplicitCardProperties(card);
    }

    private void removeImplicitCardProperties() {
        for (Class<? extends LGPower> powerClass : implicitCardPowers.keySet()) {
            powers.remove(powerClass);
        }
        for (LGTeam team : implicitCardTeams) {
            teams.remove(team);
        }

        implicitCardPowers.clear();
        implicitCardTeams.clear();
    }

    private void addImplicitCardProperties(LGCard card) {
        ImmutableSet<LGPower> cardPowers = card.createPowers();

        // Here we make sure that any explicit power are not
        // marked as implicit
        for (LGPower power : cardPowers) {
            if (!powers.containsKey(power.getClass())) {
                implicitCardPowers.put(power.getClass(), power);
            }
        }

        // Same thing for teams
        ImmutableSet<LGTeam> cardTeams = card.getTeams();
        for (LGTeam team : cardTeams) {
            if (!teams.contains(team)) {
                implicitCardTeams.add(team);
            }
        }

        // Now add them all in the player's properties.
        powers.putAll(implicitCardPowers);
        teams.addAll(implicitCardTeams);
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
        if (isDead) {
            throw new IllegalStateException("This player is already dead."); // NANI???
        }
        isDead = true;
    }

    public void goAway() {
        if (isAway) {
            throw new IllegalStateException("This player is already away.");
        }
        isAway = true;
    }
}
