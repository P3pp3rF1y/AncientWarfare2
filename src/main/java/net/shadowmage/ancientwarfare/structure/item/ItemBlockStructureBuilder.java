package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.item.ItemBlockBase;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.event.IBoxRenderer;
import net.shadowmage.ancientwarfare.structure.render.PreviewRenderer;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilderTicked;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureBuilder;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockStructureBuilder extends ItemBlockBase implements IBoxRenderer {
	private static final String STRUCTURE_NAME_TAG = "structureName";

	public ItemBlockStructureBuilder(Block block) {
		super(block);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
		String name = "corrupt_item";
		//noinspection ConstantConditions
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(STRUCTURE_NAME_TAG)) {
			name = stack.getTagCompound().getString(STRUCTURE_NAME_TAG);
		}
		tooltip.add(I18n.format("guistrings.structure.structure_name") + ": " + name);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		//noinspection ConstantConditions
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(STRUCTURE_NAME_TAG)) {
			return false;
		}
		boolean val = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
		if (!world.isRemote && val) {
			WorldTools.getTile(world, pos, TileStructureBuilder.class).ifPresent(t -> setupStructureBuilder(stack, player, world, pos, t));
		}
		return val;
	}

	@SuppressWarnings("ConstantConditions")
	private void setupStructureBuilder(ItemStack stack, EntityPlayer player, World world, BlockPos pos, TileStructureBuilder tb) {
		tb.setOwner(player);
		NBTTagCompound tag = stack.getTagCompound();
		String name = tag.getString(STRUCTURE_NAME_TAG);
		EnumFacing face = player.getHorizontalFacing();
		setupStructureBuilder(world, pos, tb, name, face);
		if (tag.hasKey("progress")) {
			StructureBuilderTicked builder = tb.getBuilder();
			builder.deserializeProgressData(tag.getCompoundTag("progress"));
		}
	}

	public void setupStructureBuilder(World world, BlockPos pos, TileStructureBuilder tb, String name, EnumFacing face) {
		StructureTemplate t = StructureTemplateManager.INSTANCE.getTemplate(name);
		if (t != null) {
			BlockPos p = pos.offset(face, t.getSize().getZ() - 1 - t.getOffset().getZ() + 1);
			tb.setBuilder(new StructureBuilderTicked(world, t, face, p));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBox(EntityPlayer player, EnumHand hand, ItemStack stack, float delta) {
		//noinspection ConstantConditions
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(STRUCTURE_NAME_TAG)) {
			return;
		}
		String name = stack.getTagCompound().getString(STRUCTURE_NAME_TAG);
		StructureTemplate t = StructureTemplateManager.INSTANCE.getTemplate(name);
		if (t == null) {
			return;
		}
		BlockPos hit = BlockTools.getBlockClickedOn(player, player.world, true);
		if (hit == null) {
			return;
		}
		Util.renderBoundingBox(player, hit, hit, delta);
		EnumFacing face = player.getHorizontalFacing();
		BlockPos p2 = hit.offset(face, t.getSize().getZ() - 1 - t.getOffset().getZ() + 1);
		StructureBB bb = new StructureBB(p2, face, t.getSize(), t.getOffset());
		Util.renderBoundingBox(player, bb.min, bb.max, delta);
		PreviewRenderer.renderTemplatePreview(player, hand, stack, delta, t, bb, (face.getHorizontalIndex() + 2) % 4);
	}
}
