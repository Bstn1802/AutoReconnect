package autoreconnect.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Arrays;

public interface DisconnectScreenRenderListener {
    Event<DisconnectScreenRenderListener> EVENT = EventFactory.createArrayBacked(DisconnectScreenRenderListener.class, listeners ->
        (matrices, reasonHeight) -> Arrays.stream(listeners).forEach(listener -> listener.onRender(matrices, reasonHeight)));

    void onRender(MatrixStack matrices, int reasonHeight);
}