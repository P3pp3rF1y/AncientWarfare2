package net.shadowmage.ancientwarfare.npc.entity.vehicle;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public interface ITarget {
	double getX();

	double getY();

	double getZ();

	AxisAlignedBB getBoundigBox();

	boolean exists(World world);
}
