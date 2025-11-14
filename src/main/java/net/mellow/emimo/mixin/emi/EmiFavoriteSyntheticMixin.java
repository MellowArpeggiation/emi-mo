package net.mellow.emimo.mixin.emi;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.emi.emi.runtime.EmiFavorite;
import net.mellow.emimo.KeyBinds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.InventoryMenu;

@Mixin(EmiFavorite.Synthetic.class)
public class EmiFavoriteSyntheticMixin {

    @Inject(method = "getTooltip", at = @At("RETURN"), remap = false)
    private void onGetTooltip(CallbackInfoReturnable<List<ClientTooltipComponent>> cir) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player.containerMenu instanceof InventoryMenu) return;
        if(mc.player.containerMenu instanceof CreativeModeInventoryScreen.ItemPickerMenu) return;

        List<ClientTooltipComponent> list = cir.getReturnValue();
        Font font = mc.font;
        List<FormattedCharSequence> tooltip = font.split(Component.translatable("emimo.pullItemsHint", KeyBinds.PULL_ITEMS.get().getTranslatedKeyMessage().plainCopy().withStyle(ChatFormatting.GOLD)), 200); // longest line prize
        for(FormattedCharSequence seq : tooltip) {
            list.add(ClientTooltipComponent.create(seq));
        }
    }

}
