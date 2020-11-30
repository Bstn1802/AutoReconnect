package net.autoreconnect;

import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import org.yaml.snakeyaml.Yaml;

import java.io.*;

public class Config {
    public int rejoinTime[];
    public static Config getInstance(File file) throws IOException {
        Yaml yaml = new Yaml();
        if(file.exists()) {
            Config config = yaml.load(new FileInputStream(file));
            return config;
        }
        else{
            Config newCfg=new Config();
            Writer writer=new FileWriter(file);
            yaml.dump(newCfg,writer);
            return newCfg;
        }
    }
    private Config(){
        rejoinTime= new int[]{3, 10, 60, 300};
    }
}
