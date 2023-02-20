package pyerter.statik.item.custom.engineering.augment;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pyerter.statik.Statik;
import pyerter.statik.item.custom.engineering.AbstractEngineeredTool;

public class SimpleAugment extends Augment {

    protected final int augmentMask;
    protected final String augmentID;

    protected int level = 1;
    protected float levelMultiplier = 0.5f;

    protected float attackDamage = 0;
    protected float attackSpeed = 0;
    protected float miningSpeedMult = 1;
    protected PostMinePredicate postMinePredicate = null;

    protected SimpleAugment( String augmentID, int augmentMask) {
        super(augmentID);
        this.augmentID = augmentID;
        this.augmentMask = augmentMask;
    }

    protected SimpleAugment(String augmentID, int augmentMask, int level, float levelMultiplier, float attackDamage, float attackSpeed, float miningSpeedMult, PostMinePredicate postMinePredicate) {
        super(augmentID);
        this.augmentID = augmentID;
        this.augmentMask = augmentMask;
        this.level = level;
        this.levelMultiplier = levelMultiplier;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.miningSpeedMult = miningSpeedMult;
        this.postMinePredicate = postMinePredicate;
    }

    @Override
    public String getAugmentID() {
        return augmentID;
    }

    @Override
    public int getAugmentMask() {
        return augmentMask;
    }

    @Override
    public String getTranslation() {
        return super.getTranslation() + ": " + level;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public float onGetAttackDamage(ItemStack stack, AbstractEngineeredTool tool) {
        return attackDamage + (Math.max(0, level - 1) * attackDamage * levelMultiplier);
    }

    @Override
    public float getAugmentAttackDamage() {
        return attackDamage + (Math.max(0, level - 1) * attackDamage * levelMultiplier);
    }

    @Override
    public float onGetMiningSpeedMultiplier(ItemStack stack, AbstractEngineeredTool tool) {
        return miningSpeedMult + (Math.max(0, level - 1) * miningSpeedMult * levelMultiplier);
    }

    @Override
    public float getAugmentMiningSpeedMultiplier() {
        return miningSpeedMult + (Math.max(0, level - 1) * miningSpeedMult * levelMultiplier);
    }

    @Override
    public float getAugmentAttackSpeed() {
        return attackSpeed + (Math.max(0, level - 1) * attackSpeed * levelMultiplier);
    }

    @Override
    public boolean onPostMine(boolean effectiveMine, ItemStack stack, AbstractEngineeredTool tool, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        Statik.logInfo("Calling post mine");
        return postMinePredicate != null && effectiveMine ? postMinePredicate.onPostMine(stack, tool, level, world, state, pos, miner) : false;
    }

    public static class Builder {
        protected String augmentID;

        protected int level = 1;
        protected float levelMultiplier = 0.5f;

        protected int augmentMask = 0;

        protected float attackDamage = 0;
        protected float attackSpeed = 0;
        protected float miningSpeedMult = 1;
        PostMinePredicate minePredicate = null;

        public Builder(String augmentID) {
            this.augmentID = augmentID;
        }

        public Builder setLevel(int level) {
            this.level = Math.max(0, level);
            return this;
        }

        public Builder setLevelMultiplier(float levelMultiplier) {
            this.levelMultiplier = levelMultiplier;
            return this;
        }

        public Builder addAttackDamage(float attackDamage) {
            if ((augmentMask & Augment.MASK_GET_ATTACK_DAMAGE) == 0)
                augmentMask |= Augment.MASK_GET_ATTACK_DAMAGE;
            this.attackDamage += attackDamage;
            return this;
        }

        public Builder addAttackSpeed(float attackSpeed) {
            if ((augmentMask & Augment.MASK_GET_ATTACK_SPEED) == 0)
                augmentMask |= Augment.MASK_GET_ATTACK_SPEED;
            this.attackSpeed += attackSpeed;
            return this;
        }

        public Builder addMiningSpeedMult(float mult) {
            if ((augmentMask & Augment.MASK_GET_MINING_SPEED) == 0)
                augmentMask |= Augment.MASK_GET_MINING_SPEED;
            this.miningSpeedMult += mult;
            return this;
        }

        public Builder setMinePredicate(PostMinePredicate predicate) {
            if ((augmentMask & Augment.MASK_POST_MINE) == 0)
                augmentMask |= Augment.MASK_POST_MINE;
            this.minePredicate = predicate;
            return this;
        }

        public SimpleAugment build() {
            return new SimpleAugment(augmentID, augmentMask, level, levelMultiplier, attackDamage, attackSpeed, miningSpeedMult, minePredicate);
        }

        public SimpleAugment[] buildLevels(int levels) {
            SimpleAugment[] augments = new SimpleAugment[levels];
            for (int i = 1; i <= levels; i++) {
                augments[i - 1] = new SimpleAugment(augmentID + "_" + i, augmentMask, i, levelMultiplier, attackDamage, attackSpeed, miningSpeedMult, minePredicate);
            }
            return augments;
        }

    }

    public interface PostMinePredicate {
        boolean onPostMine(ItemStack stack, AbstractEngineeredTool tool, int level, World world, BlockState state, BlockPos pos, LivingEntity miner);
    }
}
