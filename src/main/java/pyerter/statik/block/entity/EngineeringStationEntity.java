package pyerter.statik.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pyerter.statik.Statik;
import pyerter.statik.item.ModItems;
import pyerter.statik.item.ModToolMaterials;
import pyerter.statik.item.custom.engineering.AbstractEngineeredTool;
import pyerter.statik.item.custom.engineering.AbstractEngineersItem;
import pyerter.statik.item.custom.engineering.EngineersTrustyHammer;
import pyerter.statik.item.custom.engineering.augment.Augment;
import pyerter.statik.item.custom.engineering.augment.AugmentHelper;
import pyerter.statik.item.custom.engineering.augment.AugmentedTabletItem;
import pyerter.statik.item.inventory.ImplementedInventory;
import pyerter.statik.recipe.EngineeringStationRefineRecipe;
import pyerter.statik.screen.handlers.EngineeringStationScreenHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EngineeringStationEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private static final String DISPLAY_NAME = "Engineering Station";
    private static List<ToolMaterial> materials = null;
    public static List<ToolMaterial> getMaterials() { if(materials == null) materials = updateToolMaterials(); return materials; }
    public static final List<Item> ENGINEERS_ITEMS = initializeEngineerItemsList();
    private static List<Item> initializeEngineerItemsList() {
        List<Item> items = new ArrayList<>();
        items.add(ModItems.ENGINEERS_BLUPRINT);
        items.add(ModItems.AUGMENTED_TABLET_ITEM);
        return items;
    }
    public static final List<Item> acceptedRefiningMaterials = new ArrayList<>();
    public static final int ENGINEERING_STATION_INVENTORY_SIZE = 5;
    private final DefaultedList<ItemStack> inventory =
            DefaultedList.ofSize(ENGINEERING_STATION_INVENTORY_SIZE, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;
    private int hammered = 0;
    private int successfulBuild = 0;
    private ItemStack resultingStack = ItemStack.EMPTY;

    public static final int HAMMERS_PER_CRAFT = 2;

    public EngineeringStationEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENGINEERING_STATION, pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                switch (index) {
                    case 0: return EngineeringStationEntity.this.hammered;
                    case 1: return EngineeringStationEntity.this.successfulBuild;
                    default: return 0;
                }
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: EngineeringStationEntity.this.hammered = value; break;
                    case 1: EngineeringStationEntity.this.successfulBuild = value; break;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return MutableText.of(new LiteralTextContent(DISPLAY_NAME));
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new EngineeringStationScreenHandler(syncId, inv, this, this.propertyDelegate, this);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("engineering_station.hammered", hammered);
        nbt.putInt("engineering_station.successfulBuild", successfulBuild);
        nbt.put("engineering_station.resulting_stack", resultingStack.writeNbt(new NbtCompound()));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, inventory);
        super.readNbt(nbt);
        hammered = nbt.getInt("engineering_station.hammered");
        successfulBuild = nbt.getInt("engineering_station.successfulBuild");
        resultingStack = ItemStack.fromNbt(nbt.getCompound("engineering_station.resulting_stack"));
    }

    public Pair<Boolean, Boolean> hammerIt() {
        return hammerIt(false);
    }

    // Left boolean: if can hammer
    // Right boolean: if result was made
    // Left boolean implies right boolean
    public Pair<Boolean, Boolean> hammerIt(boolean stackCraft) {
        Pair<Optional<ItemStack>, Boolean[]> craftingResult = getCraftingResult();
        Optional<Pair<Pair<ItemStack, List<Augment>>, Boolean[]>> augmentResult = getAugmentResult();
        if (!craftingResult.getLeft().isPresent() && !augmentResult.isPresent()) {
            resetHammeredProgress();
            return new Pair<>(false, false);
        }

        boolean shouldStackCraft = stackCraft &&
                craftingResult.getLeft().isPresent() &&
                !craftingResult.getRight()[0] &&
                !craftingResult.getRight()[1] &&
                !craftingResult.getRight()[2] &&
                craftingResult.getRight()[3];

        hammered++;
        if (hammered >= HAMMERS_PER_CRAFT) {
            if (craftingResult.getLeft().isPresent()) {
                if (shouldStackCraft) {
                    craftResultMax(craftingResult.getLeft().get(), craftingResult.getRight());
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1f, 1f, 0);
                } else {
                    craftResult(craftingResult.getLeft().get(), craftingResult.getRight());
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1f, 1f, 0);
                }
            } else {
                craftAugment(augmentResult.get());
                world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1f, 1f, 0);
            }
            resetHammeredProgress();
            successfulBuild = 1;
            // world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1f, 1f, 0);
            return new Pair<>(true, true);
        } else {
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1f, 1f, 0);
        }
        return new Pair<>(true, false);
    }

    public boolean craftResult(ItemStack stack, Boolean[] usedSlots) {
        if (stack == null || usedSlots.length != 4)
            return false;

        for (int i = 0; i < 4; i++) {
            if (usedSlots[i])
                inventory.get(i).decrement(1);
        }

        if (!stack.isEmpty()) {
            if (inventory.get(2).isEmpty())
                inventory.set(2, stack);
            else if (inventory.get(2).isItemEqual(stack))
                inventory.get(2).increment(stack.getCount());
        }
        return true;
    }

    public boolean craftResultMax(ItemStack stack, Boolean[] usedSlots) {
        if (stack == null || stack.isEmpty() || usedSlots.length != 4)
            return false;

        Optional<EngineeringStationRefineRecipe> recipe = hasRefineRecipe(this);
        if (!recipe.isPresent() || !recipe.get().getOutput().isItemEqual(stack))
            return false;

        ItemStack recipeResult = recipe.get().getOutput();
        int maxStackCount = recipeResult.getMaxCount();
        int openSpace = (maxStackCount - (!inventory.get(2).isEmpty() ? inventory.get(2).getCount() : 0));
        maxStackCount = openSpace;
        int repeatTimes = maxStackCount / recipeResult.getCount();
        for (int i = 0; i < 4; i++) {
            if (usedSlots[i])
                repeatTimes = Math.min(repeatTimes, inventory.get(i).getCount());
        }
        stack.setCount(stack.getCount() * repeatTimes);

        for (int i = 0; i < 4; i++) {
            if (usedSlots[i])
                inventory.get(i).decrement(repeatTimes);
        }

        if (inventory.get(2).isEmpty())
            inventory.set(2, stack);
        else if (inventory.get(2).isItemEqual(stack))
            inventory.get(2).increment(stack.getCount());

        return true;
    }

    public boolean craftAugment(Pair<Pair<ItemStack, List<Augment>>, Boolean[]> augmentResult) {
        if (augmentResult.getLeft().getLeft().isEmpty()) {
            boolean augmentedTool = false;
            ItemStack augmentStack = getEngineeringSlot();

            for (Augment aug : augmentResult.getLeft().getRight()) {
                if (aug.applyAugment(augmentStack))
                    augmentedTool = true;
            }

            if (augmentedTool) {
                craftResult(ItemStack.EMPTY, augmentResult.getRight());
                return true;
            }
        } else if (augmentResult.getLeft().getRight() == null) {
            return craftResult(augmentResult.getLeft().getLeft(), augmentResult.getRight());
        }
        return false;
    }

    public void resetHammeredProgress() {
        hammered = 0;
        successfulBuild--;
    }

    public static void tick(World world, BlockPos pos, BlockState state, EngineeringStationEntity entity) {
        // nada
    }

    public Pair<Optional<ItemStack>, Boolean[]> getCraftingResult() {

        Optional<ItemStack> resultingStack;

        resultingStack = canEngineerTool(getToolSlot(), getEngineeringSlot());
        if (resultingStack.isPresent())
            return new Pair(resultingStack, new Boolean[]{true, false, true, false});

        resultingStack = canUpgradeEngineerTool(getMaterialSlot(), getEngineeringSlot());
        if (resultingStack.isPresent())
            return new Pair(resultingStack, new Boolean[]{false, false, true, true});

        Optional<EngineeringStationRefineRecipe> refineRecipe = hasRefineRecipe(this);
        if (refineRecipe.isPresent()) {
            if (refineRecipe.get().getEngineerIngredient().isEmpty())
                return new Pair(Optional.of(refineRecipe.get().getOutput()), new Boolean[]{false, false, false, true});
            else
                return new Pair(Optional.of(refineRecipe.get().getOutput()), new Boolean[]{false, false, true, true});
        }

        return new Pair<>(Optional.empty(), null);
    }

    public Optional<Pair<Pair<ItemStack, List<Augment>>, Boolean[]>> getAugmentResult() {
        Boolean[] augmentToolUses = new Boolean[]{false, true, false, false};
        Optional<Pair<ItemStack, List<Augment>>> augmentResult = canAugmentTool(getEngineeringSlot(), getAugmentSlot());
        if (augmentResult.isPresent()) {
            return Optional.of(new Pair<>(new Pair<>(ItemStack.EMPTY, augmentResult.get().getRight()), augmentToolUses));
        }

        Statik.logInfo("Checking if can forge augment");
        Boolean[] forgedAugmentUses = new Boolean[]{ false, true, false, true };
        Optional<ItemStack> forgedAugment = canForgeAugment(getMaterialSlot(), getAugmentSlot(), getEngineeringSlot());
        if (forgedAugment.isPresent()) {
            return Optional.of(new Pair<>(new Pair<>(forgedAugment.get(), null), forgedAugmentUses));
        }

        return Optional.empty();
    }

    public static Optional<ItemStack> canEngineerTool(ItemStack toolStack, ItemStack engineerStack) {
        ToolMaterial mat = getToolMaterial(toolStack);
        AbstractEngineeredTool.ToolType toolType = AbstractEngineeredTool.getToolType(toolStack);
        if (mat == null || toolType == AbstractEngineeredTool.ToolType.NADA)
            return Optional.empty();

        AbstractEngineersItem.ENGINEERS_CRAFT_TYPE craftType = AbstractEngineersItem.tryGetEngineersCraftType(engineerStack);
        switch (craftType) {
            case ENGINEERIFY:
                AbstractEngineeredTool resultTool = AbstractEngineeredTool.getRegisteredTool(mat, toolType);
                if (resultTool != null) {
                    ItemStack resultStack = new ItemStack(resultTool);
                    AbstractEngineeredTool.copyEnchantmentsFrom(toolStack, resultStack);
                    return Optional.of(resultStack);
                }
                return Optional.empty();
            // return resultTool != null ? Optional.of(new ItemStack(resultTool)) : Optional.empty();
            default: return Optional.empty();
        }
    }

    public static Optional<ItemStack> canUpgradeEngineerTool(ItemStack materialStack, ItemStack engineerStack) {
        ToolMaterial mat = AbstractEngineeredTool.getToolMaterialFromIngredient(materialStack);
        if (mat == null)
            return Optional.empty();

        Pair<ToolMaterial, AbstractEngineeredTool.ToolType> typePair = AbstractEngineeredTool.tryGetToolInfo(engineerStack);
        if (typePair == null || typePair.getRight() == AbstractEngineeredTool.ToolType.NADA || typePair.getLeft() == mat)
            return Optional.empty();

        if (!(mat.getMiningLevel() == typePair.getLeft().getMiningLevel() + 1 || mat.getMiningLevel() == typePair.getLeft().getMiningLevel()))
            return Optional.empty();

        AbstractEngineeredTool newTool = AbstractEngineeredTool.getRegisteredTool(mat, typePair.getRight());
        ItemStack newStack = new ItemStack(newTool);
        boolean transferSuccess = AbstractEngineeredTool.transferToolData (engineerStack, newStack);
        if (!transferSuccess)
            return Optional.empty();

        Optional<ItemStack> resultStack = Optional.of(newStack);
        return resultStack;
    }

    private static Optional<EngineeringStationRefineRecipe> hasRefineRecipe(EngineeringStationEntity entity) {
        World world = entity.world;
        SimpleInventory inventory = new SimpleInventory(entity.inventory.size());
        for (int i = 0; i < inventory.size(); i++) { inventory.setStack(i, entity.getStack(i)); }

        Optional<EngineeringStationRefineRecipe> match = world.getRecipeManager()
                .getFirstMatch(EngineeringStationRefineRecipe.Type.INSTANCE, inventory, world);
        boolean returnMatch = match.isPresent();
        if (returnMatch) {
            if (match.get().getEngineerIngredient().isEmpty())
                returnMatch = canInsertAmountIntoOutputSlot(inventory)
                        && canInsertItemIntoOutputSlot(inventory, match.get().getOutput());
        }
        /*if (match.isPresent() && canInsertAmountIntoOutputSlot(inventory)
                && canInsertItemIntoOutputSlot(inventory, match.get().getOutput())) {
            return match;
        }*/

        return returnMatch ? match : Optional.empty();
    }

    private static Optional<Pair<ItemStack, List<Augment>>> canAugmentTool(ItemStack toolSlot, ItemStack augmentSlot) {
        if (augmentSlot.isEmpty() || !(augmentSlot.getItem() instanceof AugmentedTabletItem) || toolSlot.isEmpty())
            return Optional.empty();

        List<Augment> augments = AugmentHelper.getAugments(augmentSlot);
        List<Augment> augmentResults = augments.stream()
                .map(a -> AugmentHelper.getResultingAugmentModification(toolSlot, a))
                .filter(a -> a != null).toList();
        if (augmentResults.size() == 0)
            return Optional.empty();

        return Optional.of(new Pair<>(toolSlot, augmentResults));
    }

    private static Optional<ItemStack> canForgeAugment(ItemStack materialSlot, ItemStack augmentSlot, ItemStack engineeringSlot) {
        if (!engineeringSlot.isEmpty() || materialSlot.isEmpty() || augmentSlot.isEmpty())
            return Optional.empty();

        if (!(augmentSlot.getItem() instanceof AugmentedTabletItem) || !(materialSlot.getItem() instanceof EnchantedBookItem) || AugmentHelper.getAugments(augmentSlot).size() > 0)
            return Optional.empty();

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.fromNbt(EnchantedBookItem.getEnchantmentNbt(materialSlot));

        Optional<Augment> resultAugment = enchantments.entrySet().stream()
                .map(e -> Augment.getAugmentFromEnchantment(e.getKey(), e.getValue()))
                .filter(o -> o.isPresent()).map(o -> o.get()).findFirst();

        if (resultAugment.isPresent()) {
            ItemStack result = new ItemStack(ModItems.AUGMENTED_TABLET_ITEM);
            resultAugment.get().applyAugment(result);
            return Optional.of(result);
        }

        return Optional.empty();
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleInventory inventory, ItemStack output) {
        return inventory.getStack(2).getItem() == output.getItem() || inventory.getStack(2).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleInventory inventory) {
        return inventory.getStack(2).getMaxCount() > inventory.getStack(2).getCount();
    }

    public static boolean acceptsQuickTransfer(ItemStack itemStack) {
        return false;
    }

    /** Slot 0: Tool slot is the left slot **/
    public static boolean acceptsQuickTransferToolSlot(ItemStack itemStack) {
        return itemStack.getItem() instanceof ToolItem;
    }
    /** Slot 0: Tool slot is the left slot **/
    public ItemStack getToolSlot() {
        return inventory.get(0);
    }

    /** Slot 1: Augment slot is the right slot **/
    public static boolean acceptsQuickTransferAugmentSlot(ItemStack itemStack) {
        return itemStack.getItem() instanceof AugmentedTabletItem;
    }
    /** Slot 1: Augment slot is the right slot **/
    public ItemStack getAugmentSlot() {
        return inventory.get(1);
    }

    /** Slot 2: Engineering Slot is the bottom slot **/
    public static boolean acceptsQuickTransferEngineeringSlot(ItemStack itemStack) {
        return itemStack.getItem() instanceof AbstractEngineeredTool || ENGINEERS_ITEMS.contains(itemStack.getItem());
    }
    /** Slot 2: Engineering Slot is the bottom slot **/
    public ItemStack getEngineeringSlot() {
        return inventory.get(2);
    }

    /** Slot 3: Materials slot is the top slot **/
    public static boolean acceptsQuickTransferMaterialSlot(ItemStack itemStack) {
        return acceptedRefiningMaterials.contains(itemStack.getItem()) || AbstractEngineeredTool.isAcceptedToolIngredient(itemStack) || itemStack.getItem() instanceof EnchantedBookItem;
    }
    /** Slot 3: Materials slot is the top slot **/
    public ItemStack getMaterialSlot() {
        return inventory.get(3);
    }

    /** Slot 4: Hammer slot is the far left slot **/
    public static boolean acceptsQuickTransferHammerSlot(ItemStack itemStack) {
        return itemStack.getItem() instanceof EngineersTrustyHammer;
    }
    /** Slot 4: Hammer slot is the far left slot **/
    public ItemStack getHammerSlot() {
        return inventory.get(4);
    }

    public static boolean acceptsQuickTransfer(ItemStack itemStack, Integer i) {
        switch (i) {
            case 0: return acceptsQuickTransferToolSlot(itemStack);
            case 1: return acceptsQuickTransferAugmentSlot(itemStack);
            case 2: return acceptsQuickTransferEngineeringSlot(itemStack);
            case 3: return acceptsQuickTransferMaterialSlot(itemStack);
            case 4: return acceptsQuickTransferHammerSlot(itemStack);
            default: return false;
        }
    }

    public static ToolMaterial getToolMaterial(ItemStack itemStack) {
        if (itemStack != null && !itemStack.isEmpty() && itemStack.getItem() instanceof ToolItem) {
            ToolMaterial mat = ((ToolItem)itemStack.getItem()).getMaterial();
            return mat;
        }
        return null;
    }

    public static List<ToolMaterial> updateToolMaterials() {
        List<ToolMaterial> mats = List.of(ToolMaterials.values());

        // TODO: figure out how to implement a tool material finder to be compatible with other mods
        // Loop through Modded tool material instances provided in a JSON and add?
        List<ToolMaterial> modMats = List.of(ModToolMaterials.values());
        mats.addAll(modMats);

        mats.sort((a, b) -> { return a.getMiningLevel() - b.getMiningLevel(); });
        return mats;
    }
}
