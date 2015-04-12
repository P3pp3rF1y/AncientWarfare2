package net.shadowmage.ancientwarfare.automation.gamedata;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraft.server.MinecraftServer;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;

public final class MailboxTicker {
    public static final Object INSTANCE = new MailboxTicker();
    private MailboxTicker() {

    }

    @SubscribeEvent
    public void serverTick(ServerTickEvent evt) {
        if (evt.phase == Phase.END) {
            MinecraftServer server = MinecraftServer.getServer();
            if (server != null && server.getEntityWorld() != null) {
                MailboxData data = AWGameData.INSTANCE.getData(server.getEntityWorld(), MailboxData.class);
                data.onTick(1);
            }
        }
    }
}
