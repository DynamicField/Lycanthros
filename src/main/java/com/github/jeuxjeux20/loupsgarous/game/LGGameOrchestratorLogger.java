package com.github.jeuxjeux20.loupsgarous.game;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LGGameOrchestratorLogger extends Logger {
    private final String prefix;

    public LGGameOrchestratorLogger(LGGameOrchestrator orchestrator) {
        super(orchestrator.getClass().getCanonicalName(), null);
        prefix = "[LoupsGarous] (Game " +  orchestrator.game().getId() + ") ";
        setParent(orchestrator.plugin().getLogger());
        setLevel(Level.ALL);
    }

    @Override
    public void log(LogRecord record) {
        record.setMessage(prefix + record.getMessage());
        super.log(record);
    }
}
