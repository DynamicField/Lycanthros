package com.github.jeuxjeux20.loupsgarous;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.error;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;

public final class LGMessages {
    private LGMessages() {
    }

    public static String cannotFindPlayer(String playerName) {
        return error("Impossible de trouver le joueur \"") + player(playerName) + error("\".");
    }
}
