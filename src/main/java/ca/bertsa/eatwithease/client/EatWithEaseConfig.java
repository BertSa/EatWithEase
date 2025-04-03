package ca.bertsa.eatwithease.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static ca.bertsa.eatwithease.client.EatWithEaseClient.LOGGER;

public class EatWithEaseConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("eat_with_ease.json");

    public static final List<String> DEFAULT_BLACKLIST = List.of(
            "chorus_fruit",
            "enchanted_golden_apple",
            "golden_apple",
            "honey_bottle",
            "poisonous_potato",
            "pufferfish",
            "rotten_flesh",
            "suspicious_stew",
            "spider_eye"
    );

    private static List<String> blacklist = new ArrayList<>(DEFAULT_BLACKLIST);

    public static boolean isBlacklisted(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        Identifier id = Registries.ITEM.getId(stack.getItem());
        if ("minecraft".equals(id.getNamespace())) {
            return getBlacklist().contains(id.getPath());
        } else {
            return getBlacklist().contains(id.toString());
        }
    }

    public static void loadConfig() {
        if (!Files.exists(CONFIG_PATH)) {
            saveConfig();
            return;
        }
        try {
            String json = Files.readString(CONFIG_PATH);
            Data data = GSON.fromJson(json, Data.class);
            if (data != null) {
                setBlacklist(data.blacklist);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load config", e);
        }

    }

    public static void saveConfig() {
        try {
            Data data = toData();
            Files.writeString(CONFIG_PATH, GSON.toJson(data));
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }

    private static Data toData() {
        Data data = new Data();
        data.blacklist = getBlacklist();
        return data;
    }

    public static List<String> getBlacklist() {
        return new ArrayList<>(blacklist);
    }

    public static void setBlacklist(List<String> blacklist) {
        EatWithEaseConfig.blacklist = new ArrayList<>(blacklist);
    }

    private static class Data {
        List<String> blacklist;
    }

}