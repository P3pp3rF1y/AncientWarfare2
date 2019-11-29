package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.gui.GuiInfoTool;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.ItemTools;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.Map;

public class ItemInfoTool extends ItemBaseCore {
	public ItemInfoTool() {
		super("info_tool");
		setMaxStackSize(1);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(getMode(stack).toString() + " mode");
	}

	private void printSimpleMessage(EntityPlayer player, IBlockState state) {
		//noinspection ConstantConditions
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

	public void printItemInfo(EntityPlayer player, ItemStack infoTool, ItemStack stack) {
		if (getMode(infoTool) == Mode.INFO) {
			printSimpleMessage(player, stack);
		} else {
			printJSON(player, stack);
		}
	}

	private void printSimpleMessage(EntityPlayer player, ItemStack stack) {
		//noinspection ConstantConditions
		player.sendMessage(new TextComponentString("Item name: " + stack.getItem().getRegistryName().toString()));
		player.sendMessage(new TextComponentString("Meta: " + stack.getMetadata()));
		if (stack.hasTagCompound()) {
			//noinspection ConstantConditions
			player.sendMessage(new TextComponentString("NBT: " + stack.getTagCompound().toString()));
		}
	}

	private void printJSON(EntityPlayer player, ItemStack stack) {
		String json = ItemTools.serializeToJson(stack).toString();
		StringSelection stringSelection = new StringSelection(json);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		player.sendMessage(new TextComponentString(json));
		player.sendMessage(new TextComponentString("Copied to clipboard"));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (world.isRemote) {
			return super.onItemRightClick(world, player, hand);
		}

		RayTraceResult hit = rayTrace(world, player, true);
		if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
			IBlockState state = world.getBlockState(hit.getBlockPos());
			if (getMode(player.getHeldItem(hand)) == Mode.INFO) {
				printSimpleMessage(player, state);
			} else {
				printJSON(player, state);
			}
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}

		if (player.isSneaking()) {
			return new ActionResult<>(EnumActionResult.SUCCESS, cycleMode(player.getHeldItem(hand)));
		} else {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_INFO_TOOL);
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}
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

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		super.registerClient();

		NetworkHandler.registerGui(NetworkHandler.GUI_INFO_TOOL, GuiInfoTool.class);
	}

	enum Mode {
		INFO,
		JSON
	}
}
