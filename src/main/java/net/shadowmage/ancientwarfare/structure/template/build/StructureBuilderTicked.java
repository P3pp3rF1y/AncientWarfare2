package net.shadowmage.ancientwarfare.structure.template.build;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.MathUtils;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;

import java.util.Optional;

public class StructureBuilderTicked extends StructureBuilder {
	private static final String CLEAR_POS_TAG = "clearPos";
	private static final String CLEARED_TAG = "cleared";
	private static final String CURRENT_PRIORITY_TAG = "currentPriority";
	private static final String DESTINATION_TAG = "destination";
	public boolean invalid = false;
	private boolean hasClearedArea;
	private BlockPos clearPos;

	public StructureBuilderTicked(World world, StructureTemplate template, EnumFacing face, BlockPos pos) {
		super(world, template, face, pos);
		clearPos = new BlockPos(bb.min.getX(), bb.max.getY(), bb.min.getZ());
	}

	public StructureBuilderTicked()//nbt-constructor
	{

	}

	public void tick() {
		if (!hasClearedArea) {
			while (!breakClearTargetBlock()) {
				if (!incrementClear()) {
					hasClearedArea = true;
					break;
				}
			}
			if (!incrementClear()) {
				hasClearedArea = true;
			}
		} else if (!this.isFinished()) {
			while (!this.isFinished()) {
				if (placeAtCurrentPos())
					break;
			}
			increment();//finally, increment to next position (will trigger isFinished if actually done, has no problems if already finished)
		}
	}

	private boolean placeAtCurrentPos() {
		Optional<TemplateRuleBlock> rule = template.getRuleAt(curTempPos);
		if (!rule.isPresent() || !rule.get().placeInSurvival() || !rule.get().shouldPlaceOnBuildPass(world, turns, destination, currentPriority)) {
			increment();//skip that position, was either air/null rule, or could not be placed on current pass, auto-increment to next
		} else//place it...
		{
			rule.ifPresent(this::placeRule);
			return true;
		}
		return false;
	}

	private boolean breakClearTargetBlock() {
		return BlockTools.breakBlockAndDrop(world, clearPos);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean incrementClear() {
		clearPos = clearPos.east();
		if (clearPos.getX() > bb.max.getX()) {
			clearPos = new BlockPos(bb.min.getX(), clearPos.getY(), clearPos.getZ());
			clearPos = clearPos.south();
			if (clearPos.getZ() > bb.max.getZ()) {
				clearPos = new BlockPos(bb.min.getX(), clearPos.down().getY(), bb.min.getZ());
				return clearPos.getY() >= bb.min.getY();
			}
		}
		return true;
	}

	public void setWorld(World world)//should be called on first-update of the TE (after its world is set)
	{
		this.world = world;
	}

	public World getWorld() {
		return world;
	}

	public void readFromNBT(NBTTagCompound tag)//should be called immediately after construction
	{
		String name = tag.getString("name");
		Optional<StructureTemplate> template = StructureTemplateManager.getTemplate(name);
		if (template.isPresent()) {
			this.template = template.get();
			curTempPos = MathUtils.fromLong(tag.getLong("pos"));
			this.clearPos = BlockPos.fromLong(tag.getLong(CLEAR_POS_TAG));
			this.hasClearedArea = tag.getBoolean(CLEARED_TAG);
			this.turns = tag.getInteger("turns");
			this.buildFace = EnumFacing.VALUES[tag.getByte("buildFace")];
			this.maxPriority = tag.getInteger("maxPriority");
			this.currentPriority = tag.getInteger(CURRENT_PRIORITY_TAG);
			this.destination = BlockPos.fromLong(tag.getLong(DESTINATION_TAG));

			this.bb = new StructureBB(BlockPos.fromLong(tag.getLong("bbMin")), BlockPos.fromLong(tag.getLong("bbMax")));
			this.buildOrigin = BlockPos.fromLong(tag.getLong("buildOrigin"));
			this.incrementDestination();
		} else {
			invalid = true;
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setString("name", template.name);
		tag.setByte("face", (byte) getBuildFace().ordinal());
		tag.setInteger("turns", turns);
		tag.setInteger("maxPriority", maxPriority);
		tag.setInteger(CURRENT_PRIORITY_TAG, currentPriority);
		tag.setLong("pos", MathUtils.toLong(curTempPos));
		tag.setLong(CLEAR_POS_TAG, clearPos.toLong());
		tag.setBoolean(CLEARED_TAG, hasClearedArea);
		tag.setLong(DESTINATION_TAG, destination.toLong());

		tag.setLong("buildOrigin", buildOrigin.toLong());
		tag.setLong("bbMin", bb.min.toLong());
		tag.setLong("bbMax", bb.max.toLong());
	}

	@Override
	public StructureTemplate getTemplate() {
		return template;
	}

	public NBTTagCompound serializeProgressData() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger(CURRENT_PRIORITY_TAG, currentPriority);
		tag.setLong("pos", MathUtils.toLong(curTempPos));
		tag.setLong(CLEAR_POS_TAG, clearPos.toLong());
		tag.setBoolean(CLEARED_TAG, hasClearedArea);
		tag.setLong(DESTINATION_TAG, destination.toLong());
		return tag;
	}

	public void deserializeProgressData(NBTTagCompound tag) {
		currentPriority = tag.getInteger(CURRENT_PRIORITY_TAG);
		curTempPos = MathUtils.fromLong(tag.getLong("pos"));
		clearPos = BlockPos.fromLong(tag.getLong(CLEAR_POS_TAG));
		hasClearedArea = tag.getBoolean(CLEARED_TAG);
		destination = BlockPos.fromLong(tag.getLong(DESTINATION_TAG));
	}

	public float getPercentDoneClearing() {
		float max = getTotalBlocks();
		BlockPos relativeClearPos = clearPos.add(-bb.min.getX(), -bb.max.getY(), -bb.min.getZ());
		float current = (float) -relativeClearPos.getY() * (bb.getXSize() * bb.getZSize());//add layers done
		current += relativeClearPos.getZ() * bb.getXSize();//add rows done
		current += relativeClearPos.getX();//add blocks done
		return current / max;
	}

	public boolean hasClearedArea() {
		return hasClearedArea;
	}
}
