package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;

public class ItemStructureBuilderWorldGen extends ItemStructureBuilder {
	public ItemStructureBuilderWorldGen(String name) {
		super(name);
	}

	@Override
	protected void buildStructure(EntityPlayer player, BlockPos hit, EnumFacing facing, StructureBuilder builder) {
		builder.getTemplate().getValidationSettings().preGeneration(player.world, hit, facing, builder.getTemplate(), builder.getBoundingBox());
		super.buildStructure(player, hit, facing, builder);
		builder.getTemplate().getValidationSettings().postGeneration(player.world, hit, builder.getBoundingBox(), builder.getTemplate());
	}
}
