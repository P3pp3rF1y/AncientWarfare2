package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileDistributorHeavy;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileDistributorLight;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileDistributorMedium;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueSidedCell;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;

public class BlockTorqueDistributor extends BlockTorqueBase {
    static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);

    protected BlockTorqueDistributor(String regName) {
        super(Material.ROCK, regName);
    }

    @Override
    protected void addProperties(BlockStateContainer.Builder builder) {
        builder.add(TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, Type.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).getMeta();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        switch (state.getValue(TYPE)) {
            case LIGHT:
                return new TileDistributorLight();
            case MEDIUM:
                return new TileDistributorMedium();
            case HEAVY:
                return new TileDistributorHeavy();
        }
        return new TileDistributorLight();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
        items.add(new ItemStack(this, 1, 2));
    }

/*
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Blocks.IRON_BLOCK.getIcon(0, 0);
    }

*/
    @Override
    public RotationType getRotationType() {
        return RotationType.SIX_WAY;
    }

    @Override
    public boolean invertFacing() {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        //TODO static AABBs and combination created based on the boolean array

        float min = 0.1875f, max = 0.8125f;
        float x1 = min, y1 = min, z1 = min, x2 = max, y2 = max, z2 = max;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileTorqueSidedCell) {
            TileTorqueSidedCell tile = (TileTorqueSidedCell) world.getTileEntity(pos);
            boolean[] sides = tile.getConnections();
            if (sides[0]) {
                y1 = 0.f;
            }
            if (sides[1]) {
                y2 = 1.f;
            }
            if (sides[2]) {
                z1 = 0.f;
            }
            if (sides[3]) {
                z2 = 1.f;
            }
            if (sides[4]) {
                x1 = 0.f;
            }
            if (sides[5]) {
                x2 = 1.f;
            }
        }
        return new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
    }

    public enum Type implements IStringSerializable {
        LIGHT(0),
        MEDIUM(1),
        HEAVY(2);

        private int meta;
        Type(int meta) {
            this.meta = meta;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public int getMeta() {
            return meta;
        }

        public static Type byMetadata(int meta) {
            return values()[meta];
        }
    }
}
