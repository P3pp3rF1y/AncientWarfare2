package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.Map;

public class ItemInfoTool extends ItemBaseCore {
	public ItemInfoTool() {
		super("info_tool");
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(getMode(stack).toString() + " mode");
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (!world.isRemote) {
			IBlockState state = world.getBlockState(pos);
			if (getMode(player.getHeldItem(hand)) == Mode.INFO) {
				printSimpleMessage(player, state);
			} else {
				printJSON(player, state);
			}
			return EnumActionResult.SUCCESS;
		}

		return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
	}

	private void printSimpleMessage(EntityPlayer player, IBlockState state) {
		player.sendMessage(new TextComponentString("Block name: " + state.getBlock().getRegistryName().toString()));
		if (!state.getProperties().isEmpty()) {
			player.sendMessage(new TextComponentString("Properties:"));
			for (Map.Entry<IProperty<?>, Comparable<?>> prop : state.getProperties().entrySet()) {
				player.sendMessage(new TextComponentString(prop.getKey().getName() + " : " + prop.getValue().toString()));
			}
		}
	}

	private void printJSON(EntityPlayer player, IBlockState state) {
		String json = BlockTools.serializeToJson(state).toString();
		StringSelection stringSelection = new StringSelection(json);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		player.sendMessage(new TextComponentString(json));
		player.sendMessage(new TextComponentString("Copied to clipboard"));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote && player.isSneaking()) {
			return new ActionResult<>(EnumActionResult.SUCCESS, cycleMode(player.getHeldItem(hand)));
		}

		return super.onItemRightClick(world, player, hand);
	}

	private ItemStack cycleMode(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		//noinspection ConstantConditions
		stack.getTagCompound().setString("mode", getMode(stack) == Mode.INFO ? "json" : "info");
		return stack;
	}

	private Mode getMode(ItemStack stack) {
		if (stack.hasTagCompound()) {
			//noinspection ConstantConditions
			return stack.getTagCompound().getString("mode").equals("json") ? Mode.JSON : Mode.INFO;
		}
		return Mode.INFO;
	}

	enum Mode {
		INFO,
		JSON
	}
}
