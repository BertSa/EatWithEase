package ca.bertsa.eatwithease;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Objects;


public class EatingHandler {
    private static final int LAST_HOTBAR_SLOT_INDEX = 8;
    private static final int PLAYER_INVENTORY_SLOT_COUNT_WITHOUT_EQUIPMENT_AND_CRAFTING_SLOTS = 36;

    private static EatingHandler single_instance = null;

    private boolean eating;
    private Integer foodSlot;

    private EatingHandler() {
        setEating(false);
        setFoodSlot(null);
    }

    public static synchronized EatingHandler getInstance() {
        if (single_instance == null) {
            single_instance = new EatingHandler();
        }

        return single_instance;
    }

    public void setEating(boolean eating) {
        this.eating = eating;
    }

    public void setFoodSlot(Integer foodSlot) {
        this.foodSlot = foodSlot;
    }

    public void handleKeyPressed(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        KeyBinding useKey = client.options.useKey;


        if (player == null || interactionManager == null || useKey == null) {
            return;
        }

        if (!player.getHungerManager().isNotFull()) {
            useKey.setPressed(false);
            return;
        }

        if (eating) {
            useKey.setPressed(true);
            return;
        }

        Inventory inventory = Objects.requireNonNull(player).getInventory();
        for (int slot = 0; slot < inventory.size(); slot++) {
            final ItemStack itemStack = inventory.getStack(slot);

            if (itemStack.contains(DataComponentTypes.FOOD) && !EatWithEaseConfig.isBlacklisted(itemStack)) {
                foodSlot = getSlotIndex(slot);
                swapStacks(player, interactionManager, foodSlot);
                eating = true;
                useKey.setPressed(true);
                return;
            }
        }
    }

    public void handleKeyReleased(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        KeyBinding useKey = client.options.useKey;

        if (player == null || interactionManager == null || foodSlot == null) {
            return;
        }

        if (eating) {
            swapStacks(player, interactionManager, foodSlot);
            setEating(false);
            setFoodSlot(null);
            useKey.setPressed(false);
        }
    }

    private void swapStacks(ClientPlayerEntity player, ClientPlayerInteractionManager interactionManager, int slot) {
        int selectedSlot = getSlotIndex(player.getInventory().selectedSlot);

        if (selectedSlot == PLAYER_INVENTORY_SLOT_COUNT_WITHOUT_EQUIPMENT_AND_CRAFTING_SLOTS) {
            interactionManager.clickSlot(0, selectedSlot, 0, SlotActionType.SWAP, player);
            interactionManager.clickSlot(0, slot, 0, SlotActionType.SWAP, player);
            interactionManager.clickSlot(0, selectedSlot, 0, SlotActionType.SWAP, player);
        } else {
            interactionManager.clickSlot(0, slot, 0, SlotActionType.SWAP, player);
            interactionManager.clickSlot(0, selectedSlot, 0, SlotActionType.SWAP, player);
            interactionManager.clickSlot(0, slot, 0, SlotActionType.SWAP, player);
        }
    }

    private static int getSlotIndex(int slot) {
        if (slot <= LAST_HOTBAR_SLOT_INDEX) {
            slot += PLAYER_INVENTORY_SLOT_COUNT_WITHOUT_EQUIPMENT_AND_CRAFTING_SLOTS;
        }

        return slot;
    }
}