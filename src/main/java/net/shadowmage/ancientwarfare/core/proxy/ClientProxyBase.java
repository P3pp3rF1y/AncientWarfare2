package net.shadowmage.ancientwarfare.core.proxy;

import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.input.InputHandler.Keybind;

import java.util.Set;

public class ClientProxyBase extends CommonProxyBase {

    private Set<IClientRegistrar> clientRegistrars = Sets.newHashSet();

    @Override
    public void addClientRegistrar(IClientRegistrar registrar) {
        clientRegistrars.add(registrar);
    }

    @Override
    public void init() {
        super.init();

        OBJLoader.INSTANCE.addDomain(AncientWarfareCore.modID);

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
