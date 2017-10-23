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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileFlywheelControlLarge;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileFlywheelControlLight;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileFlywheelControlMedium;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;

public class BlockFlywheel extends BlockTorqueBase {
    static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);

    public BlockFlywheel(String regName) {
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
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        switch (state.getValue(TYPE)) {
            case LIGHT:
                return new TileFlywheelControlLight();
            case MEDIUM:
                return new TileFlywheelControlMedium();
            case LARGE:
                return new TileFlywheelControlLarge();
        }
        return null;
    }

    @Override
    public void getSubBlocks(CreativeTabs item, NonNullList<ItemStack> items) {
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
        switch (meta) {
            case 0: {
                return Blocks.PLANKS.getIcon(side, 0);
            }
            case 1: {
                return Blocks.IRON_BLOCK.getIcon(side, 0);
            }
            case 2: {
                //TODO change this to steel block icon...once I make a steel block...
                return Blocks.IRON_BLOCK.getIcon(side, 0);
            }
        }
        return Blocks.IRON_BLOCK.getIcon(side, 0);
    }
*/

    @Override
    public boolean invertFacing() {
        return false;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    public enum Type implements IStringSerializable {
        LIGHT(0),
        MEDIUM(1),
        LARGE(2);

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
