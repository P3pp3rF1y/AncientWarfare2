package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.event.IBoxRenderer;
import net.shadowmage.ancientwarfare.structure.gui.GuiStructureSelection;
import net.shadowmage.ancientwarfare.structure.render.PreviewRenderer;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemStructureBuilder extends ItemBaseStructure implements IItemKeyInterface, IBoxRenderer {
	private static final String LOCK_POS_TAG = "lockPos";

	public ItemStructureBuilder(String name) {
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
			StructureTemplate template = StructureTemplateManager.INSTANCE.getTemplate(buildSettings.name);
			if (template == null) {
				player.sendMessage(new TextComponentTranslation("guistrings.template.not_found"));
				return;
			}
			BlockPos bpHit = BlockTools.getBlockClickedOn(player, player.world, true);
			if (bpHit == null) {
				return;
			}//no hit position, clicked on air
			StructureBuilder builder = new StructureBuilder(player.world, template, player.getHorizontalFacing(), bpHit);
			builder.getTemplate().getValidationSettings().preGeneration(player.world, bpHit, player.getHorizontalFacing(), builder.getTemplate(), builder.getBoundingBox());
			builder.instantConstruction();
			builder.getTemplate().getValidationSettings().postGeneration(player.world, bpHit, builder.getBoundingBox());
			if (!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}
		} else {
			player.sendMessage(new TextComponentTranslation("guistrings.structure.no_selection"));
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!player.world.isRemote && !player.isSneaking() && player.capabilities.isCreativeMode) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_BUILDER, 0, 0, 0);
		}
		ItemStack stack = player.getHeldItem(hand);
		if (player.isSneaking() && getLockPosition(stack).isPresent()) {
			removeLockPosition(stack);
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	private void removeLockPosition(ItemStack stack) {
		//noinspection ConstantConditions
		stack.getTagCompound().removeTag(LOCK_POS_TAG);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		ItemStructureSettings buildSettings = ItemStructureSettings.getSettingsFor(stack);
		if (player.isSneaking() && buildSettings.hasName()) {
			if (!worldIn.isRemote) {
				lockPosition(stack, pos.offset(facing), player);
			}
			return EnumActionResult.SUCCESS;
		}

		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	private void lockPosition(ItemStack stack, BlockPos pos, EntityPlayer player) {
		NBTTagCompound tag = stack.getTagCompound();
		tag.setLong(LOCK_POS_TAG, pos.toLong());
		tag.setByte("lockFacing", (byte) player.getHorizontalFacing().getHorizontalIndex());
	}

	private Optional<Tuple<BlockPos, EnumFacing>> getLockPosition(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		//noinspection ConstantConditions
		return tag.hasKey(LOCK_POS_TAG) ? Optional.of(new Tuple<>(BlockPos.fromLong(tag.getLong(LOCK_POS_TAG)), EnumFacing.HORIZONTALS[tag.getByte("lockFacing")])) : Optional.empty();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBox(EntityPlayer player, EnumHand hand, ItemStack stack, float delta) {
		ItemStructureSettings settings = ItemStructureSettings.getSettingsFor(stack);
		if (!settings.hasName()) {
			return;
		}
		String name = settings.name();
		StructureTemplate structure = StructureTemplateManager.INSTANCE.getTemplate(name);
		if (structure == null) {
			return;
		}
		Optional<Tuple<BlockPos, EnumFacing>> lockPosition = getLockPosition(stack);
		BlockPos hit;
		EnumFacing facing;
		if (lockPosition.isPresent()) {
			hit = lockPosition.get().getFirst();
			facing = lockPosition.get().getSecond();
		} else {
			hit = BlockTools.getBlockClickedOn(player, player.world, true);
			if (hit == null) {
				return;
			}
			facing = player.getHorizontalFacing();
		}

		StructureBB bb = new StructureBB(hit, facing, structure.getSize(), structure.getOffset());
		int turns = (facing.getHorizontalIndex() + 2) % 4;
		Util.renderBoundingBox(player, bb.min, bb.max, delta);
		PreviewRenderer.renderTemplatePreview(player, hand, stack, delta, structure, bb, turns);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		super.registerClient();

		NetworkHandler.registerGui(NetworkHandler.GUI_BUILDER, GuiStructureSelection.class);
	}

}
