package pyerter.statik.item.custom.engineering;

import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;

public class EngineeredAxe extends AbstractEngineeredTool {
    public EngineeredAxe(float attackDamage, float attackSpeed, ToolMaterial material, Settings settings) {
        super(attackDamage, attackSpeed, material, BlockTags.AXE_MINEABLE, settings);

        toolType = ToolType.AXE;
        super.registerTool(this, material, ToolType.AXE);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (anitcipatedUseOnBlockResult(context) == ActionResult.FAIL)
            return ActionResult.PASS;

        return super.useAxeActionOnBlock(context);
    }
}
