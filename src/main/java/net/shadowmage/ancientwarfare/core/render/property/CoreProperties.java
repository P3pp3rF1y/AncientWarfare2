package net.shadowmage.ancientwarfare.core.render.property;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;

public class CoreProperties {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final IUnlistedProperty<EnumFacing> UNLISTED_HORIZONTAL_FACING = net.minecraftforge.common.property.Properties.toUnlisted(BlockHorizontal.FACING);
	public static final IUnlistedProperty<EnumFacing> UNLISTED_FACING = net.minecraftforge.common.property.Properties.toUnlisted(BlockDirectional.FACING);
	public static final PropertyBool VISIBLE = PropertyBool.create("visible");
}
