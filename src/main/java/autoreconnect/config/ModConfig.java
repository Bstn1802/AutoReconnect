package autoreconnect.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.List;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection"})
@Config(name = "autoreconnect")
public class ModConfig implements ConfigData {
    @Comment("Delays between attempts in seconds")
    private List<Integer> delays = List.of(3, 10, 30, 60);
    @Comment("Whether to repeat reconnecting with the last configured delay")
    private boolean infinite = false;
    @Comment("Messages to send after joining the world on a specific server")
    @CollapsibleObject
    private ServerMessages messages = new ServerMessages();

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
        private String serverAddress = "";
        private List<String> messages = List.of();
        @Comment("Delay between each message in milliseconds")
        private int delay = 1000;

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