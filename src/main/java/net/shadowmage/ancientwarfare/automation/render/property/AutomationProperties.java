package net.shadowmage.ancientwarfare.automation.render.property;

import net.minecraft.block.properties.PropertyBool;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.common.property.PropertyFloat;

public class AutomationProperties {
    public static final IUnlistedProperty<Boolean> ACTIVE = Properties.toUnlisted(PropertyBool.create("active"));
    public static final IUnlistedProperty<Boolean> DYNAMIC = Properties.toUnlisted(PropertyBool.create("dynamic"));
    public static final IUnlistedProperty<Float> ROTATION = new PropertyFloat("rotation");
}
