package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

public class ItemBardInstrument extends Item {

    private final String[] instrumentNames = new String[]{"lute", "flute", "harp", "drum"};
    private final IIcon[] icons = new IIcon[instrumentNames.length];

    public ItemBardInstrument(String regName) {
        setUnlocalizedName(regName);
        setCreativeTab(AWNpcItemLoader.npcTab);
        setHasSubtypes(true);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List stackList) {
        for (int i = 0; i < instrumentNames.length; i++) {
            stackList.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return super.getUnlocalizedName(par1ItemStack) + "." + instrumentNames[par1ItemStack.getItemDamage()];
    }

    @Override
    public void registerIcons(IIconRegister par1IconRegister) {
        for (int i = 0; i < instrumentNames.length; i++) {
            icons[i] = par1IconRegister.registerIcon("ancientwarfare:npc/instrument_" + instrumentNames[i]);
        }
    }

    @Override
    public IIcon getIconFromDamage(int par1) {
        return icons[par1];
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            int meta = stack.getItemDamage();
            String s = "note.bd";
            if(meta == 0){
                s = "note.bassattack";
            }else if(meta == 1){
                s = "mob.zombie.unfect";
            }else if(meta == 2){
                s = "note.harp";
            }
            world.playSoundEffect(player.posX + 0.5, player.posY + 0.5, player.posZ + 0.5, s, 2.0F, 1.0F);
        }
        return stack;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase living, ItemStack stack) {
        return true;
    }
}
