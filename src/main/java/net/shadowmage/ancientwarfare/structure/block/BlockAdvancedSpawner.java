package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.structure.gui.GuiSpawnerAdvanced;
import net.shadowmage.ancientwarfare.structure.gui.GuiSpawnerAdvancedInventory;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockAdvancedSpawner extends BlockBaseStructure {
    private static final PropertyBool TRANSPARENT = PropertyBool.create("transparent");

    public BlockAdvancedSpawner() {
        super(Material.ROCK, "advanced_spawner");
        setHardness(2.f);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        @Nonnull ItemStack stack = new ItemStack(this);
        SpawnerSettings settings = SpawnerSettings.getDefaultSettings();
        NBTTagCompound defaultTag = new NBTTagCompound();
        settings.writeToNBT(defaultTag);
        stack.setTagInfo("spawnerSettings", defaultTag);
        items.add(stack);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(TRANSPARENT).build();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IBlockState superState = super.getActualState(state, world, pos);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileAdvancedSpawner) {
            TileAdvancedSpawner spawner = (TileAdvancedSpawner) te;
            if (spawner.getSettings().isTransparent()) {
                return superState.withProperty(TRANSPARENT, true);
            }
        }
        return superState.withProperty(TRANSPARENT, false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes,
            @Nullable Entity entity, boolean p_185477_7_) {
        if(world.loadedEntityList.contains(entity)) {
            super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity, p_185477_7_);
        }
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileAdvancedSpawner();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileAdvancedSpawner) {
            ((TileAdvancedSpawner) te).onBlockBroken();
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileAdvancedSpawner) {
            TileAdvancedSpawner spawner = (TileAdvancedSpawner) te;
            return spawner.getBlockHardness();
        }
        return super.getBlockHardness(state, world, pos);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileAdvancedSpawner) {
            @Nonnull ItemStack item = new ItemStack(this);
            NBTTagCompound settings = new NBTTagCompound();
            ((TileAdvancedSpawner) te).getSettings().writeToNBT(settings);
            item.setTagInfo("spawnerSettings", settings);
            return item;
        }
        return super.getPickBlock(state, target, world, pos, player);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.capabilities.isCreativeMode) {
            if (!world.isRemote) {
                if (player.isSneaking()) {
                    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK_INVENTORY, pos);
                } else {
                    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK, pos);
                }
            }
            return true;
        }
        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void registerClient() {
        super.registerClient();

        NetworkHandler.registerGui(NetworkHandler.GUI_SPAWNER_ADVANCED, GuiSpawnerAdvanced.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK, GuiSpawnerAdvanced.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_SPAWNER_ADVANCED_INVENTORY, GuiSpawnerAdvancedInventory.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK_INVENTORY, GuiSpawnerAdvancedInventory.class);
    }
}
