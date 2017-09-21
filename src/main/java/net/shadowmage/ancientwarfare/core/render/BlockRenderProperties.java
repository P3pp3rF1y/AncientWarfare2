package net.shadowmage.ancientwarfare.core.render;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;

public class BlockRenderProperties {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final IUnlistedProperty<EnumFacing> UNLISTED_FACING = Properties.toUnlisted(BlockHorizontal.FACING);
}
