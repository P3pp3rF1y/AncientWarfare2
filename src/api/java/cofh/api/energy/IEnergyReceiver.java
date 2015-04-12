package cofh.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * From CoFHLib.
 */
public interface IEnergyReceiver extends IEnergyConnection{
    int receiveEnergy(ForgeDirection var1, int var2, boolean var3);

    int getEnergyStored(ForgeDirection var1);

    int getMaxEnergyStored(ForgeDirection var1);
}
