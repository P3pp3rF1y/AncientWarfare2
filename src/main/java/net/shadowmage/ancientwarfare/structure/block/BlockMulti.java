package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

public class BlockMulti extends BlockBaseStructure {
	public static final PropertyBool INVISIBLE = PropertyBool.create("invisible");

	public BlockMulti(Material material, String regName) {
		super(material, regName);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, INVISIBLE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(INVISIBLE, (meta & 1) == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(INVISIBLE) ? 1 : 0;
	}

}
