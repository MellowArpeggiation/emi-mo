package net.mellow.emimo.mixin.emi;

import java.util.function.Function;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStackInteraction;
import dev.emi.emi.input.EmiBind;
import dev.emi.emi.screen.EmiScreenManager;
import net.mellow.emimo.ItemPuller;

@Mixin(EmiScreenManager.class)
public class EmiScreenManagerMixin {

    private static EmiBind pullItem = new EmiBind("key.emi.pull_item", GLFW.GLFW_KEY_V);

    @Inject(method = "stackInteraction", at = @At("HEAD"), remap = false)
    private static void onStackInteraction(EmiStackInteraction stack, Function<EmiBind, Boolean> function, CallbackInfoReturnable<Boolean> cir) {
        EmiIngredient ingredient = stack.getStack();
        if(function.apply(pullItem)) {
            ItemPuller.pullItem(ingredient);
        }
    }

}
