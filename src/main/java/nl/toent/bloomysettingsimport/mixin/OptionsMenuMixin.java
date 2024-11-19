package nl.toent.bloomysettingsimport.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import nl.toent.bloomysettingsimport.gui.importOptionsGui;
import nl.toent.bloomysettingsimport.gui.importOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(OptionsScreen.class)
public abstract class OptionsMenuMixin extends Screen {

    @Shadow protected abstract ButtonWidget createButton(Text message, Supplier<Screen> screenSupplier);

    protected OptionsMenuMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "init")
    private void addImportButton(CallbackInfo ci) {
        this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("settings.bloomysettingsimport.enter_btn"), button -> this.client.setScreen(new importOptionsScreen(new importOptionsGui())))
                        .dimensions( 5, 5, 150, 20)
                        .build()
        );
    }
}