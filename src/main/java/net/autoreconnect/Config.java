package net.autoreconnect;

import com.google.gson.Gson;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;

import java.io.*;

public class Config {
    public int rejoinTime[];
    public static Config getInstance(File file) throws IOException {
        Gson gson = new Gson();
        if(file.exists()) {
            FileReader reader=new FileReader(file);
            Config config = gson.fromJson(reader,Config.class);
            reader.close();
            return config;
        }
        else{
            Config newCfg=new Config();
            Writer writer=new FileWriter(file);
            String s = gson.toJson(newCfg, Config.class);
            System.out.println(s);
            writer.write(s);
            writer.close();
            return newCfg;
        }
    }
    private Config(){
        rejoinTime= new int[]{3, 10, 60, 300};
    }
}
