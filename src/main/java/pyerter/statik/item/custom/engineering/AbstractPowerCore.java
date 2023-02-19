package pyerter.statik.item.custom.engineering;

import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
//import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pyerter.statik.block.entity.CaptureChamberEntity;
import pyerter.statik.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public abstract class AbstractPowerCore extends Item implements IChargeable { // implements AccessoryItem

    public static final List<Item> REGISTERED_CORES = new ArrayList<>();
    public static boolean isCoreFuelItem(ItemStack stack) {
        return REGISTERED_CORES.contains(stack.getItem());
    }
    public static final String CHARGE_NBT_ID = "statik.core_charge";

    public AbstractPowerCore(Settings settings) {
        super(settings);
        REGISTERED_CORES.add(this);
    }

    /*
    // Used for the style of rendering if it is displayed on the character
    @Override
    public ItemModelRenderer.PlayerEquipStyle getEquipStyle() {
        return ItemModelRenderer.PlayerEquipStyle.BACK_SHEATHED;
    }
     */

    /*
    // used to transfer charge when in the accessory inventory
    @Override
    public boolean accessoryTick(World world, PlayerEntity entity, AccessoriesInventory inventory, ItemStack stack, int slot, boolean selected) {
        if (!world.isClient()) {
            List<ItemStack> stacks = inventory.getAllEquipmentStacks(entity);
            boolean transferred = false;
            for (ItemStack targetStack: stacks) {
                if (targetStack != stack && targetStack.getItem() instanceof IChargeable) {
                    tryTransferCharge(stack, targetStack);
                    transferred = true;
                }
            }

            return transferred;
        }
        return false;
    }
     */

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return super.use(world, user, hand);
    }

    @Override
    public String getChargeNbtID() {
        return CHARGE_NBT_ID;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return getFilledPercent(stack) > 0.66;
    }

    public int tryTransferCharge(ItemStack stack, ItemStack targetStack) {
        if (!targetStack.isEmpty() && targetStack.getItem() instanceof IChargeable && !(targetStack.getItem() instanceof AbstractPowerCore)) {
            IChargeable chargeable = (IChargeable) targetStack.getItem();
            int availablePower = Util.clamp(getCharge(stack), 0, CaptureChamberEntity.MAX_CHARGE_TRANSFER_RATE);
            int transferedPower = chargeable.tryCharge(targetStack, availablePower);
            addCharge(stack, -transferedPower);
            return transferedPower;
        }
        return 0;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient() && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if (!(player.getMainHandStack().isItemEqual(stack) || player.getOffHandStack().isItemEqual(stack)))
                return;

            ItemStack oppositeStack = player.getMainHandStack().isItemEqual(stack) ? player.getOffHandStack() : player.getMainHandStack();
            tryTransferCharge(stack, oppositeStack);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(MutableText.of(new LiteralTextContent("Core Charge: " + getCharge(stack))).formatted(Formatting.AQUA));
        tooltip.add(MutableText.of(new LiteralTextContent("\"1.21 Gigawatts!\"")).formatted(Formatting.ITALIC));
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return super.getTooltipData(stack);
    }

    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    public int getItemBarStep(ItemStack stack) {
        return Math.round((float)getCharge(stack) * 13.0F / (float)getMaxCharge());
    }

    public int getItemBarColor(ItemStack stack) {
        float f = Math.max(0.0F, (float)getCharge(stack) / (float)getMaxCharge());
        return MathHelper.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    public static class ChargePredicateProvider implements ClampedModelPredicateProvider {

        public BiFunction<ItemStack, IChargeable, Float> coreChargeFunction;
        public ChargePredicateProvider(BiFunction<ItemStack, IChargeable, Float> coreChargeFunction) {
            this.coreChargeFunction = coreChargeFunction;
        }

        @Override
        public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
            if (stack.getItem() instanceof IChargeable) {
                IChargeable chargeable = (IChargeable) stack.getItem();
                return coreChargeFunction.apply(stack, chargeable);
            }
            return 0;
        }

        @Override
        public float unclampedCall(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
            if (stack.getItem() instanceof IChargeable) {
                IChargeable chargeable = (IChargeable) stack.getItem();
                return coreChargeFunction.apply(stack, chargeable);
            }
            return 0;
        }
    }
}
