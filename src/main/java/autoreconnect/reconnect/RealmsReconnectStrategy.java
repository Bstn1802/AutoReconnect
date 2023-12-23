package autoreconnect.reconnect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.task.OpenServerTask;

//import java.util.concurrent.locks.ReentrantLock;

public class RealmsReconnectStrategy extends ReconnectStrategy {
    private final RealmsServer realmsServer;

    public RealmsReconnectStrategy(RealmsServer realmsServer) {
        this.realmsServer = realmsServer;
    }

    @Override
    public String getName() {
        return realmsServer.getName();
    }

    /**
     * @see net.minecraft.client.QuickPlay#startRealms(MinecraftClient, RealmsClient, String)
     */
    @Override
    public void reconnect() {
        TitleScreen titleScreen = new TitleScreen();
        OpenServerTask realmsGetServerDetailsTask = new OpenServerTask(realmsServer, new RealmsMainScreen(titleScreen), true, MinecraftClient.getInstance());
        MinecraftClient.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen(titleScreen, realmsGetServerDetailsTask));
        /*
         * The RealmsGetServerDetailsTask seems to have been removed in either 1.20.3 or 1.20.4
         * The 2 Tasks that seemed to fit the most were OpenServerTask and RealmsPrepareConnectionTask
         * I looked into the OpenServerTask, and saw that if the boolean join was true, 
         * it would execute a play method in the RealmsMainScreen class that in turn runs the RealmsPrepareConnectionTask
         * Hence I decided to use the OpenServerTask for now
         */
    }
}
