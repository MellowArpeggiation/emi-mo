package net.mellow.emimo.mixin.emi;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStackInteraction;
import dev.emi.emi.input.EmiBind;
import dev.emi.emi.screen.EmiScreenManager;
import net.mellow.emimo.ItemPuller;
import net.mellow.emimo.KeyBinds;

@Mixin(EmiScreenManager.class)
public class EmiScreenManagerMixin {

    @Inject(method = "stackInteraction", at = @At("HEAD"), remap = false)
    private static void onStackInteraction(EmiStackInteraction stack, Function<EmiBind, Boolean> function, CallbackInfoReturnable<Boolean> cir) {
        EmiIngredient ingredient = stack.getStack();

        EmiBind fakeBind = new EmiBind("key.emi.pull_item", KeyBinds.PULL_ITEMS.get().getKey().getValue());

        if(function.apply(fakeBind)) {
            ItemPuller.pullItem(ingredient);
        }
    }

}
