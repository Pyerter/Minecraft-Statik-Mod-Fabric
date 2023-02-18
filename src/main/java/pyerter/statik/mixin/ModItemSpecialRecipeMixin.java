package pyerter.statik.mixin;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pyerter.statik.util.IItemSpecialRecipeRemainder;

@Mixin(Item.class)
public abstract class ModItemSpecialRecipeMixin {

    @Inject(method="getRecipeRemainder", at=@At("HEAD"), cancellable = true)
    public void onGetRecipeRemainder(CallbackInfoReturnable<Item> info) {
        if (this instanceof IItemSpecialRecipeRemainder) {
            info.setReturnValue(((IItemSpecialRecipeRemainder) this).getSpecialRecipeRemainder());
        }
    }
}
