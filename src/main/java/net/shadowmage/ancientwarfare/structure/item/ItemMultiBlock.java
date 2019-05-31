package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.item.ItemBlockBase;

public class ItemMultiBlock extends ItemBlockBase {
	private final Vec3i minOffset;
	private final Vec3i maxOffset;

	public ItemMultiBlock(Block block, Vec3i minOffset, Vec3i maxOffset) {
		super(block);
		this.minOffset = minOffset;
		this.maxOffset = maxOffset;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
		if (!super.canPlaceBlockOnSide(world, pos, side, player, stack)) {
			return false;
		}
		for (BlockPos p : BlockPos.getAllInBox(pos.add(minOffset), pos.add(maxOffset).up())) {
			if (p.equals(pos)) {
				continue;
			}
			IBlockState state = world.getBlockState(p);
			if (!state.getBlock().isReplaceable(world, p)) {
				return false;
			}
		}

		return true;
	}
}
