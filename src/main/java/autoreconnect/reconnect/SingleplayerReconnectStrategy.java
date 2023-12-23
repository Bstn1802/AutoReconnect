package autoreconnect.reconnect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.text.Text;

public class SingleplayerReconnectStrategy extends ReconnectStrategy {
    private final String worldName;

    public SingleplayerReconnectStrategy(String worldName) {
        this.worldName = worldName;
    }

    @Override
    public String getName() {
        return worldName;
    }

    /**
     * @see net.minecraft.client.QuickPlay#startSingleplayer(MinecraftClient, String)
     */
    @Override
    public void reconnect() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!client.getLevelStorage().levelExists(getName())) return;
        client.setScreenAndRender(new MessageScreen(Text.translatable("selectWorld.data_read")));
        client.createIntegratedServerLoader().start(getName(), () -> {});
        /*
         * The method signature for the IntegratedServerLoader.start() method was changed in either 1.20.3 or 1.20.4
         * So as of now the onCancel runnable is just a lambda function as I am not sure what the runnable should do
         * (maybe return the player to the title screen?)
         * The runnable seems to be run whenever an exception is encountered while running the start() method and the world
         * cannot be "recovered" (not too sure what that means)
         */
    }
}
