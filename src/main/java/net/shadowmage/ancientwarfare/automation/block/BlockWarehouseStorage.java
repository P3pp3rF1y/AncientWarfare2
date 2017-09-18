package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorageLarge;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorageMedium;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;

public class BlockWarehouseStorage extends BlockBaseAutomation {
    static final PropertyEnum<Size> SIZE = PropertyEnum.create("size", Size.class);

/*
    private BlockIconMap iconMap = new BlockIconMap();
*/

    public BlockWarehouseStorage(String regName) {
        super(Material.ROCK, regName);
        setHardness(2.f);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, SIZE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(SIZE, Size.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(SIZE).getMeta();
    }

    /*
		public BlockWarehouseStorage setIcon(int meta, int side, String texName) {
			this.iconMap.setIconTexture(side, meta, texName);
			return this;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void registerBlockIcons(IIconRegister reg) {
			iconMap.registerIcons(reg);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public IIcon getIcon(int side, int meta) {
			return iconMap.getIconFor(side, meta);
		}

	 */

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        switch (state.getValue(SIZE)) {
            case MEDIUM:
                return new TileWarehouseStorageMedium();
            case LARGE:
                return new TileWarehouseStorageLarge();
            default:
                return new TileWarehouseStorage();
        }
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
        items.add(new ItemStack(this, 1, 2));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        return te instanceof IInteractableTile && ((IInteractableTile) te).onBlockClicked(player, hand);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileWarehouseStorage tile = (TileWarehouseStorage) world.getTileEntity(pos);
        if (tile != null) {
            tile.onTileBroken();
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(SIZE).getMeta();
    }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
        super.eventReceived(state, world, pos, id, param);
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, param);
    }

    public enum Size implements IStringSerializable {
        STANDARD(0),
        MEDIUM(1),
        LARGE(2);

        private int meta;
        Size(int meta) {
            this.meta = meta;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public int getMeta() {
            return meta;
        }

        public static Size byMetadata(int meta) {
            return values()[meta];
        }
    }
}
