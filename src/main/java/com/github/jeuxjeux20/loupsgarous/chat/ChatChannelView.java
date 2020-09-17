package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

import java.util.ArrayList;
import java.util.List;

public final class ChatChannelView {
    private final ChatChannel chatChannel;
    private final LGPlayer viewer;
    private String name;
    private boolean isReadable = false;
    private boolean isWritable = false;
    private boolean isNameDisplayed = false;
    private boolean isSenderAnonymized = false;
    private final List<String> anonymizedNames = new ArrayList<>();

    public ChatChannelView(ChatChannel chatChannel, LGPlayer viewer) {
        this.chatChannel = chatChannel;
        this.viewer = viewer;
        this.name = chatChannel.getDefaultName();
    }

    public LGPlayer getViewer() {
        return viewer;
    }

    public LGGameOrchestrator getOrchestrator() {
        return getViewer().getOrchestrator();
    }

    public boolean isReadable() {
        return isReadable;
    }

    public void setReadable(boolean readable) {
        isReadable = readable;
    }

    public boolean isWritable() {
        return isWritable;
    }

    public void setWritable(boolean writable) {
        isWritable = writable;
    }

    public void makeFullyAccessible() {
        isReadable = true;
        isWritable = true;
    }

    public boolean isNameDisplayed() {
        return isNameDisplayed;
    }

    public void setNameDisplayed(boolean nameDisplayed) {
        isNameDisplayed = nameDisplayed;
    }

    public boolean isSenderAnonymized() {
        return isSenderAnonymized;
    }

    public void setSenderAnonymized(boolean senderAnonymized) {
        isSenderAnonymized = senderAnonymized;
    }

    public List<String> getAnonymizedNames() {
        return anonymizedNames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChatChannel getChatChannel() {
        return chatChannel;
    }

    public void mergePermissionsFrom(ChatChannelView properties) {
        this.isWritable = properties.isWritable;
        this.isReadable = properties.isReadable;
    }
}
