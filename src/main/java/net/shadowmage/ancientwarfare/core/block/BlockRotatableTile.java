package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.block.material.Material;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;

public abstract class BlockRotatableTile extends BlockAWCoreBase implements IRotatableBlock {

/*
    private final IconRotationMap iconMap = new IconRotationMap();
*/

    protected BlockRotatableTile(Material material, String regName) {
        super(material, regName);
    }

/*
    @Override
    public Block setIcon(RelativeSide side, String texName) {
        iconMap.setIcon(this, side, texName);
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        iconMap.registerIcons(reg);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        IRotatableTile tile = (IRotatableTile) world.getTileEntity(pos);
        return iconMap.getIcon(this, tile.getPrimaryFacing().ordinal(), side);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconMap.getIcon(this, meta, side);
    }
*/

}
