package ca.bertsa.eatwithease.client.mixin;

import ca.bertsa.eatwithease.client.EatWithEaseClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class ScrollDisableMixin {

    @Inject(at = @At("HEAD"), method = "onMouseScroll(JDD)V", cancellable = true)
    private void disableScrolling(long window, double horizontal, double vertical, CallbackInfo callbackInfo) {
        if (EatWithEaseClient.isEating()) {
            callbackInfo.cancel();
        }

    }
}

