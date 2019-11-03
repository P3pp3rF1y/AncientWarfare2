package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;

import javax.annotation.Nullable;
import java.util.Optional;

public class TemplateRuleVanillaSkull extends TemplateRuleBlockTile {
	public static final String PLUGIN_NAME = "vanillaSkull";
	private int skullRotation;
	private Tuple<Integer, TileEntitySkull> tileCache;

	public TemplateRuleVanillaSkull(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, state, turns);
		skullRotation = Rotation.values()[turns % 4].rotate(tag.getInteger("Rot"), 16);
	}

	public TemplateRuleVanillaSkull() {
		super();
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		super.handlePlacement(world, turns, pos, builder);
		WorldTools.getTile(world, pos, TileEntitySkull.class).ifPresent(te -> setTileProperties(turns, te));
	}

	private TileEntitySkull setTileProperties(int turns, TileEntitySkull te) {
		te.setSkullRotation(Rotation.values()[turns % 4].rotate(skullRotation, 16));
		return te;
	}

	@Override
	protected Optional<ItemStack> getStack() {
		return Optional.of(new ItemStack(Items.SKULL, 1, tag.getByte("SkullType")));
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		tag.setInteger("skullRotation", (short) skullRotation);
	}

	@Override
	public void parseRule(NBTTagCompound tag) {
		super.parseRule(tag);
		skullRotation = tag.getInteger("skullRotation");
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}

	@Nullable
	@Override
	public TileEntity getTileEntity(int turns) {
		if (tileCache == null || tileCache.getFirst() != turns) {
			//noinspection ConstantConditions
			TileEntitySkull te = setTileProperties(turns, (TileEntitySkull) super.getTileEntity(turns));
			te.setWorld(new RuleWorld(getState(turns)));
			tileCache = new Tuple<>(turns, te);
		}

		return tileCache.getSecond();
	}
}
