package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.entity.EntitySeat;

import java.util.List;

public class BlockSeat extends BlockBaseStructure {
	public BlockSeat(Material material, String regName) {
		super(material, regName);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && !isOccupied(world, pos)) {
			EntitySeat seatEntity = new EntitySeat(world, new Vec3d(pos).add(getSeatOffset()));
			world.spawnEntity(seatEntity);
			playerIn.startRiding(seatEntity);
		}
		return true;
	}

	protected Vec3d getSeatOffset() {
		return new Vec3d(0.5, 0.5, 0.5);
	}

	private boolean isOccupied(World world, BlockPos pos) {
		List<EntitySeat> seats = world.getEntitiesWithinAABB(EntitySeat.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(1));
		for (EntitySeat seat : seats) {
			if (seat.getSeatPos().equals(pos)) {
				return seat.isBeingRidden();
			}
		}
		return false;
	}
}
