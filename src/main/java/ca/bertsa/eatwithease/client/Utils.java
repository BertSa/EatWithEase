package ca.bertsa.eatwithease.client;

import ca.bertsa.grossesaucelib.utils.ItemUtils;
import net.minecraft.item.ItemStack;

public class Utils {
    public static boolean isStackFoodAndNotBlacklisted(ItemStack stack) {
        return ItemUtils.isStackFood(stack) && !EatWithEaseConfig.isBlacklisted(stack);
    }
}
