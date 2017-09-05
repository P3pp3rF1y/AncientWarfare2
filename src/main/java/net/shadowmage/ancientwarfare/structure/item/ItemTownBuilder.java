package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.structure.town.WorldTownGenerator;

public class ItemTownBuilder extends Item implements IItemKeyInterface {

    public ItemTownBuilder(String itemName) {
        this.setUnlocalizedName(itemName);
        this.setCreativeTab(AWStructuresItemLoader.structureTab);
        this.setMaxStackSize(1);
        this.setTextureName("ancientwarfare:structure/structure_builder");//TODO make texture...
    }

//@SuppressWarnings({ "unchecked", "rawtypes" })
//@Override
//public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
//  {
//  String structure = "guistrings.no_selection";
//  ItemStructureSettings.getSettingsFor(stack, viewSettings);
//  if(viewSettings.hasName())
//    {
//    structure = viewSettings.name;
//    }  
//  list.add(I18n.format("guistrings.current_structure")+" "+I18n.format(structure));
//  }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
        return false;
    }

    @Override
    public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key) {
        return key == ItemKey.KEY_0;
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        if (player == null || player.world.isRemote) {
            return;
        }
        long t1 = System.nanoTime();
        WorldTownGenerator.INSTANCE.attemptGeneration(player.world, MathHelper.floor(player.posX), MathHelper.floor(player.posZ));
        long t2 = System.nanoTime();
        AWLog.logDebug("Total Town gen nanos (incl. validation): " + (t2 - t1));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        return stack;
    }//TODO open town-type selection GUI

}
