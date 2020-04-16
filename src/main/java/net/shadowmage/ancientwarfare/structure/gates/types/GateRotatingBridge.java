package net.shadowmage.ancientwarfare.structure.gates.types;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.entity.RotateBoundingBox;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;

public class GateRotatingBridge extends Gate {

	/*
	 * @param id
	 */
	public GateRotatingBridge(int id, String tex, SoundEvent moveSound, SoundEvent hurtSound, SoundEvent breakSound) {
		super(id, tex, moveSound, hurtSound, breakSound);
		setName("gateDrawbridge");
		this.moveSpeed = 1.f;
		this.canSoldierInteract = false;
		setVariant(Variant.WOOD_ROTATING);
	}

	@Override
	public void setCollisionBoundingBox(EntityGate gate) {
		if (gate.pos1 == null || gate.pos2 == null) {
			return;
		}
		BlockPos min = BlockTools.getMin(gate.pos1, gate.pos2);
		BlockPos max = BlockTools.getMax(gate.pos1, gate.pos2);
		updateRenderBoundingBox(gate);
		if (gate.edgePosition == 0) {
			gate.setEntityBoundingBox(new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), (double) max.getX() + 1, (double) max.getY() + 1, (double) max.getZ() + 1));
		} else if (gate.edgePosition < gate.edgeMax) {
			if (!(gate.getEntityBoundingBox() instanceof RotateBoundingBox)) {
				try {
					ObfuscationReflectionHelper.setPrivateValue(Entity.class, gate, new RotateBoundingBox(gate.gateOrientation, min, max.add(1, 1, 1)), "field_70121_D");
				}
				catch (Exception ignored) {
					//noop
				}
			}
			if (gate.getEntityBoundingBox() instanceof RotateBoundingBox) {
				((RotateBoundingBox) gate.getEntityBoundingBox()).rotate(gate.getOpeningStatus() * getMoveSpeed());
			}
		} else {
			int heightAdj = max.getY() - min.getY();
			BlockPos pos3 = max.up(-heightAdj).offset(gate.gateOrientation, heightAdj);
			max = BlockTools.getMax(min, pos3).add(1, 1, 1);
			min = BlockTools.getMin(min, pos3);
			gate.setEntityBoundingBox(new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()));
		}
	}

	@Override
	public boolean canActivate(EntityGate gate, boolean open) {
		if (gate.pos1 == null || gate.pos2 == null) {
			return false;
		}
		if (!open) {
			return super.canActivate(gate, false);
		} else {
			BlockPos min = BlockTools.getMin(gate.pos1, gate.pos2);
			BlockPos max = BlockTools.getMax(gate.pos1, gate.pos2);
			int heightAdj = max.getY() - min.getY();
			BlockPos pos3 = max.up(-heightAdj).offset(gate.gateOrientation, heightAdj);
			max = BlockTools.getMax(min, pos3);
			min = BlockTools.getMin(min, pos3);
			Block id;
			for (int x = min.getX(); x <= max.getX(); x++) {
				for (int z = min.getZ(); z <= max.getZ(); z++) {
					BlockPos posToCheck = new BlockPos(x, min.getY(), z);
					id = gate.world.getBlockState(posToCheck).getBlock();
					if (!gate.world.isAirBlock(posToCheck) && id != AWStructureBlocks.GATE_PROXY) {
						return false;
					}

				}
			}
			return true;
		}
	}

	@Override
	public void setInitialBounds(EntityGate gate, BlockPos pos1, BlockPos pos2) {
		BlockPos min = BlockTools.getMin(pos1, pos2);
		BlockPos max = BlockTools.getMax(pos1, pos2);
		boolean wideOnXAxis = min.getX() != max.getX();
		float width = wideOnXAxis ? max.getX() - min.getX() + 1 : max.getZ() - min.getZ() + 1;
		float xOffset = wideOnXAxis ? width * 0.5f : 0.5f;
		float zOffset = wideOnXAxis ? 0.5f : width * 0.5f;
		gate.setPositions(min, max);
		gate.setRenderBoundingBox(getRenderBoundingBox(gate, min, max));
		gate.edgeMax = 90.f;
		gate.setPosition(min.getX() + xOffset, min.getY(), min.getZ() + zOffset);

	}

	@Override
	protected AxisAlignedBB getRenderBoundingBox(EntityGate gate, BlockPos min, BlockPos max) {
		int heightAdj = max.getY() - min.getY();
		BlockPos pos3 = max.up(-heightAdj).offset(gate.gateOrientation, heightAdj);
		max = BlockTools.getMax(max, pos3).add(1, 1, 1);
		min = BlockTools.getMin(min, pos3);
		return new AxisAlignedBB(min, max);
	}

	@Override
	public void onGateFinishOpen(EntityGate gate) {
		BlockPos min = BlockTools.getMin(gate.pos1, gate.pos2);
		BlockPos max = BlockTools.getMax(gate.pos1, gate.pos2);
		int heightAdj = max.getY() - min.getY();
		BlockPos pos3 = max.up(-heightAdj).offset(gate.gateOrientation, heightAdj);
		max = BlockTools.getMax(min, pos3);
		min = BlockTools.getMin(min, pos3);
		closeBetween(gate, min, max);
	}

	@Override
	public void onGateStartClose(EntityGate gate) {
		BlockPos min = BlockTools.getMin(gate.pos1, gate.pos2);
		BlockPos max = BlockTools.getMax(gate.pos1, gate.pos2);
		boolean widestOnXAxis = gate.pos1.getX() != gate.pos2.getX();
		int heightAdj = max.getY() - min.getY();
		BlockPos pos3 = max.up(-heightAdj).offset(gate.gateOrientation, heightAdj);
		max = BlockTools.getMax(min, pos3);
		min = BlockTools.getMin(min, pos3);
		Block id;
		for (int x = min.getX(); x <= max.getX(); x++) {
			for (int y = min.getY(); y <= max.getY(); y++) {
				for (int z = min.getZ(); z <= max.getZ(); z++) {
					if ((widestOnXAxis && z == gate.pos1.getZ()) || (!widestOnXAxis && x == gate.pos1.getX())) {
						continue;
					}
					id = gate.world.getBlockState(new BlockPos(x, y, z)).getBlock();
					if (id == AWStructureBlocks.GATE_PROXY) {
						gate.world.setBlockToAir(new BlockPos(x, y, z));
					}
				}
			}
		}
	}
}
