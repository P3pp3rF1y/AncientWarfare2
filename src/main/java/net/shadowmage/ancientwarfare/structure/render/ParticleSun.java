package net.shadowmage.ancientwarfare.structure.render;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleSun extends Particle {
	public ParticleSun(World world, double x, double y, double z) {
		super(world, x, y, z, 0.0D, 0.0D, 0.0D);
		this.motionX = 0;
		this.motionZ = 0;
		motionY = 0;

		float f = (float) (Math.random() * 0.1f + 0.9f);
		particleRed = 1f * f;
		particleGreen = 213 / 255f * f;
		particleBlue = 74 / 255f * f;
		particleScale *= 0.5F;
		float scale = world.rand.nextFloat() * 0.3f + 0.7f;
		particleScale *= scale;
		particleMaxAge = (int) (16.0D / (Math.random() * 0.8D + 0.2D));
		particleMaxAge = (int) ((float) particleMaxAge * scale);
	}

	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		if (particleAge++ >= particleMaxAge) {
			setExpired();
		}

		setParticleTextureIndex(151 - particleAge * 8 / particleMaxAge);
		move(motionX, motionY, motionZ);

		motionX *= 0.96D;
		motionY *= 0.96D;
		motionZ *= 0.96D;
	}
}
