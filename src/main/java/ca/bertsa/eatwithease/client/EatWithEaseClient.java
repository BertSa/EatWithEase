package ca.bertsa.eatwithease.client;

import ca.bertsa.grossesaucelib.callbacks.KeyEventCallback;
import ca.bertsa.grossesaucelib.utils.InventoryUtils;
import ca.bertsa.grossesaucelib.utils.PlayerInteractionUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class EatWithEaseClient implements ClientModInitializer {
    public static final String MOD_ID = "eat-with-ease";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyBinding eatingKeyBinding;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private static boolean eating = false;

    @Override
    public void onInitializeClient() {
        EatWithEaseConfig.loadConfig();
        eatingKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + MOD_ID + ".eat", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category." + MOD_ID + ".eat-with-ease"));
        registerEatingKeyPressedEvent(new KeyEventCallback() {

            @Override
            public void handleKeyPressed(@NotNull MinecraftClient client) {
                ClientPlayerEntity player = client.player;

                if (player == null) {
                    return;
                }

                if (!player.getHungerManager().isNotFull()) {
                    PlayerInteractionUtils.setUseKeyPressedFalse();
                    return;
                }

                if (isEating()) {
                    PlayerInteractionUtils.setUseKeyPressedTrue();
                    return;
                }

                if (!Utils.isStackFoodAndNotBlacklisted(player.getMainHandStack()) && !Utils.isStackFoodAndNotBlacklisted(player.getOffHandStack())) {
                    Integer itemStackWithSlot = InventoryUtils.getSlotIndexOfFirstMatchingItem(Utils::isStackFoodAndNotBlacklisted);
                    if (itemStackWithSlot == null) {
                        return;
                    }
                    InventoryUtils.swapStacksWithHand(EatWithEaseConfig.getPreferredHand(), itemStackWithSlot);
                }

                PlayerInteractionUtils.setUseKeyPressedTrue();
                setEating(true);
            }


            public void handleKeyReleased(@NotNull MinecraftClient client) {
                if (isEating()) {
                    InventoryUtils.swapStacksBack();
                    PlayerInteractionUtils.setUseKeyPressedFalse();
                    setEating(false);
                }
            }
        });
    }

    public void registerEatingKeyPressedEvent(KeyEventCallback keyEventHandler) {
        ClientTickEvents.END_CLIENT_TICK.register(c -> {
            if (eatingKeyBinding.isPressed()) {
                keyEventHandler.handleKeyPressed(c);
            } else {
                keyEventHandler.handleKeyReleased(c);
            }
        });
    }
}

