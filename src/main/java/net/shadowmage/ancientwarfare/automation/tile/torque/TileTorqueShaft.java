package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;
import net.shadowmage.ancientwarfare.core.util.Trig;

import javax.annotation.Nullable;

public abstract class TileTorqueShaft extends TileTorqueSingleCell {

    private TileTorqueShaft prev, next;

    private boolean prevNeighborInvalid = true;
    private boolean nextNeighborInvalid = true;

    public TileTorqueShaft() {
        double max = getMaxTransfer();
        torqueCell = new TorqueCell(max, max, max, getEfficiency());
    }

    protected abstract double getEfficiency();

    protected abstract double getMaxTransfer();

    @Override
    protected void serverNetworkSynch() {
        if (prev() == null) {
            TileTorqueShaft last = this;
            TileTorqueShaft n = next();
            double totalPower = torqueCell.getEnergy();
            double num = 1;
            while (n != null) {
                totalPower += n.torqueCell.getEnergy();
                last = n;
                num++;
                n = n.next;
            }
            double avg = totalPower / num;
            double perc = avg / torqueCell.getMaxEnergy();
            double torqueOut = last.torqueOut;

            int percent = (int) (perc * 100.d);
            int percent2 = (int) ((torqueOut / last.torqueCell.getMaxOutput()) * 100.d);
//    AWLog.logDebug("shaft net synch, p1, p2: "+percent+" :: "+percent2 + " avg: "+avg+" avgper: "+perc+" lo: "+last.torqueOut);
            percent = Math.max(percent, percent2);
            if (percent != clientDestEnergyState) {
                clientDestEnergyState = percent;
                sendSideRotation(getPrimaryFacing(), percent);
            }
        }
    }

    @Override
    protected void updateRotation() {
        if (prev() == null) {
            prevRotation = rotation;
            if (clientEnergyState > 0) {
                double r = AWAutomationStatics.low_rpt * clientEnergyState * 0.01d;
                rotation += r;
            }
            TileTorqueShaft n = next;
            while (n != null) {
                n.rotation = rotation;
                n.prevRotation = prevRotation;
                n = n.next;
            }
        }
    }

    protected void onNeighborCacheInvalidated() {
        invalidateNeighborCache();
        invalidateLocalCache();
    }

    private void invalidateLocalCache() {
        prevNeighborInvalid = true;
        nextNeighborInvalid = true;
        prev = next = null;
    }

    private void invalidateNeighborCache() {
        if (next != null) {
            next.invalidateLocalCache();
        }
        if (prev != null) {
            prev.invalidateLocalCache();
        }
    }

    @Nullable
    public TileTorqueShaft prev() {
        if (prevNeighborInvalid) {
            prevNeighborInvalid = false;
            ITorqueTile input = getTorqueCache()[orientation.getOpposite().ordinal()];
            if (input instanceof TileTorqueShaft && input.getClass() == this.getClass() && input.canOutputTorque(orientation)) {
                prev = (TileTorqueShaft) input;
                prev.next = this;
            }
        }
        return prev;
    }

    @Nullable
    public TileTorqueShaft next() {
        if (nextNeighborInvalid) {
            nextNeighborInvalid = false;
            ITorqueTile output = getTorqueCache()[orientation.ordinal()];
            if (output instanceof TileTorqueShaft && output.getClass() == this.getClass() && output.canInputTorque(orientation.getOpposite())) {
                next = (TileTorqueShaft) output;
                next.prev = this;
            }
        }
        return next;
    }

    @Override
    public float getClientOutputRotation(EnumFacing from, float delta) {
        return (prev() == null ? getRotation(rotation, prevRotation, delta) : prev().getClientOutputRotation(from, delta)) * Trig.TORADIANS;
    }

}
