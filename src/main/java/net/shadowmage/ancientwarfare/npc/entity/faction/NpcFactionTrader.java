package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public abstract class NpcFactionTrader extends NpcFaction
{

public NpcFactionTrader(World par1World)
  {
  super(par1World);
  setCurrentItemOrArmor(0, new ItemStack(Items.book));
  }

@Override
protected boolean interact(EntityPlayer par1EntityPlayer)
  {
  if(!par1EntityPlayer.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(par1EntityPlayer, NetworkHandler.GUI_NPC_TRADE, getEntityId(), 0, 0);
    }
  return false;
  }

@Override
public boolean isHostileTowards(Entity e)
  {
  return false;
  }

}
