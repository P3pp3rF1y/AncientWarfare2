package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IBoundedSite;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;

import java.util.Optional;

public class TemplateRuleRotatable extends TemplateRuleBlock {
	private static final String TE_DATA_TAG = "teData";
	public static final String PLUGIN_NAME = "rotatable";
	private EnumFacing orientation;
	private BlockPos p1;
	private BlockPos p2;
	NBTTagCompound tag;

	public TemplateRuleRotatable(World world, BlockPos pos, IBlockState state, int turns) {
		super(state, turns);
		Optional<TileEntity> te = WorldTools.getTile(world, pos);
		if (te.isPresent()) {
			TileEntity worksite = te.get();
			EnumFacing o = ((BlockRotationHandler.IRotatableTile) worksite).getPrimaryFacing();
			if (o.getAxis() != EnumFacing.Axis.Y) {
				for (int i = 0; i < turns; i++) {
					o = o.rotateY();
				}
			}
			this.orientation = o;
			if (worksite instanceof IBoundedSite && ((IBoundedSite) worksite).hasWorkBounds()) {
				p1 = BlockTools.rotateAroundOrigin(((IBoundedSite) worksite).getWorkBoundsMin().add(-pos.getX(), -pos.getY(), -pos.getZ()), turns);
				p2 = BlockTools.rotateAroundOrigin(((IBoundedSite) worksite).getWorkBoundsMax().add(-pos.getX(), -pos.getY(), -pos.getZ()), turns);
			}
			tag = new NBTTagCompound();
			worksite.writeToNBT(tag);
		}
	}

	public TemplateRuleRotatable() {
		super();
	}

	@Override
	public boolean shouldReuseRule(World world, IBlockState state, int turns, BlockPos pos) {
		return false;
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		if (world.setBlockState(pos, state, 2)) {
			Optional<TileEntity> te = WorldTools.getTile(world, pos);
			if (te.isPresent()) {
				TileEntity worksite = te.get();
				tag.setInteger("x", pos.getX());
				tag.setInteger("y", pos.getY());
				tag.setInteger("z", pos.getZ());
				worksite.readFromNBT(tag);
				EnumFacing o = orientation;
				if (o.getAxis() != EnumFacing.Axis.Y) {
					for (int i = 0; i < turns; i++) {
						o = o.rotateY();
					}
				}
				((BlockRotationHandler.IRotatableTile) worksite).setPrimaryFacing(o);
				if (worksite instanceof IBoundedSite && p1 != null && p2 != null) {
					BlockPos pos1 = BlockTools.rotateAroundOrigin(p1, turns).add(pos);
					BlockPos pos2 = BlockTools.rotateAroundOrigin(p2, turns).add(pos);
					((IBoundedSite) worksite).setBounds(pos1, pos2);
				}
				BlockTools.notifyBlockUpdate(world, pos);
			}
		}
	}

	@Override
	public void parseRule(NBTTagCompound tag) {
		super.parseRule(tag);
		this.orientation = EnumFacing.values()[tag.getInteger("orientation")];
		if (tag.hasKey(TE_DATA_TAG)) {
			this.tag = tag.getCompoundTag(TE_DATA_TAG);
		}
		if (tag.hasKey("pos1")) {
			this.p1 = NBTHelper.readBlockPosFromNBT(tag.getCompoundTag("pos1"));
		}
		if (tag.hasKey("pos2")) {
			this.p2 = NBTHelper.readBlockPosFromNBT(tag.getCompoundTag("pos2"));
		}
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		tag.setInteger("orientation", orientation.ordinal());
		if (p1 != null) {
			tag.setTag("pos1", NBTHelper.writeBlockPosToNBT(new NBTTagCompound(), p1));
		}
		if (p2 != null) {
			tag.setTag("pos2", NBTHelper.writeBlockPosToNBT(new NBTTagCompound(), p2));
		}
		if (this.tag != null) {
			tag.setTag(TE_DATA_TAG, this.tag);
		}
	}

	@Override
	public boolean shouldPlaceOnBuildPass(World world, int turns, BlockPos pos, int buildPass) {
		return buildPass == 0;
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}
}
