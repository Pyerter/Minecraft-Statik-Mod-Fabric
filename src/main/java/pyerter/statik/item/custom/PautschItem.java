package pyerter.statik.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pyerter.statik.Statik;
import pyerter.statik.item.inventory.INbtInventory;
import pyerter.statik.screen.handlers.PautschItemScreenHandler;
import pyerter.statik.util.InventoryUtil;

import java.util.List;

public class PautschItem extends Item implements INbtInventory, NamedScreenHandlerFactory { // implements AccessoryItem
    public static final String ITEM_INVENTORY_NBT_ID = "pootsadditions.pautsch_inventory";
    public static final Integer PAUTSCH_INVENTORY_SIZE = 27;

    public PautschItem(Settings settings) {
        super(settings);
    }

    /*
    // Accessory item stuff
    @Override
    public ItemModelRenderer.PlayerEquipStyle getEquipStyle() {
        return ItemModelRenderer.PlayerEquipStyle.BELT_LEFT;
    }

    @Override
    public boolean accessoryTick(World world, PlayerEntity entity, AccessoriesInventory inventory, ItemStack stack, int slot, boolean selected) {
        return false;
    }
     */

    public DefaultedList<ItemStack> getFullInventory(ItemStack stack) {
        validateInventory(stack);
        return InventoryUtil.getItemsFromNbt(stack.getNbt(), ITEM_INVENTORY_NBT_ID, PAUTSCH_INVENTORY_SIZE);
    }

    @Override
    public DefaultedList<ItemStack> getItems(ItemStack stack) {
        return getFullInventory(stack);
    }

    public boolean validateInventory(ItemStack stack) {
        if (!stack.hasNbt() || !stack.getNbt().contains(ITEM_INVENTORY_NBT_ID)) {
            createInventory(stack);
        }
        return true;
    }

    public void createInventory(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();

        DefaultedList<ItemStack> inventory = DefaultedList.ofSize(PAUTSCH_INVENTORY_SIZE, ItemStack.EMPTY);
        InventoryUtil.writeItemsToNbt(nbt, PAUTSCH_INVENTORY_SIZE, ITEM_INVENTORY_NBT_ID);
    }

    public void createInventory(ItemStack stack, DefaultedList<ItemStack> stacks) {
        NbtCompound nbt = stack.getOrCreateNbt();

        DefaultedList<ItemStack> inventory = DefaultedList.ofSize(PAUTSCH_INVENTORY_SIZE, ItemStack.EMPTY);
        InventoryUtil.writeItemsToNbt(nbt, stacks, ITEM_INVENTORY_NBT_ID);
    }

    public boolean tryCreateInventory(ItemStack stack, DefaultedList<ItemStack> desiredInventory) {
        if (!stack.hasNbt() || !stack.getNbt().contains(ITEM_INVENTORY_NBT_ID)) {
            createInventory(stack, desiredInventory);
            return true;
        }

        DefaultedList<ItemStack> inventory = InventoryUtil.getItemsFromNbt(stack.getNbt(), ITEM_INVENTORY_NBT_ID, PAUTSCH_INVENTORY_SIZE);
        boolean fullyMerged = InventoryUtil.tryMergeItemLists(inventory, desiredInventory);
        createInventory(stack, inventory);

        return fullyMerged;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // do stuff
        ItemStack stack = user.getStackInHand(hand);
        if (hand == Hand.MAIN_HAND && !world.isClient) {
            user.openHandledScreen(this);
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.pass(stack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return super.useOnBlock(context);
    }

    @Override
    public Text getDisplayName() {
        return MutableText.of(new LiteralTextContent("Pautsch Inventory"));
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        Statik.logInfo("Creating menu!");
        return new PautschItemScreenHandler(syncId, inv, player.getMainHandStack(), this);
    }

    @Nullable
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player, ItemStack stack) {
        Statik.logInfo("Creating menu!");
        return new PautschItemScreenHandler(syncId, inv, stack, this);
    }

    public static boolean acceptsQuickTransfer(ItemStack itemStack) {
        return !(itemStack.getItem() instanceof PautschItem);
    }
}
