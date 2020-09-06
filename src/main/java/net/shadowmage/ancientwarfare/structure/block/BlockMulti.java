package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.tile.TileMulti;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public abstract class BlockMulti<T extends TileMulti> extends BlockBaseStructure {
	public static final PropertyBool INVISIBLE = PropertyBool.create("invisible");
	private final Supplier<T> instantiateTe;
	private final Class<T> teClass;

	public BlockMulti(Material material, String regName, Supplier<T> instantiateTe, Class<T> teClass) {
		super(material, regName);
		this.instantiateTe = instantiateTe;
		this.teClass = teClass;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return instantiateTe.get();
	}

	@Override
	protected final BlockStateContainer createBlockState() {
		List<IProperty> additionaProperties = getAdditionalProperties();
		IProperty[] properties = new IProperty[1 + additionaProperties.size()];
		properties[0] = INVISIBLE;
		int i = 1;
		for (IProperty property : additionaProperties) {
			properties[i] = property;
			i++;
		}
		return new BlockStateContainer(this, properties);
	}

	protected List<IProperty> getAdditionalProperties() {
		return Collections.emptyList();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(INVISIBLE, (meta & 1) == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return Boolean.TRUE.equals(state.getValue(INVISIBLE)) ? 1 : 0;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		WorldTools.getTile(world, pos, teClass).ifPresent(te -> {
			setPlacementProperties(world, pos, placer, stack, te);
			te.setPlacementDirection(world, pos, state, placer.getHorizontalFacing(), placer.rotationYaw);
			placeInvisibleBlocks(world, state, te);
			te.setMainPosOnAdditionalBlocks();
		});
	}

	private void placeInvisibleBlocks(World world, IBlockState state, T te) {
		te.getAdditionalPositions(state).forEach(additionalPos -> world.setBlockState(additionalPos, getDefaultState().withProperty(INVISIBLE, true)));
	}

	protected abstract void setPlacementProperties(World world, BlockPos pos, EntityLivingBase placer, ItemStack stack, T te);
}
