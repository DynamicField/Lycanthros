package com.github.jeuxjeux20.loupsgarous.chat;

import java.util.Collection;

public interface ChatChannelViewTransformer {
    boolean handlesChannel(ChatChannel channel);

    void transform(ChatContext context, ChatChannelView view);

    static void runAll(Collection<ChatChannelViewTransformer> transformers,
                       ChatContext context, ChatChannelView properties) {
        for (ChatChannelViewTransformer processor : transformers) {
            if (processor.handlesChannel(properties.getChatChannel())) {
                processor.transform(context, properties);
            }
        }
    }
}
