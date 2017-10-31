package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueSidedCell;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;

public abstract class BlockTorqueTransport extends BlockTorqueBase {
    public static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);

    protected BlockTorqueTransport(String regName) {
        super(Material.ROCK, regName);
        this.setLightOpacity(1);
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
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getStateFromMeta(placer.getHeldItem(hand).getMetadata());
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
        list.add(new ItemStack(this, 1, 2));
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.SIX_WAY;
    }

    @Override
    public boolean invertFacing() {
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
