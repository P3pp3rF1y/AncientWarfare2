package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.structure.gui.GuiTownSelection;
import net.shadowmage.ancientwarfare.structure.town.TownBoundingArea;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate;
import net.shadowmage.ancientwarfare.structure.town.TownTemplateManager;
import net.shadowmage.ancientwarfare.structure.town.WorldTownGenerator;

import java.util.Optional;

public class ItemTownBuilder extends ItemBaseStructure implements IItemKeyInterface {

	public ItemTownBuilder(String name) {
		super(name);
		setMaxStackSize(1);
		//TODO make texture (uses structure builder one currently)
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
		return false;
	}

	@Override
	public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		return altFunction == ItemAltFunction.ALT_FUNCTION_1;
	}

	@Override
	public void onKeyAction(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		if (player == null || player.world.isRemote) {
			return;
		}

		RayTraceResult rayTraceResult = rayTrace(player.world, player, false);
		if (rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) {
			return;
		}

		Optional<TownTemplate> template = TownTemplateManager.INSTANCE.getTemplate(getTownName(stack));
		if (!template.isPresent()) {
			player.sendMessage(new TextComponentString("No town template set"));
			return;
		}

		long t1 = System.nanoTime();
		WorldTownGenerator.INSTANCE.generate(player.world, getTownArea(rayTraceResult.getBlockPos(), player.getHorizontalFacing(), getLength(stack), getWidth(stack)), template.get());
		long t2 = System.nanoTime();
		AWLog.logDebug("Total Town gen nanos (incl. validation): " + (t2 - t1));
	}

	private TownBoundingArea getTownArea(BlockPos pos, EnumFacing horizontalFacing, int chunkLength, int chunkWidth) {
		int minY = pos.getY() - 3;
		int maxY = Math.min(pos.getY() + 14, 255);

		if (horizontalFacing.getAxis() == EnumFacing.Axis.X) {
			int chunkMinX = (pos.getX() >> 4) - (horizontalFacing.getFrontOffsetX() < 0 ? chunkLength : 0);
			int chunkMaxX = (pos.getX() >> 4) + (horizontalFacing.getFrontOffsetX() > 0 ? chunkLength : 0);
			int chunkMinZ = (pos.getZ() >> 4) - (chunkWidth / 2);
			int chunkMaxZ = chunkMinZ + chunkWidth;
			return new TownBoundingArea(chunkMinX, chunkMinZ, chunkMaxX, chunkMaxZ, minY, maxY);
		}

		int chunkMinX = (pos.getX() >> 4) - (chunkWidth / 2);
		int chunkMaxX = chunkMinX + chunkWidth;
		int chunkMinZ = (pos.getZ() >> 4) - (horizontalFacing.getFrontOffsetZ() < 0 ? chunkLength : 0);
		int chunkMaxZ = (pos.getZ() >> 4) + (horizontalFacing.getFrontOffsetZ() > 0 ? chunkLength : 0);
		return new TownBoundingArea(chunkMinX, chunkMinZ, chunkMaxX, chunkMaxZ, minY, maxY);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!player.world.isRemote && !player.isSneaking()) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_TOWN_BUILDER, 0, 0, 0);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	public static String getTownName(ItemStack townBuilder) {
		return townBuilder.getTagCompound() == null ? "" : townBuilder.getTagCompound().getString("townName");
	}

	public static void setTownName(ItemStack townBuilder, String structName) {
		townBuilder.setTagInfo("townName", new NBTTagString(structName));
	}

	public static void setWidth(ItemStack townBuilder, int width) {
		townBuilder.setTagInfo("width", new NBTTagInt(width));
	}

	public static void setLength(ItemStack townBuilder, int length) {
		townBuilder.setTagInfo("length", new NBTTagInt(length));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		super.registerClient();

		NetworkHandler.registerGui(NetworkHandler.GUI_TOWN_BUILDER, GuiTownSelection.class);
	}

	public static int getWidth(ItemStack townBuilder) {
		//noinspection ConstantConditions
		return townBuilder.hasTagCompound() ? townBuilder.getTagCompound().getInteger("width") : 0;
	}

	public static int getLength(ItemStack townBuilder) {
		//noinspection ConstantConditions
		return townBuilder.hasTagCompound() ? townBuilder.getTagCompound().getInteger("length") : 0;
	}
}
