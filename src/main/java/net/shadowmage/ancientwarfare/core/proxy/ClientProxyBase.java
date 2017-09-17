package net.shadowmage.ancientwarfare.core.proxy;

import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.input.InputHandler.Keybind;

import java.util.Set;

public class ClientProxyBase extends CommonProxyBase {

    private Set<IClientRegistrar> clientRegistrars = Sets.newHashSet();

    public void addClientRegistrant(IClientRegistrar registrar) {
        clientRegistrars.add(registrar);
    }

    @Override
    public void registerClient() {
        for(IClientRegistrar registrar : clientRegistrars) {
            registrar.registerClient();
        }
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
