package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;

public final class TileTorqueShaftHeavy extends TileTorqueShaft {

    @Override
    protected double getEfficiency() {
        return AWAutomationStatics.high_efficiency_factor;
    }

    @Override
    protected double getMaxTransfer() {
        return AWAutomationStatics.high_transfer_max;
    }

}
