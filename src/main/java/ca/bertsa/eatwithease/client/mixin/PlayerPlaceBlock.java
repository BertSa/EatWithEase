package ca.bertsa.eatwithease.client.mixin;

import ca.bertsa.eatwithease.client.EatWithEaseClient;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class PlayerPlaceBlock {
    @Inject(at = @At("HEAD"), method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z", cancellable = true)
    private void restrict(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (EatWithEaseClient.isEating()) {
            cir.setReturnValue(false);
        }
    }
}
