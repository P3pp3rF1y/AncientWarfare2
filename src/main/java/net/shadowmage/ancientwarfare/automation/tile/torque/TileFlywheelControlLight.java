package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public class TileFlywheelControlLight extends TileFlywheelControl {

    public TileFlywheelControlLight() {
        double max = AWAutomationStatics.low_transfer_max;
        double eff = AWAutomationStatics.low_efficiency_factor;
        inputCell = new TorqueCell(max, max, max, eff);
        torqueCell = new TorqueCell(max, max, max, eff);
    }

}
