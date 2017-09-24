package net.shadowmage.ancientwarfare.automation.gamedata;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.server.FMLServerHandler;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;

public final class MailboxTicker {
    public static final Object INSTANCE = new MailboxTicker();
    private MailboxTicker() {

    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent evt) {
        if (evt.phase == TickEvent.Phase.END) {
            MinecraftServer server = FMLCommonHandler.instance().getSide() == Side.SERVER ? FMLServerHandler.instance().getServer() : null; //TODO does this really need to be here or does it need to getData every single tick?
            if (server != null && server.getEntityWorld() != null) {
                AWGameData.INSTANCE.getData(server.getEntityWorld(), MailboxData.class).onTick(1);
            }
        }
    }
}
