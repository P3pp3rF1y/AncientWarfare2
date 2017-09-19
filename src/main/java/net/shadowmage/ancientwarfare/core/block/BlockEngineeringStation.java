package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.gui.crafting.GuiEngineeringStation;
import net.shadowmage.ancientwarfare.core.model.crafting_table.ModelEngineeringStation;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegistrar;
import net.shadowmage.ancientwarfare.core.render.BlockRenderProperties;
import net.shadowmage.ancientwarfare.core.render.TileCraftingTableRender;
import net.shadowmage.ancientwarfare.core.tile.TileEngineeringStation;

public class BlockEngineeringStation extends BlockRotatableTile implements IClientRegistrar {

    protected BlockEngineeringStation() {
        super(Material.ROCK, "engineering_station");
/*
        setIcon(RelativeSide.ANY_SIDE, "ancientwarfare:core/engineering_station_bottom");
        setIcon(RelativeSide.BOTTOM, "ancientwarfare:core/engineering_station_bottom");
        setIcon(RelativeSide.TOP, "ancientwarfare:core/engineering_station_top");
        setIcon(RelativeSide.FRONT, "ancientwarfare:core/engineering_station_front");
        setIcon(RelativeSide.REAR, "ancientwarfare:core/engineering_station_front");
        setIcon(RelativeSide.LEFT, "ancientwarfare:core/engineering_station_side");
        setIcon(RelativeSide.RIGHT, "ancientwarfare:core/engineering_station_side");
*/
        setHardness(2.f);

        AncientWarfareCore.proxy.addClientRegistrar(this);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockRenderProperties.FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        EnumFacing facing = EnumFacing.NORTH;
        TileEntity tileentity = worldIn instanceof ChunkCache ? ((ChunkCache)worldIn).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEngineeringStation) {
            facing = ((TileEngineeringStation) tileentity).getPrimaryFacing();
        }

        return state.withProperty(BlockRenderProperties.FACING, facing);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEngineeringStation();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_CRAFTING, pos);
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEngineeringStation tile = (TileEngineeringStation) world.getTileEntity(pos);
        if (tile != null) {
            tile.onBlockBreak();
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public boolean invertFacing() {
        return true;
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
    @SideOnly(Side.CLIENT)
    public void registerClient() {
        NetworkHandler.registerGui(NetworkHandler.GUI_CRAFTING, GuiEngineeringStation.class);

        TileCraftingTableRender render = new TileCraftingTableRender(new ModelEngineeringStation(), "textures/model/core/tile_engineering_station.png");
        ClientRegistry.bindTileEntitySpecialRenderer(TileEngineeringStation.class, render);

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "facing=north"));
    }
}
