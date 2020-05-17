package com.github.df.loupsgarous.tags;

import com.github.df.loupsgarous.mechanic.RevelationRequest;
import org.bukkit.ChatColor;

public final class MaireTag extends LGTag {
    public static final MaireTag INSTANCE = new MaireTag();

    private MaireTag() {
    }

    @Override
    public String getName() {
        return "Maire";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.BLUE;
    }

    @Override
    protected boolean isRevealed(RevelationRequest<LGTag> request) {
        return true;
    }
}
