package net.autoreconnect;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class GUIMaker {
    public static TexturedButtonWidget CreateImageButton(int x, int y, String translateText, String tooltip,Identifier id, TexturedButtonWidget.PressAction action){
        TexturedButtonWidget buttonWidget=new TexturedButtonWidget(x, y, 20, 20, 20,20, 20 ,id, 20, 40, action, new TranslatableText(translateText, new Object[0]));
        return buttonWidget;
    }
    public static TexturedButtonWidget CreateImageButton(int x, int y, String translateText, String tooltip,String idNS,String idPath, ButtonWidget.PressAction action){
        Identifier id=new Identifier(idNS,idPath);
        TexturedButtonWidget buttonWidget=CreateImageButton(x,y,translateText,tooltip,id,action);
        return buttonWidget;
    }
}
