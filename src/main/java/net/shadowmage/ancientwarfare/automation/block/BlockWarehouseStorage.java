package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorageLarge;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStorageMedium;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

public class BlockWarehouseStorage extends BlockBaseAutomation {
    static final PropertyEnum<Size> SIZE = PropertyEnum.create("size", Size.class);

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
        SMALL(0),
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

    @Override
    public void registerClient() {
        final ResourceLocation assetLocation = new ResourceLocation(AncientWarfareCore.modID, "automation/" + getRegistryName().getResourcePath());

        ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(assetLocation, getPropertyString(state.getProperties()));
            }
        });

        ModelLoaderHelper.registerItem(Item.getItemFromBlock(this), "automation", false, meta -> "size=" + Size.values()[meta].name().toLowerCase());

        NetworkHandler.registerGui(NetworkHandler.GUI_WAREHOUSE_STORAGE, GuiWarehouseStorage.class);
    }
}
