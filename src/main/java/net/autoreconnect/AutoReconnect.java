package net.autoreconnect;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoReconnect implements ModInitializer
{
	public static Config config;

	public static int attempt = 0;
	public static boolean connect = false;

	private static final AtomicInteger countdown = new AtomicInteger();
	private static Timer timer = null;

	@Override
	public void onInitialize() {
		loadConfig();
	}

	public static void startCountdown(int seconds)
	{
		countdown.set(seconds);
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask()
		{
			@Override
			public void run()
			{
				if (countdown.decrementAndGet() <= 0)
				{
					connect = true;
					cancel();
				}
			}
		}, 1000, 1000);
	}

	private static void cancel()
	{
		if (timer == null) return;
		timer.cancel();
		timer = null;
	}

	public static void reset()
	{
		cancel();
		attempt = 0;
		connect = false;
	}

	public static void loadConfig(){
		try {
			config = Config.getInstance(new File(FabricLoader.INSTANCE.getConfigDir().toFile(),"autoreconnect.yaml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getCountdown()
	{
		return countdown.get();
	}
}
