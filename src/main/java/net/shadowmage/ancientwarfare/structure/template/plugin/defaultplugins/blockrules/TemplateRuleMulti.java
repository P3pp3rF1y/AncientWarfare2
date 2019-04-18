package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.tile.TileMulti;

import javax.annotation.Nullable;
import java.util.Optional;

import static net.shadowmage.ancientwarfare.structure.block.BlockMulti.INVISIBLE;

public abstract class TemplateRuleMulti<T extends TileMulti> extends TemplateRuleBlockTile<T> {
	private final Class<T> teClass;
	private boolean mainBlock = false;

	public TemplateRuleMulti(World world, BlockPos pos, IBlockState state, int turns, Class<T> teClass) {
		super(world, pos, state, turns);
		this.teClass = teClass;

		Optional<TileMulti> te = WorldTools.getTile(world, pos, TileMulti.class);
		if (!te.isPresent()) {
			return;
		}
		TileMulti tileMulti = te.get();
		mainBlock = !tileMulti.getMainBlockPos().isPresent();
	}

	public TemplateRuleMulti(Class<T> teClass) {
		super();
		this.teClass = teClass;
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		if (mainBlock) {
			super.handlePlacement(world, turns, pos, builder);
			WorldTools.getTile(world, pos, teClass).ifPresent(te -> {
				te.getAdditionalPositions(state).forEach(additionalPos -> world.setBlockState(additionalPos, world.getBlockState(pos).getBlock().getDefaultState().withProperty(INVISIBLE, true)));
				te.setMainPosOnAdditionalBlocks();
			});
		}
	}

	@Nullable
	@Override
	public TileEntity getTileEntity(int turns) {
		return super.getTileEntity(turns);
	}

	@Override
	public boolean shouldPlaceOnBuildPass(World world, int turns, BlockPos pos, int buildPass) {
		return buildPass == 0;
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}

	@Override
	public boolean shouldReuseRule(World world, IBlockState state, int turns, BlockPos pos) {
		return false;
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		if (mainBlock) {
			tag.setBoolean("mainBlock", mainBlock);
		}
	}

	@Override
	public void parseRule(NBTTagCompound tag) {
		super.parseRule(tag);
		mainBlock = tag.getBoolean("mainBlock");
	}
}
