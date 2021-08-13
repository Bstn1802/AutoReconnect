package autoreconnect.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;

import java.util.Arrays;

public interface DisconnectListener {
    Event<DisconnectListener> EVENT = EventFactory.createArrayBacked(DisconnectListener.class, listeners ->
        reason -> Arrays.stream(listeners).forEach(listener -> listener.onDisconnected(reason)));

    void onDisconnected(Text reason);
}