package ca.bertsa.eatwithease;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EatWithEaseClient implements ClientModInitializer {
    public static final String MOD_ID = "eat-with-ease";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyBinding eatingKeyBinding;


    @Override
    public void onInitializeClient() {
        EatWithEaseConfig.loadConfig();
        eatingKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + MOD_ID + ".eat", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category." + MOD_ID + ".eat-with-ease"));
        registerEatingKeyPressedEvent();
    }

    private void registerEatingKeyPressedEvent() {
        EatingHandler eatingHandler = EatingHandler.getInstance();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (eatingKeyBinding.isPressed()) {
                eatingHandler.handleKeyPressed(client);
            } else {
                eatingHandler.handleKeyReleased(client);
            }
        });
    }
}

