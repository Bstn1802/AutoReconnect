package autoreconnect.reconnect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.TitleScreen;
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
        client.createIntegratedServerLoader().start(getName(), () -> client.setScreen(new TitleScreen()));
    }
}
