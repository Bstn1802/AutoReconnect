package autoreconnect.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.Screen;

import java.util.Arrays;

public interface ScreenChangedListener {
    Event<ScreenChangedListener> EVENT = EventFactory.createArrayBacked(ScreenChangedListener.class, listeners ->
        (current, next) -> Arrays.stream(listeners).forEach(listener -> listener.onScreenChanged(current, next)));

    void onScreenChanged(Screen current, Screen next);
}