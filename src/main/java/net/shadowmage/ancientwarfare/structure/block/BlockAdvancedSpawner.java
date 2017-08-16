package net.shadowmage.ancientwarfare.structure.block;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;

import java.util.List;

public class BlockAdvancedSpawner extends Block {

    IIcon transparentIcon;

    public BlockAdvancedSpawner() {
        super(Material.ROCK);
        this.setCreativeTab(AWStructuresItemLoader.structureTab);
        this.setBlockTextureName("ancientwarfare:structure/advanced_spawner");
        setHardness(2.f);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileAdvancedSpawner) {
            TileAdvancedSpawner spawner = (TileAdvancedSpawner) te;
            if (spawner.getSettings().isTransparent()) {
                return transparentIcon;
            }
        }
        return super.getIcon(world, x, y, z, side);
    }

    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        return 0;
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
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB maskBB, List list, Entity entity){
        if(world.loadedEntityList.contains(entity)){
            super.addCollisionBoxesToList(world, x, y, z, maskBB, list, entity);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        super.registerBlockIcons(p_149651_1_);
        transparentIcon = p_149651_1_.registerIcon("ancientwarfare:structure/advanced_spawner2");
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
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileAdvancedSpawner) {
            TileAdvancedSpawner spawner = (TileAdvancedSpawner) te;
            return spawner.getBlockHardness();
        }
        return super.getBlockHardness(world, x, y, z);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer entityPlayer) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileAdvancedSpawner) {
            ItemStack item = new ItemStack(this);
            NBTTagCompound settings = new NBTTagCompound();
            ((TileAdvancedSpawner) te).getSettings().writeToNBT(settings);
            item.setTagInfo("spawnerSettings", settings);
            return item;
        }
        return super.getPickBlock(target, world, x, y, z, entityPlayer);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.capabilities.isCreativeMode) {
            if (!world.isRemote) {
                if (player.isSneaking()) {
                    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK_INVENTORY, x, y, z);
                } else {
                    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK, x, y, z);
                }
            }
            return true;
        }
        return super.onBlockActivated(world, x, y, z, player, sideHit, hitX, hitY, hitZ);
    }

}
