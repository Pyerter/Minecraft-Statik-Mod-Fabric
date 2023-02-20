package pyerter.statik.particle;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.World;

public class ModParticleUtil {

    public static void spawnParticles(World world, BlockPos pos, ParticleEffect effect, IntProvider count, Direction direction, double offsetMultiplier) {
        int i = count.get(world.random);

        for(int j = 0; j < i; ++j) {
            spawnParticle(world, pos, direction, effect, MathHelper.nextDouble(ModParticles.RANDOM, 0, 0.5), offsetMultiplier);
        }
    }

    public static void spawnParticle(World world, BlockPos pos, Direction direction, ParticleEffect effect, double directionSpeed, double offsetMultiplier) {
        Vec3d vec3d = Vec3d.ofCenter(pos);
        int i = direction.getOffsetX();
        int j = direction.getOffsetY();
        int k = direction.getOffsetZ();
        double d = vec3d.x + (i == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double)i * offsetMultiplier);
        double e = vec3d.y + (j == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double)j * offsetMultiplier);
        double f = vec3d.z + (k == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double)k * offsetMultiplier);
        double g = i != 0 ? directionSpeed * i : 0.0;
        double h = j != 0 ? directionSpeed * j : 0.0;
        double l = k != 0 ? directionSpeed * k : 0.0;
        world.addParticle(effect, d, e, f, g, h, l);
    }

}
