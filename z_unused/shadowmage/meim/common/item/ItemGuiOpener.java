package shadowmage.meim.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import shadowmage.ancient_framework.common.utils.BlockPosition;
import shadowmage.meim.common.MEIM;

public class ItemGuiOpener extends ItemBase
{

public ItemGuiOpener(int itemID)
  {
  super(itemID);
  this.setUnlocalizedName("MEIM Gui");
  }

@Override
public boolean onUsedFinal(World world, EntityPlayer player, ItemStack stack, BlockPosition hit, int side)
  {
  if(!world.isRemote)
    {
    return false;
    }
  MEIM.proxy.openMEIMGUI();
  return false;
  }

}
