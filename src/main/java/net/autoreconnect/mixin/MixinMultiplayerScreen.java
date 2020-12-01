package net.autoreconnect.mixin;

import net.autoreconnect.GUIMaker;
import net.autoreconnect.Lang;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.autoreconnect.AutoReconnect.*;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen extends Screen {
    protected MixinMultiplayerScreen(Text title) {
        super(title);
    }
    @Inject(at = @At("RETURN"), method = "init")
    protected void init(CallbackInfo info){
        ButtonWidget button=GUIMaker.CreateImageButton(this.width / 2 + 158,this.height - 52,"",Lang.reloadConfig(),"autoreconnect","textures/gui/update_icon.png",(w)->{
            loadConfig();
        });
        addButton(button);
    }
}
