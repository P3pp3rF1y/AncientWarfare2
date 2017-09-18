package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.material.Material;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;

public abstract class BlockTorqueGenerator extends BlockTorqueBase {

    protected BlockTorqueGenerator(String regName) {
        super(Material.ROCK, regName);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public boolean invertFacing() {
        return false;
    }

}
