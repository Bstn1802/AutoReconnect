package autoreconnect.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;

@Config(name = "autoreconnect")
public class ModConfig implements ConfigData {
    @Comment("Delays between attempts in seconds")
    private List<Integer> delays;
    @Comment("Whether to repeat reconnecting with the last configured delay")
    private boolean infinite;
    @Comment("Messages to send after a successful automatic reconnect")
    @CollapsibleObject
    private AutoMessages autoMessages;

    @Override
    public void validatePostLoad() {
        if (delays == null) delays = List.of(3, 10, 30, 60);
        else if(!delays.isEmpty()) delays.removeIf(i -> i <= 0);
        if (autoMessages == null) autoMessages = new AutoMessages();
        if (autoMessages.name == null) autoMessages.name = "";
        if (autoMessages.messages == null) autoMessages.messages = emptyList();
        else if(!autoMessages.messages.isEmpty()) autoMessages.messages.removeIf(Objects::isNull);
        if (autoMessages.delay <= 0) autoMessages.delay = 1000;
    }

    public int getDelayForAttempt(int attempt) throws IndexOutOfBoundsException {
        if (attempt < delays.size()) return delays.get(attempt);
        if (infinite) return delays.get(delays.size() - 1); // repeat last
        throw new IndexOutOfBoundsException();
    }

    public boolean hasAttempts() {
        return !delays.isEmpty();
    }

    public AutoMessages getAutoMessages() {
        return autoMessages;
    }

    public static final class AutoMessages {
        @Comment("Name of the singleplayer world or the server")
        private String name;
        @Comment("Messages to be sent after reconnecting")
        private List<String> messages;
        @Comment("Delay between each message in milliseconds")
        private int delay;

        public String getName() {
            return name;
        }

        public Iterator<String> getMessages() {
            return messages.iterator();
        }

        public int getDelay() {
            return delay;
        }
    }
}
