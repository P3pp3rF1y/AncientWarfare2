package net.shadowmage.ancientwarfare.automation.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.automation.block.BlockWarehouseStockLinker;
import net.shadowmage.ancientwarfare.automation.init.AWAutomationBlocks;
import net.shadowmage.ancientwarfare.core.item.ItemBlockOwnedRotatable;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockWarehouseStockLinker extends ItemBlockOwnedRotatable {
	public static final String WAREHOUSE_POS_TAG = "warehousePosTag";

	public ItemBlockWarehouseStockLinker(BlockWarehouseStockLinker block) {
		super(block);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (stack.hasTagCompound()) {
			//noinspection ConstantConditions
			tooltip.add(I18n.format("tile.warehouse_stock_linker.tooltip", formatPos(NBTUtil.getPosFromTag(stack.getTagCompound().getCompoundTag(WAREHOUSE_POS_TAG)))));
		}
	}

	private void addMessage(EntityPlayer player, BlockPos pos) {
		player.sendMessage(new TextComponentTranslation("guistrings.automation.warehouse_set", formatPos(pos)));
	}

	private String formatPos(BlockPos pos) {
		return String.format("x: %d y: %d z: %d", pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (player.isSneaking() && world.isRemote) {
			return EnumActionResult.PASS;
		}
		if (player.isSneaking() && !world.isRemote) {
			RayTraceResult hit = RayTraceUtils.getPlayerTarget(player, 5, 0);
			if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK && world.getBlockState(hit.getBlockPos()).getBlock() == AWAutomationBlocks.WAREHOUSE_CONTROL) {
				ItemStack stack = player.getHeldItem(hand);
				stack.setTagInfo(WAREHOUSE_POS_TAG, NBTUtil.createPosTag(hit.getBlockPos()));
				addMessage(player, hit.getBlockPos());
				return EnumActionResult.SUCCESS;
			}
		}
		return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}
}
