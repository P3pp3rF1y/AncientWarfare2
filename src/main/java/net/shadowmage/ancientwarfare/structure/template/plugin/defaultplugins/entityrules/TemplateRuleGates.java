package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules;

import com.google.common.primitives.Ints;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.owner.Owner;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntityBase;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.gates.types.Gate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TemplateRuleGates extends TemplateRuleEntityBase {

	public static final String PLUGIN_NAME = "awGate";
	private String owner;
	private String gateType;
	private EnumFacing orientation;
	private BlockPos pos1;
	private BlockPos pos2;

	public TemplateRuleGates(World world, Entity entity, int turns, int x, int y, int z) {
		super(world, entity, turns, x, y, z);
		EntityGate gate = (EntityGate) entity;

		this.pos1 = BlockTools.rotateAroundOrigin(gate.pos1.add(-x, -y, -z), turns);
		this.pos2 = BlockTools.rotateAroundOrigin(gate.pos2.add(-x, -y, -z), turns);
		this.orientation = EnumFacing.HORIZONTALS[(gate.gateOrientation.getHorizontalIndex() + turns) % 4];
		this.gateType = Gate.getGateNameFor(gate);
		this.owner = gate.getOwner().getName();
	}

	public TemplateRuleGates() {
		super();
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		BlockPos p1 = BlockTools.rotateAroundOrigin(pos1, turns).add(pos);
		BlockPos p2 = BlockTools.rotateAroundOrigin(pos2, turns).add(pos);

		BlockPos min = BlockTools.getMin(p1, p2);
		BlockPos max = BlockTools.getMax(p1, p2);
		for (int x1 = min.getX(); x1 <= max.getX(); x1++) {
			for (int y1 = min.getY(); y1 <= max.getY(); y1++) {
				for (int z1 = min.getZ(); z1 <= max.getZ(); z1++) {
					world.setBlockToAir(new BlockPos(x1, y1, z1));
				}
			}
		}

		Optional<EntityGate> gate = Gate.constructGate(world, p1, p2, Gate.getGateByName(gateType),
				EnumFacing.HORIZONTALS[Ints.constrainToRange((orientation.getHorizontalIndex() + turns) % 4, 0, 4)],
				owner.isEmpty() ? Owner.EMPTY : new Owner(world, owner));
		if (!gate.isPresent()) {
			AncientWarfareStructure.LOG.warn("Could not create gate for type: " + gateType);
			return;
		}
		world.spawnEntity(gate.get());
	}

	@Override
	public void parseRule(NBTTagCompound tag) {
		super.parseRule(tag);
		gateType = tag.getString("gateType");
		orientation = EnumFacing.VALUES[tag.getByte("orientation")];
		pos1 = NBTHelper.readBlockPosFromNBT(tag.getCompoundTag("pos1"));
		pos2 = NBTHelper.readBlockPosFromNBT(tag.getCompoundTag("pos2"));
		owner = tag.getString("owner");
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		tag.setString("gateType", gateType);
		tag.setByte("orientation", (byte) orientation.ordinal());
		tag.setTag("pos1", NBTHelper.writeBlockPosToNBT(new NBTTagCompound(), pos1));
		tag.setTag("pos2", NBTHelper.writeBlockPosToNBT(new NBTTagCompound(), pos2));
		tag.setString("owner", owner);
	}

	@Override
	public List<ItemStack> getResources() {
		return Collections.singletonList(Gate.getItemToConstruct(Gate.getGateByName(gateType).getGlobalID()));
	}

	@Override
	public boolean shouldPlaceOnBuildPass(World world, int turns, BlockPos pos, int buildPass) {
		return buildPass == 3;
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}
}
