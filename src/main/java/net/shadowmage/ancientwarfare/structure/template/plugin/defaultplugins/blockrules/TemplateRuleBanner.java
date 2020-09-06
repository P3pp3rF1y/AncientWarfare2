package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;

import java.util.Optional;

public class TemplateRuleBanner extends TemplateRuleBlockTile {
	public static final String PLUGIN_NAME = "banner";

	public TemplateRuleBanner(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, state.getBlock() == Blocks.STANDING_BANNER ? BlockTools.rotateFacing(state.withRotation(Rotation.values()[turns % 4]), turns) : state, turns);
	}

	public TemplateRuleBanner() {
		super();
	}

	@Override
	protected Optional<ItemStack> getStack() {
		EnumDyeColor baseColor = EnumDyeColor.byDyeDamage(tag.getInteger("Base"));
		NBTTagList patterns = tag.getTagList("Patterns", 10);

		return Optional.of(ItemBanner.makeBanner(baseColor, patterns));
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		if (state.getBlock() == Blocks.STANDING_BANNER) {
			builder.placeBlock(pos, getState(turns), buildPass);
			WorldTools.getTile(world, pos).ifPresent(t -> {
				tag.setInteger("x", pos.getX());
				tag.setInteger("y", pos.getY());
				tag.setInteger("z", pos.getZ());
				t.readFromNBT(tag);
			});
		} else {
			super.handlePlacement(world, turns, pos, builder);
		}
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}

	@Override
	public IBlockState getState(int turns) {
		return super.getState(turns).withRotation(Rotation.values()[turns % 4]);
	}
}
