package net.shadowmage.ancientwarfare.core.gamedata;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;

public final class Timekeeper {
    public static final Timekeeper INSTANCE = new Timekeeper();
    
    private int timeOfDayInTicks;
    private int TICKER = 0;
    
    private Timekeeper() {}

    @SubscribeEvent
    public void serverTick(WorldTickEvent event) {
        if (event.phase == Phase.END) {
            TICKER++;
            if (TICKER >= AWCoreStatics.timekeeperRefreshRate) {
                TICKER = 0;
                timeOfDayInTicks = (int) (event.world.getWorldTime() % 24000);
            }
        }
    }
    
    
    /**
     * Get the current time of day in ticks (roughly, has an update rate
     * specified by timekeeper refreshrate config - default 100 ticks) 
     */
    public int getTimeInTicks() {
        return timeOfDayInTicks;
    }
    
    public boolean isDaytime() {
        return (timeOfDayInTicks < 12000);
    }
    
    public boolean isNighttime() {
        return (timeOfDayInTicks > 12000);
    }
}
