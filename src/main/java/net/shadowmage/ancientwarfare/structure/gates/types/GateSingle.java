package net.shadowmage.ancientwarfare.structure.gates.types;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;

public class GateSingle extends Gate {
	/*
	 * @param id
	 * @param textureLocation
	 */
	public GateSingle(int id, String textureLocation, SoundEvent moveSound, SoundEvent hurtSound, SoundEvent breakSound, int maxHealth) {
		super(id, textureLocation, moveSound, hurtSound, breakSound, maxHealth);
	}

	@Override
	public void setInitialBounds(EntityGate gate, BlockPos pos1, BlockPos pos2) {
		BlockPos min = BlockTools.getMin(pos1, pos2);
		BlockPos max = BlockTools.getMax(pos1, pos2);
		boolean wideOnXAxis = min.getX() != max.getX();
		float width = wideOnXAxis ? max.getX() - min.getX() + 1 : max.getZ() - min.getZ() + 1;
		float xOffset = wideOnXAxis ? width * 0.5f : 0.5f;
		float zOffset = wideOnXAxis ? 0.5f : width * 0.5f;
		gate.setPositions(pos1, pos2);
		gate.setRenderBoundingBox(new AxisAlignedBB(min, max));
		gate.edgeMax = width;
		gate.setPosition(min.getX() + xOffset, min.getY(), min.getZ() + zOffset);
	}
}
