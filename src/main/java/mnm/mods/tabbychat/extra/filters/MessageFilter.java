package mnm.mods.tabbychat.extra.filters;

import mnm.mods.tabbychat.ChatManager;
import mnm.mods.tabbychat.TabbyChatClient;
import mnm.mods.tabbychat.api.Channel;
import mnm.mods.tabbychat.api.filters.Filter;
import mnm.mods.tabbychat.api.filters.FilterEvent;
import mnm.mods.tabbychat.util.MessagePatterns;

import java.util.regex.Pattern;
import javax.annotation.Nonnull;

/**
 * Base class for filters that just need to set the
 */
public class MessageFilter implements Filter {

    private final ChatManager chat = TabbyChatClient.getInstance().getChat();

    @Nonnull
    @Override
    public Pattern getPattern() {

        MessagePatterns messege = TabbyChatClient.getInstance().getServerSettings().general.messegePattern.get();
        String pattern = String.format("(?:%s|%s)", messege.getOutgoing(), messege.getIncoming());
        return Pattern.compile(pattern);
    }

    @Override
    public void action(FilterEvent event) {

        if (TabbyChatClient.getInstance().getServerSettings().general.pmEnabled.get()) {
            // 0 = whole message, 1 = outgoing recipient, 2 = incoming recipient
            String player = event.matcher.group(1);
            // For when it's an incoming message.
            if (player == null) {
                player = event.matcher.group(2);
            }
            Channel dest = chat.getChannel(player, true);
            event.channels.add(dest);
        }
    }
}
