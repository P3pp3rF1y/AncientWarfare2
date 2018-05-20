package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.structure.gui.GuiTownSelection;
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
	public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key) {
		return key == ItemKey.KEY_0;
	}

	@Override
	public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
		if (player == null || player.world.isRemote) {
			return;
		}
		Optional<TownTemplate> template = TownTemplateManager.INSTANCE.getTemplate(getTownName(stack));
		if (!template.isPresent()) {
			player.sendMessage(new TextComponentString("No town template set"));
			return;
		}
		long t1 = System.nanoTime();
		WorldTownGenerator.INSTANCE.generate(player.world, MathHelper.floor(player.posX), MathHelper.floor(player.posZ), template.get());
		long t2 = System.nanoTime();
		AWLog.logDebug("Total Town gen nanos (incl. validation): " + (t2 - t1));
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

	@Override
	public void registerClient() {
		super.registerClient();

		NetworkHandler.registerGui(NetworkHandler.GUI_TOWN_BUILDER, GuiTownSelection.class);
	}
}
