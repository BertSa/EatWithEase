package ca.bertsa.eatwithease.client;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
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
        registerEatingKeyPressedEvent(new KeyEventHandler() {

            @Override
            public void handleKeyPressed() {
                if (client.player == null) {
                    return;
                }

                if (!client.player.getHungerManager().isNotFull()) {
                    super.setPressed(false);
                    return;
                }

                if (eating) {
                    super.setPressed(true);
                    return;
                }

                if (!isStackFoodAndNotBlacklisted(client.player.getMainHandStack()) && !isStackFoodAndNotBlacklisted(client.player.getOffHandStack())) {
                    ItemWithSlot itemStackWithSlot = getFirstMatchingItem(KeyEventHandler::isStackFoodAndNotBlacklisted);
                    if (itemStackWithSlot == null){
                        return;
                    }
                    super.swapStacks(itemStackWithSlot.slot);
                }

                super.setPressed(true);
                setEating(true);
            }


            @Override
            public void handleKeyReleased() {
                if (client.player == null) {
                    return;
                }

                if (eating) {
                    super.swapStacksBack();
                    super.setPressed(false);
                    setEating(false);
                }
            }
        });
    }

    public void registerEatingKeyPressedEvent(KeyEventHandler keyEventHandler) {
        ClientTickEvents.END_CLIENT_TICK.register(c -> {
            if (eatingKeyBinding.isPressed()) {
                keyEventHandler.handleKeyPressed();
            } else {
                keyEventHandler.handleKeyReleased();
            }
        });
    }
}

