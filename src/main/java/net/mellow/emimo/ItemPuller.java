package net.mellow.emimo;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.runtime.EmiFavorite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ItemPuller {

    public static void pullItem(EmiIngredient stack) {
        if (stack.isEmpty() || !(stack instanceof EmiFavorite)) return;

        long toPull = 1;
        if (stack instanceof EmiFavorite.Synthetic synthetic) {
            toPull = synthetic.amount;
        }

        Minecraft client = Minecraft.getInstance();
        MultiPlayerGameMode manager = client.gameMode;
        Player player = client.player;
        AbstractContainerMenu screenHandler = player.containerMenu;

        List<ItemStack> searchStacks = stack.getEmiStacks().stream().map(s -> s.getItemStack()).collect(Collectors.toList());

        // // Attempt to pull using any registered custom pullers, and stop on a successful pull
        // if (EmiStackPullers.attemptPull(screenHandler, searchStacks, toPull)) return;

        for (ItemStack searchStack : searchStacks) {

            // Sweep through all the non-player inventory slots
            for (Slot inventorySlot : screenHandler.slots) {
                if (inventorySlot.container instanceof Inventory || !inventorySlot.hasItem() || !inventorySlot.mayPickup(player)) continue;
                if (!ItemStack.isSameItem(searchStack, inventorySlot.getItem())) continue;

                ItemStack fromStack = inventorySlot.getItem().copy();
                int remaining = fromStack.getCount();

                // And attempt to smoosh it into the player inventory
                for (Slot playerSlot : getQuickMoveDestinationSlots(screenHandler.slots, fromStack)) {
                    if (playerSlot.hasItem() && !ItemStack.isSameItemSameTags(fromStack, playerSlot.getItem())) continue;

                    ItemStack playerStack = playerSlot.getItem();

                    int maxTransfer = fromStack.getMaxStackSize() - playerStack.getCount();
                    int amountToTransfer = (int) Math.min(maxTransfer, toPull);

                    manager.handleInventoryMouseClick(screenHandler.containerId, inventorySlot.index, 0, ClickType.PICKUP, player);

                    if (remaining <= amountToTransfer) {
                        manager.handleInventoryMouseClick(screenHandler.containerId, playerSlot.index, 0, ClickType.PICKUP, player);
                        toPull -= remaining;
                        remaining = 0;
                    } else {
                        while (amountToTransfer > 0) {
                            manager.handleInventoryMouseClick(screenHandler.containerId, playerSlot.index, 1, ClickType.PICKUP, player);
                            toPull--;

                            amountToTransfer--;
                            remaining--;
                        }

                        // put that thing back where it came from, or so help me...!
                        manager.handleInventoryMouseClick(screenHandler.containerId, inventorySlot.index, 0, ClickType.PICKUP, player);
                    }

                    if (toPull <= 0) return;
                    if (remaining <= 0) break;
                }
            }
        }
    }

    private static List<Slot> getQuickMoveDestinationSlots(List<Slot> slots, ItemStack stackToMove) {
        List<Slot> destinationSlots = Lists.newArrayList();
        for (Slot candidateSlot : slots) {
            if (candidateSlot.container instanceof Inventory && candidateSlot.mayPlace(stackToMove)) {
                destinationSlots.add(candidateSlot);
            }
        }

        // Sort such that we fill existing stacks first where possible
        destinationSlots.sort((a, b) -> Boolean.compare(b.hasItem(), a.hasItem()));
        return destinationSlots;
    }

}
