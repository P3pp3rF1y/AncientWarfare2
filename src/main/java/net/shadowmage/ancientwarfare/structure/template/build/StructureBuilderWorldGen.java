package net.shadowmage.ancientwarfare.structure.template.build;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;

public class StructureBuilderWorldGen extends StructureBuilder {

	public StructureBuilderWorldGen(World world, StructureTemplate template, EnumFacing face, BlockPos pos) {
		super(world, template, face, pos);
	}

	@Override
	public void instantConstruction() {
		template.getValidationSettings().preGeneration(world, buildOrigin, getBuildFace(), template, bb);
		super.instantConstruction();
		template.getValidationSettings().postGeneration(world, buildOrigin, bb);
	}

}
