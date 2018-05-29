package net.shadowmage.ancientwarfare.core.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.entity.AWFakePlayer;
import net.shadowmage.ancientwarfare.core.owner.Owner;

public class CommonProxyBase {

	public void addClientRegistrar(IClientRegistrar registrar) {
		//NOOP for commonProxy
	}

	public void preInit() {

	}

	public void init() {

	}

	public EntityPlayer getClientPlayer() {
		//NOOP for commonProxy
		return null;
	}

	public EntityPlayer getFakePlayer(World world) {
		return AWFakePlayer.get(world);
	}

	public EntityPlayer getFakePlayer(World world, Owner owner) {
		EntityPlayer player;
		player = world.getPlayerEntityByUUID(owner.getUUID());
		if (player != null) {
			return player;
		}
		player = world.getPlayerEntityByName(owner.getName());
		if (player != null) {
			return player;
		}
		return AWFakePlayer.get(world);
	}
}
