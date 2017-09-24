package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaft;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaftHeavy;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaftLight;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaftMedium;

public class BlockTorqueTransportShaft extends BlockTorqueTransportConduit {
    static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);

    public BlockTorqueTransportShaft(String regName) {
        super(regName);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
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
    public TileEntity createTileEntity(World world, IBlockState state) {
        switch (state.getValue(TYPE)) {
            case LIGHT:
                return new TileTorqueShaftLight();
            case MEDIUM:
                return new TileTorqueShaftMedium();
            case HEAVY:
                return new TileTorqueShaftHeavy();
        }
        return new TileTorqueShaftLight();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        //TODO static AABBs that are used to put together the total

        float min = 0.1875f, max = 0.8125f;
        float x1 = min, y1 = min, z1 = min, x2 = max, y2 = max, z2 = max;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileTorqueShaft) {
            TileTorqueShaft tile = (TileTorqueShaft) world.getTileEntity(pos);
            EnumFacing facing = tile.getPrimaryFacing();
            int s1 = facing.ordinal();
            switch (facing.getAxis()) {
                case X:
                    x1 = 0;
                    x2 = 1;
                    break;
                case Y:
                    y1 = 0;
                    y2 = 1;
                    break;
                case Z:
                    z1 = 0;
                    z2 = 1;
                    break;
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
