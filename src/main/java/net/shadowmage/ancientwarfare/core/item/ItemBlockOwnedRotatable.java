package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.owner.IOwnable;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;

public class ItemBlockOwnedRotatable extends ItemBlockBase {
	private IRotatableBlock rotatable;

	public <T extends Block & IRotatableBlock> ItemBlockOwnedRotatable(T block) {
		super(block);
		rotatable = block;
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		EnumFacing facing = BlockRotationHandler.getFaceForPlacement(player, rotatable, side);
		boolean val = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState.withProperty(FACING, facing));
		if (val) {
			WorldTools.getTile(world, pos, IOwnable.class).ifPresent(t -> t.setOwner(player));
		}
		return val;
	}
}
