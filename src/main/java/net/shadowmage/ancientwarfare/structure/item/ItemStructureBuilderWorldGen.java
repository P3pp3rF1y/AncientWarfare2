package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.worldgen.WorldStructureGenerator;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemStructureBuilderWorldGen extends ItemBaseStructure implements IItemKeyInterface {

	public ItemStructureBuilderWorldGen(String name) {
		super(name);
		setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		String structure = "guistrings.structure.no_selection";
		ItemStructureSettings viewSettings = ItemStructureSettings.getSettingsFor(stack);
		if (viewSettings.hasName()) {
			structure = viewSettings.name;
		}
		tooltip.add(I18n.format("guistrings.current_structure") + " " + I18n.format(structure));
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
		ItemStructureSettings buildSettings = ItemStructureSettings.getSettingsFor(stack);
		if (buildSettings.hasName()) {
			BlockPos bpHit = BlockTools.getBlockClickedOn(player, player.world, true);
			if (bpHit == null) {
				player.sendMessage(new TextComponentTranslation("block.not_found"));
				return;
			}
			Optional<StructureTemplate> template = StructureTemplateManager.getTemplate(buildSettings.name);
			if (!template.isPresent()) {
				player.sendMessage(new TextComponentTranslation("guistrings.template.not_found"));
				return;
			}
			StructureMap map = AWGameData.INSTANCE.getData(player.world, StructureMap.class);
			WorldStructureGenerator.INSTANCE.attemptStructureGenerationAt(player.world, bpHit, player.getHorizontalFacing(), template.get(), map);
		} else {
			player.sendMessage(new TextComponentTranslation("guistrings.structure.no_selection"));
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!player.world.isRemote && !player.isSneaking()) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_BUILDER, 0, 0, 0);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
}
