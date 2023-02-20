package pyerter.statik.particle.custom;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import pyerter.statik.particle.ModParticles;

public class ManaParticle extends SpriteBillboardParticle {
    protected ManaParticle(ClientWorld clientWorld, double x, double y, double z,
                           SpriteProvider spriteProvider, double xDelta, double yDelta, double zDelta) {
        super(clientWorld, x, y, z);

        this.velocityMultiplier = 0.95f;
        this.x = xDelta;
        this.y = yDelta;
        this.z = zDelta;

        this.scale *= 1.25f;
        this.maxAge = 10;
        this.setSpriteForAge(spriteProvider);

        this.red = 1f;
        this.green = 1f;
        this.blue = 1f;

        this.velocityX = velocityX + (Math.random() * 2.0 - 1.0) * 0.4000000059604645;
        this.velocityY = velocityY + (Math.random() * 2.0 - 1.0) * 0.4000000059604645;
        this.velocityZ = velocityZ + (Math.random() * 2.0 - 1.0) * 0.4000000059604645;
        double d = (Math.random() + Math.random() + 1.0) * 0.15000000596046448;
        double e = Math.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
        this.velocityX = this.velocityX / e * d * 0.4000000059604645;
        this.velocityY = this.velocityY / e * d * 0.4000000059604645;
        this.velocityZ = this.velocityZ / e * d * 0.4000000059604645;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public Particle move(float speed) {
        this.velocityX *= (double)speed;
        this.velocityY = (this.velocityY - 0.01) * (double)speed + 0.01;
        this.velocityZ *= (double)speed;
        return this;
    }

    public void tick() {
        super.tick();
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteProvider) {
            this.sprites = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            ManaParticle glowParticle = new ManaParticle(clientWorld, d, e, f, sprites, 0.5 - ModParticles.RANDOM.nextDouble(), h, 0.5 - ModParticles.RANDOM.nextDouble());
            if (clientWorld.random.nextBoolean()) {
                glowParticle.setColor(0.6F, 1.0F, 0.8F);
            } else {
                glowParticle.setColor(0.08F, 0.4F, 0.4F);
            }

            glowParticle.velocityY *= 0.3;

            if (g == 0.0 && i == 0.0) {
                glowParticle.velocityX *= 0.10000000149011612;
                glowParticle.velocityZ *= 0.10000000149011612;
            }

            glowParticle.setMaxAge((int)(12.0 / (clientWorld.random.nextDouble() * 0.5 + 0.5)));
            return glowParticle;
        }
    }
}
