package autoreconnect.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ServerInfo;

import java.util.Arrays;

public interface ServerEntryChangedListener {
    Event<ServerEntryChangedListener> EVENT = EventFactory.createArrayBacked(ServerEntryChangedListener.class,
        listeners -> entry -> Arrays.stream(listeners).forEach(listener -> listener.onServerEntryChanged(entry)));

    void onServerEntryChanged(ServerInfo entry);
}