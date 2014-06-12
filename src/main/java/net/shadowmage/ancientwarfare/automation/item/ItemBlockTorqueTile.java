package net.shadowmage.ancientwarfare.automation.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueBase;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;

public class ItemBlockTorqueTile extends ItemBlock
{

IRotatableBlock rotatable;

public ItemBlockTorqueTile(Block p_i45328_1_)
  {
  super(p_i45328_1_);
  if(!(p_i45328_1_ instanceof IRotatableBlock))
    {
    throw new IllegalArgumentException("Must be a rotatable block!!");
    }
  rotatable = (IRotatableBlock)p_i45328_1_;
  }

@Override
public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
  {
  metadata = BlockRotationHandler.getMetaForPlacement(player, rotatable, side);
  boolean val = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, stack.getItemDamage());
  if(val)
    {
    String name = player.getCommandSenderName();
    TileTorqueBase te = (TileTorqueBase) player.worldObj.getTileEntity(x, y, z);
    te.setOrientation(ForgeDirection.getOrientation(metadata));
    if(te instanceof IOwnable)
      {
      ((IOwnable)te).setOwnerName(name);
      }
    }  
  return val;
  }

@Override
public String getUnlocalizedName(ItemStack stack)
  {
  return super.getUnlocalizedName(stack)+"."+stack.getItemDamage();
  }
}
