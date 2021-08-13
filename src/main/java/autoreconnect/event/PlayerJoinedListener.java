package autoreconnect.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.Arrays;

public interface PlayerJoinedListener {
    Event<PlayerJoinedListener> EVENT = EventFactory.createArrayBacked(PlayerJoinedListener.class, listeners ->
        player -> Arrays.stream(listeners).forEach(listener -> listener.onPlayerJoined(player)));

    void onPlayerJoined(ClientPlayerEntity player);
}