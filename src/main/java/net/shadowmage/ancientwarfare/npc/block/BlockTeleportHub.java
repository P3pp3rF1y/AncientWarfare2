package net.shadowmage.ancientwarfare.npc.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.gamedata.HeadquartersTracker;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.tile.TileTeleportHub;

public class BlockTeleportHub extends Block {

    public IIcon[] icons = new IIcon[6];
    
    public BlockTeleportHub() {
        super(Material.rock);
        this.setCreativeTab(AWNpcItemLoader.npcTab);
        setHardness(2.f);
    }

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

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileTeleportHub();
    }
    
    @Override
    public void onBlockPlacedBy(World world, int posX, int posY, int posZ, EntityLivingBase placer, ItemStack is) {
        HeadquartersTracker.get(world).setTeleportHubPosition(posX, posY, posZ);
    }
}