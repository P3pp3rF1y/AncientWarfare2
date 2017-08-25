package net.shadowmage.ancientwarfare.core.gamedata;

import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;

public final class Timekeeper {
    //public static final Timekeeper INSTANCE = new Timekeeper();
    
    private static int timeOfDayInTicks;
    private static int timeOfDayHourRaw;
    private static int timeOfDayHour;
    private static int timeOfDayMinute;
    
    private static int TICKER = 0;
    
    public Timekeeper() {}

    @SubscribeEvent
    public void serverTick(WorldTickEvent event) {
        if (event.phase == Phase.END) {
            // for SSP and SMP servers 
            tickTimekeeper(event.world.getWorldTime());
        }
    }
    
    @SubscribeEvent
    public void clientTick(ClientTickEvent event) {
        if (!Minecraft.getMinecraft().isSingleplayer()) {
            // for SMP clients only
            if (Minecraft.getMinecraft().theWorld != null) {
                tickTimekeeper(Minecraft.getMinecraft().theWorld.getWorldTime());
            }
        }
    }
    
    private void tickTimekeeper(long worldTime) {
        TICKER++;
        if (TICKER >= AWCoreStatics.timekeeperRefreshRate) {
            TICKER = 0;
            timeOfDayInTicks = (int) (worldTime % 24000);
            timeOfDayHourRaw = timeOfDayInTicks / 1000;
            timeOfDayHour = timeOfDayHourRaw + 6 - (timeOfDayHourRaw > 17 ? 24 : 0);
            timeOfDayMinute = (timeOfDayInTicks % 1000) * 60 / 1000;
        }
    }
    
    
    /**
     * Get the current time of day in ticks (roughly, has an update rate
     * specified by timekeeper refreshrate config - default 100 ticks) 
     */
    public static int getTimeOfDayInTicks() {
        return timeOfDayInTicks;
    }
    
    public static int getTimeOfDayHourRaw() {
        return timeOfDayHourRaw;
    }
    
    public static int getTimeOfDayHour() {
        return timeOfDayHour;
    }
    
    public static int getTimeOfDayMinute() {
        return timeOfDayMinute;
    }
    
    public static boolean isDaytime() {
        return (timeOfDayInTicks < 12000);
    }
    
    public static boolean isNighttime() {
        return (timeOfDayInTicks > 12000);
    }
}
