package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public class TileFlywheelControlLarge extends TileFlywheelControl {

    public TileFlywheelControlLarge() {
        double max = AWAutomationStatics.high_transfer_max;
        double eff = AWAutomationStatics.high_efficiency_factor;
        inputCell = new TorqueCell(max, max, max, eff);
        torqueCell = new TorqueCell(max, max, max, eff);
    }
}
