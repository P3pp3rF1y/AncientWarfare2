package net.shadowmage.ancientwarfare.automation.proxy;

import cofh.redstoneflux.api.IEnergyConnection;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;

public class RFProxyActual extends RFProxy {

    protected RFProxyActual() {

    }

    @Override
    public boolean isRFTile(TileEntity te) {
        return te instanceof IEnergyConnection;
    }

    @Override
    public double transferPower(ITorqueTile generator, EnumFacing from, TileEntity target) {
        if (target instanceof IEnergyReceiver) {
            IEnergyReceiver h = (IEnergyReceiver) target;
            return generator.drainTorque(from, (h.receiveEnergy(from.getOpposite(), (int) (generator.getMaxTorqueOutput(from) * AWAutomationStatics.torqueToRf), false) * AWAutomationStatics.rfToTorque));
        }
        return 0;
    }

}
