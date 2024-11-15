package com.github.df.loupsgarous.game;

import com.github.df.loupsgarous.cards.LGCard;
import com.github.df.loupsgarous.cards.UnknownCard;
import com.github.df.loupsgarous.kill.causes.LGKillCause;
import com.github.df.loupsgarous.powers.LGPower;
import com.github.df.loupsgarous.powers.PowerRegistry;
import com.github.df.loupsgarous.storage.MapStorage;
import com.github.df.loupsgarous.storage.Storage;
import com.github.df.loupsgarous.tags.LGTag;
import com.github.df.loupsgarous.tags.TagRegistry;
import com.github.df.loupsgarous.teams.LGTeam;
import com.github.df.loupsgarous.teams.TeamRegistry;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MutableClassToInstanceMap;

import java.util.*;

public class OrchestratedLGPlayer implements LGPlayer {
    private final UUID playerUUID;
    private final Set<LGTag> tags = new HashSet<>();
    private final Set<LGTeam> teams = new HashSet<>();
    private final ClassToInstanceMap<LGPower> powers = MutableClassToInstanceMap.create();
    private LGCard card = UnknownCard.INSTANCE;
    private boolean isDead;
    private boolean isAway;
    private final Storage storage = new MapStorage();

    private final LGGameOrchestrator orchestrator;

    private final Set<LGTeam> implicitCardTeams = new HashSet<>();

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

            @Override
            public boolean isRevealed(LGTeam item, LGPlayer viewer) {
                return item.isRevealed(orchestrator, OrchestratedLGPlayer.this, viewer);
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

            @Override
            public boolean isRevealed(LGTag item, LGPlayer viewer) {
                return item.isRevealed(orchestrator, OrchestratedLGPlayer.this, viewer);
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

                powers.put(power.getClass(), power);
            }

            @Override
            public boolean has(Class<? extends LGPower> powerClass) {
                return powers.containsKey(powerClass);
            }

            @Override
            public boolean remove(Class<? extends LGPower> powerClass) {
                orchestrator.getState().mustBe(LGGameState.STARTED);

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
        for (Map.Entry<Class<? extends LGPower>, LGPower> entry : powers.entrySet()) {
            LGPower power = entry.getValue();
            Class<? extends LGPower> key = entry.getKey();

            if (power.getSource() == card) {
                powers.remove(key);
            }
        }
        for (LGTeam team : implicitCardTeams) {
            teams.remove(team);
        }

        implicitCardTeams.clear();
    }

    private void addImplicitCardProperties(LGCard card) {
        ImmutableSet<LGPower> cardPowers = card.createPowers();
        for (LGPower power : cardPowers) {
            powers.put(power.getClass(), power);
        }

        ImmutableSet<LGTeam> cardTeams = card.getTeams();
        for (LGTeam team : cardTeams) {
            if (!teams.contains(team)) {
                implicitCardTeams.add(team);
            }
        }

        // Now add them all in the player's properties.
        teams.addAll(implicitCardTeams);
    }

    @Override
    public boolean isGoingToDie() {
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

    @Override
    public LGGameOrchestrator getOrchestrator() {
        return orchestrator;
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

    @Override
    public Storage getStorage() {
        return storage;
    }
}
