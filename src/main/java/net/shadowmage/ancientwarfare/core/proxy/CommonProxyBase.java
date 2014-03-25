package net.shadowmage.ancientwarfare.core.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;

import com.mojang.authlib.GameProfile;

public class CommonProxyBase
{

public void registerClient()
  {
  //NOOP for commonProxy
  }

public EntityPlayer getClientPlayer()
  {
  //NOOP for commonProxy
  return null;
  }

public EntityPlayer getFakePlayer(WorldServer world)
  {
  return getFakePlayer(world, "AncientWarfare");
  }

public EntityPlayer getFakePlayer(WorldServer world, String name)
  {
  return FakePlayerFactory.get(world, new GameProfile("", name));
  }

}
