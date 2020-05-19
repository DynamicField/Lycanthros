package com.github.jeuxjeux20.loupsgarous.commands;

import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.jeuxjeux20.guicybukkit.command.SelfConfiguredCommandExecutor;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.MutableComposition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.gui.CompositionGui;
import com.google.inject.Inject;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

@CommandName("guitest")
public class GuiTestCommand extends SelfConfiguredCommandExecutor {
    private final CompositionGui.Factory compositionGuiFactory;

    @Inject
    public GuiTestCommand(CompositionGui.Factory compositionGuiFactory) {
        this.compositionGuiFactory = compositionGuiFactory;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        MutableComposition composition = new MutableComposition(1, Collections.emptyList());
        CompositionGui compositionGui = compositionGuiFactory.create((Player) sender, composition);
        compositionGui.open();
        return true;
    }
}
