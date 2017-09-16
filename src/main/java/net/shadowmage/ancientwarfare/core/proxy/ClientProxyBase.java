package net.shadowmage.ancientwarfare.core.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.input.InputHandler.Keybind;

public class ClientProxyBase extends CommonProxyBase {

    @Override
    public void registerClient() {

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
