package ca.bertsa.eatwithease.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class KeyEventHandler {
    private static final int LAST_HOTBAR_SLOT_INDEX = 8;
    private static final int PLAYER_INVENTORY_SLOT_COUNT_WITHOUT_EQUIPMENT_AND_CRAFTING_SLOTS = 36;

    protected final MinecraftClient client;

    private Integer lastItemSwappedSlot;

    public KeyEventHandler() {
        setLastItemSwappedSlot(null);
        client = MinecraftClient.getInstance();
    }

    public abstract void handleKeyReleased();

    public abstract void handleKeyPressed();

    private void setLastItemSwappedSlot(Integer lastItemSwappedSlot) {
        this.lastItemSwappedSlot = lastItemSwappedSlot;
    }

    protected void swapStacksBack() {
        swapStacks(lastItemSwappedSlot);
    }

    protected void swapStacks(int itemSwappedSlot) {
        ClientPlayerEntity player = client.player;
        ClientPlayerInteractionManager interactionManager = client.interactionManager;

        if (player == null || interactionManager == null) {
            return;
        }

        setLastItemSwappedSlot(itemSwappedSlot);

        int selectedSlot = getSlotIndex(player.getInventory().selectedSlot);

        if (selectedSlot == PLAYER_INVENTORY_SLOT_COUNT_WITHOUT_EQUIPMENT_AND_CRAFTING_SLOTS) {
            interactionManager.clickSlot(0, selectedSlot, 0, SlotActionType.SWAP, player);
            interactionManager.clickSlot(0, itemSwappedSlot, 0, SlotActionType.SWAP, player);
            interactionManager.clickSlot(0, selectedSlot, 0, SlotActionType.SWAP, player);
        } else {
            interactionManager.clickSlot(0, itemSwappedSlot, 0, SlotActionType.SWAP, player);
            interactionManager.clickSlot(0, selectedSlot, 0, SlotActionType.SWAP, player);
            interactionManager.clickSlot(0, itemSwappedSlot, 0, SlotActionType.SWAP, player);
        }
    }

    protected List<ItemWithSlot> getListOfMatchingItems(Function<ItemStack, Boolean> filter) {
        ClientPlayerEntity player = client.player;
        List<ItemWithSlot> list = new ArrayList<>();

        if (player == null) {
            return list;
        }

        Inventory inventory = Objects.requireNonNull(player).getInventory();
        for (int slot = 0; slot < inventory.size(); slot++) {
            final ItemStack itemStack = inventory.getStack(slot);
            if (filter.apply(itemStack)) {
                list.add(new ItemWithSlot(itemStack, getSlotIndex(slot)));
            }
        }
        return list;
    }

    protected ItemWithSlot getFirstMatchingItem(Function<ItemStack, Boolean> filter) {
        ClientPlayerEntity player = client.player;

        if (player == null) {
            return null;
        }
        Inventory inventory = Objects.requireNonNull(player).getInventory();
        for (int slot = 0; slot < inventory.size(); slot++) {
            final ItemStack itemStack = inventory.getStack(slot);
            if (filter.apply(itemStack)) {
                return new ItemWithSlot(itemStack, getSlotIndex(slot));
            }
        }
        return null;
    }

    protected void setPressed(boolean pressed) {
        client.options.useKey.setPressed(pressed);
    }

    protected static int getSlotIndex(int slot) {
        if (slot <= LAST_HOTBAR_SLOT_INDEX) {
            slot += PLAYER_INVENTORY_SLOT_COUNT_WITHOUT_EQUIPMENT_AND_CRAFTING_SLOTS;
        }

        return slot;
    }

    public static class ItemWithSlot {
        public final ItemStack itemStack;
        public final Integer slot;

        public ItemWithSlot(ItemStack itemStack, int slot) {
            this.itemStack = itemStack;
            this.slot = slot;
        }
    }
}
