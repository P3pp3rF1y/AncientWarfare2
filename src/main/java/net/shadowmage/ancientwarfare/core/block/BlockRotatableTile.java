package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;

public abstract class BlockRotatableTile extends Block implements IRotatableBlock {

    private final IconRotationMap iconMap = new IconRotationMap();

    protected BlockRotatableTile(Material material) {
        super(material);
    }

    @Override
    public Block setIcon(RelativeSide side, String texName) {
        iconMap.setIcon(this, side, texName);
        return this;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        iconMap.registerIcons(reg);
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        IRotatableTile tile = (IRotatableTile) world.getTileEntity(x, y, z);
        return iconMap.getIcon(this, tile.getPrimaryFacing().ordinal(), side);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return iconMap.getIcon(this, meta, side);
    }

}
