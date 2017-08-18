package net.shadowmage.ancientwarfare.automation.gamedata;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;

public final class MailboxTicker {
    public static final Object INSTANCE = new MailboxTicker();
    private MailboxTicker() {

    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent evt) {
        if (evt.phase == TickEvent.Phase.END) {
            MinecraftServer server = MinecraftServer.getServer();
            if (server != null && server.getEntityWorld() != null) {
                AWGameData.INSTANCE.getData(server.getEntityWorld(), MailboxData.class).onTick(1);
            }
        }
    }
}
