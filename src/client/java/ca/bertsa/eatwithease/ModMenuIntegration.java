package ca.bertsa.eatwithease;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.stream.Collectors;

import static ca.bertsa.eatwithease.EatWithEaseConfig.DEFAULT_BLACKLIST;

public class ModMenuIntegration implements ModMenuApi {
    private static final String title = "Eat With Ease Settings";
    private static final String tooltipVanilla = "Food you Don't want to eat.";
    private static final String tooltipModded = "For modded food, use mod_id:food_item.";
    private static final String namespace = "minecraft:";


    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuIntegration::getModConfigScreen;
    }

    public static Screen getModConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal(title));
        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
        ConfigEntryBuilder eb = builder.entryBuilder();

        general.addEntry(eb.startStrList(Text.literal("Blacklist"), EatWithEaseConfig.getBlacklist())
                .setDefaultValue(DEFAULT_BLACKLIST)
                .setTooltip(Text.literal(tooltipVanilla), Text.literal(tooltipModded))
                .setSaveConsumer(bl -> {
                    List<String> validated = bl.stream()
                            .map(String::trim)
                            .filter(itemId -> !itemId.isEmpty())
                            .map(itemId -> itemId.startsWith(namespace) ? itemId.substring(namespace.length()) : itemId)
                            .distinct()
                            .filter(itemId -> {
                                if (itemId.contains(":")) {
                                    String[] parts = itemId.split(":", 2);
                                    String modid = parts[0];
                                    return modid.matches("[a-z0-9_-]+");
                                }
                                return true;
                            })
                            .filter(itemId -> {
                                Identifier id = Identifier.tryParse(itemId.contains(":") ? itemId : namespace + itemId);
                                if (id == null) {
                                    return false;
                                }
                                Item item = Registries.ITEM.get(id);
                                return id.toString().equals(namespace + "air") || !item.equals(Items.AIR);
                            })
                            .collect(Collectors.toList());
                    EatWithEaseConfig.setBlacklist(validated);
                })
                .build());

        builder.setSavingRunnable(EatWithEaseConfig::saveConfig);
        return builder.build();
    }
}