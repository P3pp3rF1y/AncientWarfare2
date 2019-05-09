package net.shadowmage.ancientwarfare.structure.render;

import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.world.World;

public class ParticleColoredFlame extends ParticleFlame {
	public ParticleColoredFlame(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int color) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		particleRed *= (float) (color >> 16 & 255) / 255.0F;
		particleGreen *= (float) (color >> 8 & 255) / 255.0F;
		particleBlue *= (float) (color & 255) / 255.0F;
	}
}
