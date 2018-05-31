package net.shadowmage.ancientwarfare.npc.entity.vehicle;

import net.minecraft.world.World;

public interface ITarget {
	double getX();

	double getY();

	double getZ();

	boolean exists(World world);

}
