package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.SidedTorqueCell;


public class TileDistributorHeavy extends TileDistributor {
    public TileDistributorHeavy() {
        double max = AWAutomationStatics.high_transfer_max;
        double eff = AWAutomationStatics.high_efficiency_factor;
        for (int i = 0; i < storage.length; i++) {
            storage[i] = new SidedTorqueCell(max, max, max, eff, ForgeDirection.values()[i], this);//TODO set from config
        }
    }
}
