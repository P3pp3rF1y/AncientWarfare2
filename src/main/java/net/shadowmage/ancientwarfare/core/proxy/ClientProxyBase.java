package net.shadowmage.ancientwarfare.core.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class ClientProxyBase extends CommonProxyBase {

    @Override
    public void registerClient() {

    }

    public final EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

}
