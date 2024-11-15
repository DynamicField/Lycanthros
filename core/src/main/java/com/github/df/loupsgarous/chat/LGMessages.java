package com.github.df.loupsgarous.chat;

import static com.github.df.loupsgarous.chat.LGChatStuff.error;
import static com.github.df.loupsgarous.chat.LGChatStuff.player;

public final class LGMessages {
    public static final String NOT_IN_GAME = "&cVous n'êtes pas en partie.";

    private LGMessages() {
    }

    public static String cannotFindPlayer(String playerName) {
        return error("Impossible de trouver le joueur \"") + player(playerName) + error("\".");
    }
}
