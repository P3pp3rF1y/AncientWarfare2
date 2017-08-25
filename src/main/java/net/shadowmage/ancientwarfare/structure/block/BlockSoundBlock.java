package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;
import net.shadowmage.ancientwarfare.structure.tile.TileSoundBlock;

public class BlockSoundBlock extends Block {

    public BlockSoundBlock() {
        super(Material.ROCK);
        this.setCreativeTab(AWStructuresItemLoader.structureTab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int face){
        TileEntity tileEntity = blockAccess.getTileEntity(x, y, z);
        if(tileEntity instanceof TileSoundBlock) {
            Block block = ((TileSoundBlock) tileEntity).getBlockCache();
            if (block != null) {
                return block.getIcon(face, 0);
            }
        }
        return getIcon(face, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta){
        return Blocks.JUKEBOX.getIcon(side, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister){

    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileSoundBlock();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        @Nonnull ItemStack itemStack = player.getCurrentEquippedItem();
        if(itemStack!=null && itemStack.getItem() instanceof ItemBlock){
            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity instanceof TileSoundBlock) {
                ((TileSoundBlock)tileEntity).setBlockCache(itemStack);
            }
        }
        if (!world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SOUND_BLOCK, x, y, z);
        }
        return true;
    }

}
