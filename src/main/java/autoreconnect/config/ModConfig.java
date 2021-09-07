package autoreconnect.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.List;

import static java.util.Collections.emptyList;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection", "unused"})
@Config(name = "autoreconnect")
public class ModConfig implements ConfigData {
    @Comment("Delays between attempts in seconds")
    private List<Integer> delays;
    @Comment("Whether to repeat reconnecting with the last configured delay")
    private boolean infinite;
    @Comment("Messages to send after joining the world on a specific server")
    @CollapsibleObject
    private ServerMessages messages;

    @Override
    public void validatePostLoad() {
        if (delays == null || delays.isEmpty() || delays.stream().anyMatch(i -> i <= 0)) delays = List.of(3, 10, 30, 60);
        if (messages == null) messages = new ServerMessages();
        if (messages.serverAddress == null) messages.serverAddress = "localhost";
        if (messages.messages == null) messages.messages = emptyList();
        if (messages.delay <= 0) messages.delay = 1000;
    }

    public int[] getDelays() {
        return delays.stream().mapToInt(Integer::intValue).toArray();
    }

    public boolean isInfinite() {
        return infinite;
    }

    public ServerMessages getServerMessages() {
        return messages;
    }

    public static final class ServerMessages {
        private String serverAddress;
        private List<String> messages;
        @Comment("Delay between each message in milliseconds")
        private int delay;

        public String getServerAddress() {
            return serverAddress;
        }

        public String[] getMessages() {
            return messages.toArray(String[]::new);
        }

        public int getDelay() {
            return delay;
        }
    }
}
