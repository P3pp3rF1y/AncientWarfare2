package net.shadowmage.ancientwarfare.core.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.input.InputHandler.Keybind;

public class ClientProxyBase extends CommonProxyBase {

    @Override
    public void registerClient() {

    }

    @Override
    public final EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public final boolean isKeyPressed(String keyName) {
        Keybind kb = InputHandler.instance.getKeybind(keyName);
        return kb != null && kb.isPressed();
    }

    @Override
    public final World getWorld(int dimension) {
        if(Minecraft.getMinecraft().theWorld.provider.dimensionId == dimension){
            return Minecraft.getMinecraft().theWorld;
        }
        return null;
    }
}
