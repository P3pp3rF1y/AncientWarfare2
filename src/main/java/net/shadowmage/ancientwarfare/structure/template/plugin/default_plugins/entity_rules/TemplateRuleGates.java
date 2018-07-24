package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.entity_rules;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.owner.Owner;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntity;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.gates.types.Gate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuildingException;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuildingException.EntityPlacementException;

import java.util.Optional;

public class TemplateRuleGates extends TemplateRuleEntity {

	private String owner;
	private String gateType;
	private EnumFacing orientation;
	private BlockPos pos1 = BlockPos.ORIGIN;
	private BlockPos pos2 = BlockPos.ORIGIN;

	/*
	 * scanner-constructor.  called when scanning an entity.
	 *
	 * @param world  the world containing the scanned area
	 * @param entity the entity being scanned
	 * @param turns  how many 90' turns to rotate entity for storage in template
	 * @param x      world x-coord of the enitty (floor(posX)
	 * @param y      world y-coord of the enitty (floor(posY)
	 * @param z      world z-coord of the enitty (floor(posZ)
	 */
	@SuppressWarnings("unused") //used in reflection
	public TemplateRuleGates(World world, Entity entity, int turns, int x, int y, int z) {
		super(world, entity, turns, x, y, z);
		EntityGate gate = (EntityGate) entity;

		this.pos1 = BlockTools.rotateAroundOrigin(gate.pos1.add(-x, -y, -z), turns);
		this.pos2 = BlockTools.rotateAroundOrigin(gate.pos2.add(-x, -y, -z), turns);
		this.orientation = EnumFacing.HORIZONTALS[(gate.gateOrientation.ordinal() + turns) % 4];
		this.gateType = Gate.getGateNameFor(gate);
		this.owner = gate.getOwner().getName();
	}

	@SuppressWarnings("unused") //used in reflection
	public TemplateRuleGates() {

	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) throws EntityPlacementException {
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

		Optional<EntityGate> gate = Gate.constructGate(world, p1, p2, Gate.getGateByName(gateType), EnumFacing.HORIZONTALS[((orientation.ordinal() + turns) % 4)],
				owner.isEmpty() ? Owner.EMPTY : new Owner(world, owner));
		if (!gate.isPresent()) {
			throw new StructureBuildingException.EntityPlacementException("Could not create gate for type: " + gateType);
		}
		world.spawnEntity(gate.get());
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		gateType = tag.getString("gateType");
		orientation = EnumFacing.VALUES[tag.getByte("orientation")];
		pos1 = getBlockPosFromNBT(tag.getCompoundTag("pos1"));
		pos2 = getBlockPosFromNBT(tag.getCompoundTag("pos2"));
		owner = tag.getString("owner");
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		tag.setString("gateType", gateType);
		tag.setByte("orientation", (byte) orientation.ordinal());
		tag.setTag("pos1", writeBlockPosToNBT(new NBTTagCompound(), pos1));
		tag.setTag("pos2", writeBlockPosToNBT(new NBTTagCompound(), pos2));
		tag.setString("owner", owner);
	}

	@Override
	public void addResources(NonNullList<ItemStack> resources) {
		resources.add(Gate.getItemToConstruct(Gate.getGateByName(gateType).getGlobalID()));
	}

	@Override
	public boolean shouldPlaceOnBuildPass(World world, int turns, BlockPos pos, int buildPass) {
		return buildPass == 3;
	}

}
