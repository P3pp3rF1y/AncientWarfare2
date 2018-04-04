package net.shadowmage.ancientwarfare.core.proxy;

import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.input.InputHandler.Keybind;

import java.util.Set;

public class ClientProxyBase extends CommonProxyBase {

	private Set<IClientRegistrar> clientRegistrars = Sets.newHashSet();

	public ClientProxyBase() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		for (IClientRegistrar registrar : clientRegistrars) {
			registrar.registerClient();
		}
	}

	@Override
	public void addClientRegistrar(IClientRegistrar registrar) {
		clientRegistrars.add(registrar);
	}

	@Override
	public void preInit() {
		super.preInit();

	}

	@Override
	public void init() {
		super.init();

	}

	@Override
	public final EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().player;
	}

	@Override
	public final boolean isKeyPressed(String keyName) {
		Keybind kb = InputHandler.instance.getKeybind(keyName);
		return kb != null && kb.isPressed();
	}
}
