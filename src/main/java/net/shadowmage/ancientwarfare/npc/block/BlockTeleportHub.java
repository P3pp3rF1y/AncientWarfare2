package net.shadowmage.ancientwarfare.npc.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.gamedata.HeadquartersTracker;
import net.shadowmage.ancientwarfare.npc.item.AWNPCItemLoader;
import net.shadowmage.ancientwarfare.npc.tile.TileTeleportHub;

public class BlockTeleportHub extends Block {

    public BlockTeleportHub() {
        super(Material.ROCK);
        setCreativeTab(AWNPCItemLoader.npcTab);
        setHardness(2.f);
        setUnlocalizedName("teleport_hub");
        setRegistryName(new ResourceLocation(AncientWarfareNPC.modID, "teleport_hub"));
    }

/*
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        icons[0] = register.registerIcon("ancientwarfare:npc/town_hall_bottom");
        icons[1] = register.registerIcon("ancientwarfare:npc/teleport_hub_top");
        icons[2] = register.registerIcon("ancientwarfare:npc/teleport_hub_side");
        icons[3] = register.registerIcon("ancientwarfare:npc/teleport_hub_side");
        icons[4] = register.registerIcon("ancientwarfare:npc/teleport_hub_side");
        icons[5] = register.registerIcon("ancientwarfare:npc/teleport_hub_side");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return icons[side];
    }
*/

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileTeleportHub();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        HeadquartersTracker.get(world).setTeleportHubPosition(pos);
    }
}