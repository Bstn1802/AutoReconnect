package autoreconnect.reconnect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;

public class RealmsReconnectHandler extends ReconnectHandler {
    private final RealmsServer realmsServer;

    public RealmsReconnectHandler(RealmsServer realmsServer) {
        this.realmsServer = realmsServer;
    }

    @Override
    public String getName() {
        return realmsServer.getName();
    }

    @Override
    public void reconnect() {
        RealmsMainScreen realmsMainScreen = new RealmsMainScreen(new TitleScreen());
        realmsMainScreen.init(MinecraftClient.getInstance(), 0, 0); // init but don't set screen!
        realmsMainScreen.play(realmsServer, realmsMainScreen);
    }
}
