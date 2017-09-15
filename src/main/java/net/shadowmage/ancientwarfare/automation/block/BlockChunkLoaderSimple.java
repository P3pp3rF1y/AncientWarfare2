package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.AncientWarfareAutomation;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.TileChunkLoaderSimple;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;

public class BlockChunkLoaderSimple extends Block {

    //BlockIconMap iconMap = new BlockIconMap();

    protected BlockChunkLoaderSimple(String regName) {
        super(Material.ROCK);
        this.setUnlocalizedName(regName);
        this.setRegistryName(new ResourceLocation(AncientWarfareAutomation.modID, regName));
        this.setCreativeTab(AWAutomationItemLoader.automationTab);
        setHardness(2.f);
//        String icon = "ancientwarfare:automation/" + regName + "_bottom";
//        setIcon(0, 0, icon);
//        setIcon(0, 1, icon);
//        icon = "ancientwarfare:automation/" + regName + "_side";
//        setIcon(0, 2, icon);
//        setIcon(0, 3, icon);
//        setIcon(0, 4, icon);
//        setIcon(0, 5, icon);
    }

//    public BlockChunkLoaderSimple setIcon(int meta, int side, String texName) {
//        this.iconMap.setIconTexture(side, meta, texName);
//        return this;
//    }

/*
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
        return new TileChunkLoaderSimple();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileChunkLoaderSimple) {
            ((TileChunkLoaderSimple) te).releaseTicket();
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        return te instanceof IInteractableTile && ((IInteractableTile) te).onBlockClicked(player, hand);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileChunkLoaderSimple) {
                ((TileChunkLoaderSimple) te).setupInitialTicket();
            }
        }
    }
}
