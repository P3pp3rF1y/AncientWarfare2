package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public class TileFlywheelControlMedium extends TileFlywheelControl {
    public TileFlywheelControlMedium() {
        double max = AWAutomationStatics.med_transfer_max;
        double eff = AWAutomationStatics.med_efficiency_factor;
        inputCell = new TorqueCell(max, max, max, eff);
        torqueCell = new TorqueCell(max, max, max, eff);
    }
}
