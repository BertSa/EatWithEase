package ca.bertsa.eatwithease.client.mixin;

import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ca.bertsa.eatwithease.client.EatWithEaseClient.scrollDisabled;

@Mixin(PlayerInventory.class)
public class ScrollDisableMixin {

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V", cancellable = true)
    private void disableScrolling(double scrollAmount, CallbackInfo callbackInfo) {
        if (scrollDisabled) {
            callbackInfo.cancel();
        }

    }
}

