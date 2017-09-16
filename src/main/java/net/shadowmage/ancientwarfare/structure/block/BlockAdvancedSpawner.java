package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructures;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockAdvancedSpawner extends Block {

    public BlockAdvancedSpawner() {
        super(Material.ROCK);
        setCreativeTab(AWStructuresItemLoader.structureTab);
        setUnlocalizedName("advanced_spawner");
        setRegistryName(new ResourceLocation(AncientWarfareStructures.modID, "advanced_spawner"));
        //this.setBlockTextureName("ancientwarfare:structure/advanced_spawner");
        setHardness(2.f);
    }

//    @Override
//    @SideOnly(Side.CLIENT)
//    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
//        TileEntity te = world.getTileEntity(pos);
//        if (te instanceof TileAdvancedSpawner) {
//            TileAdvancedSpawner spawner = (TileAdvancedSpawner) te;
//            if (spawner.getSettings().isTransparent()) {
//                return transparentIcon;
//            }
//        }
//        return super.getIcon(world, x, y, z, side);
//    }

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

//    @Override
//    @SideOnly(Side.CLIENT)
//    public void registerBlockIcons(IIconRegister p_149651_1_) {
//        super.registerBlockIcons(p_149651_1_);
//        transparentIcon = p_149651_1_.registerIcon("ancientwarfare:structure/advanced_spawner2");
//    }

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

}
