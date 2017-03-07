package net.shadowmage.ancientwarfare.core.interop;

import net.minecraft.entity.passive.EntityAnimal;

public interface InteropHarderWildlifeInterface {
    public boolean getMilkable(EntityAnimal animal);
    public void doMilking(EntityAnimal animal);
}
