package net.shadowmage.ancientwarfare.core.proxy;

import net.minecraft.entity.player.EntityPlayer;

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
}
