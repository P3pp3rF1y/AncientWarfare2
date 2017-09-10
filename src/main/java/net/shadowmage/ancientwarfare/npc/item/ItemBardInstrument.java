package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class ItemBardInstrument extends Item {

    private final String[] instrumentNames = new String[]{"lute", "flute", "harp", "drum"};

    public ItemBardInstrument(String regName) {
        setUnlocalizedName(regName);
        setCreativeTab(AWNpcItemLoader.npcTab);
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab != AWNpcItemLoader.npcTab) {
            return;
        }

        for (int i = 0; i < instrumentNames.length; i++) {
            items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return super.getUnlocalizedName(par1ItemStack) + "." + instrumentNames[par1ItemStack.getItemDamage()];
    }

/*
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
*/

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            int meta = stack.getItemDamage();
            SoundEvent s;
            s = "note.bd";
            if(meta == 0){
                s = "note.bassattack";
            }else if(meta == 1){
                s = SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE;
            }else if(meta == 2){
                s = "note.harp";
            }
            world.playSound(null, player.posX + 0.5, player.posY + 0.5, player.posZ + 0.5, s, 2.0F, 1.0F);
        }
        return stack;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase living, ItemStack stack) {
        return true;
    }
}
