package net.shadowmage.ancientwarfare.core.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.entity.AWFakePlayer;

import javax.annotation.Nullable;
import java.util.UUID;

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

	public EntityPlayer getFakePlayer(World world, @Nullable String name, @Nullable UUID id) {
		EntityPlayer player;
		if (id != null) {
			player = world.getPlayerEntityByUUID(id);
			if (player != null)
				return player;
		}
		if (name != null) {
			player = world.getPlayerEntityByName(name);
			if (player != null) {
				return player;
			}
			return AWFakePlayer.get(world);
		}
		return AWFakePlayer.get(world);
	}
}
