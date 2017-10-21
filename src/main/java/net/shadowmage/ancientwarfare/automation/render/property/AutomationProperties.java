package net.shadowmage.ancientwarfare.automation.render.property;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.common.property.PropertyFloat;

public class AutomationProperties {
    public static final IUnlistedProperty<Boolean> ACTIVE = Properties.toUnlisted(PropertyBool.create("active"));
    public static final IUnlistedProperty<Boolean> DYNAMIC = Properties.toUnlisted(PropertyBool.create("dynamic"));
    public static final IUnlistedProperty<Float>[] ROTATIONS = new IUnlistedProperty[6];
    static {
        for(EnumFacing facing : EnumFacing.VALUES) {
            ROTATIONS[facing.getIndex()] = new PropertyFloat("rotation_" + facing.name().toLowerCase());
        }
    }
}
