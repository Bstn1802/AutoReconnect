package net.autoreconnect;

import net.minecraft.client.resource.language.I18n;

public class Lang {
    public static String reloadConfig(){
        return I18n.translate("gui.autoreconnect.reload_config");
    }
    public static String error(){
        return I18n.translate("gui.autoreconnect.error");
    }
    public static String reconnectIn(){
        return I18n.translate("gui.autoreconnect.reconnect_in");
    }
}
