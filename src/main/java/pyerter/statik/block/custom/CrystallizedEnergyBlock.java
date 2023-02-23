package pyerter.statik.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TransparentBlock;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import pyerter.statik.particle.ModParticleUtil;
import pyerter.statik.particle.ModParticles;

import java.util.List;

public class CrystallizedEnergyBlock extends TransparentBlock {
    public CrystallizedEnergyBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (world.getBlockState(pos.offset(Direction.UP)).isOf(Blocks.AIR))
            ModParticleUtil.spawnParticles(world, pos, ModParticles.MANA_PARTICLE, UniformIntProvider.create(1, 2), Direction.UP, 0.5);

        if (world.getBlockState(pos.offset(Direction.DOWN)).isOf(Blocks.AIR))
            ModParticleUtil.spawnParticles(world, pos, ModParticles.MANA_PARTICLE, UniformIntProvider.create(1, 2), Direction.DOWN, 0.5);
    }

    @Override
    public BlockSoundGroup getSoundGroup(BlockState state) {
        return BlockSoundGroup.GLASS;
    }
}
