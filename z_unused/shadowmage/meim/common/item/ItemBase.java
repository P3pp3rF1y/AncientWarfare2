package shadowmage.meim.common.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import shadowmage.ancient_framework.common.utils.BlockPosition;

public abstract class ItemBase extends Item
{

protected List<ItemStack> subTypes = new ArrayList<ItemStack>();

public ItemBase(int itemID)
  {
  super(itemID); 
  this.setCreativeTab(ItemLoader.modelerTab);
  }

@Override
public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xOff, float yOff, float zOff)
  {
  return onUsedFinal(world, player, stack, new BlockPosition(x,y,z), side);
  }

@Override
public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,EntityPlayer par3EntityPlayer)
  {
  onUsedFinal(par2World, par3EntityPlayer, par1ItemStack, null, -1);  
  return par1ItemStack;
  }

public abstract boolean onUsedFinal(World world, EntityPlayer player, ItemStack stack, BlockPosition hit, int side);

}
