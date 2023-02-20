package pyerter.statik.item.custom.engineering;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class AbstractEngineersItem extends Item {
    public enum ENGINEERS_CRAFT_TYPE {
        ENGINEERIFY,
        REPAIR,
        NADA
    }

    public AbstractEngineersItem(Settings settings) {
        super(settings);
    }

    public abstract ENGINEERS_CRAFT_TYPE getEngineersCraftType();

    public static ENGINEERS_CRAFT_TYPE tryGetEngineersCraftType(ItemStack stack) {
        if (stack != null && !stack.isEmpty() && stack.getItem() instanceof AbstractEngineersItem) {
            return ((AbstractEngineersItem)stack.getItem()).getEngineersCraftType();
        }
        return ENGINEERS_CRAFT_TYPE.NADA;
    }
}
