package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules;

import net.minecraft.entity.EntityHanging;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class TemplateRuleEntityHanging extends TemplateRuleEntity<EntityHanging> {
	public static final String PLUGIN_NAME = "vanillaHangingEntity";
	private EnumFacing direction;
	private BlockPos hangingOffset;

	public TemplateRuleEntityHanging(World world, EntityHanging entity, int turns, int x, int y, int z) {
		super(world, entity, turns, x, y, z);
		BlockPos hangingOffset = entity.getHangingPosition().add(-x, -y, -z);
		this.hangingOffset = BlockTools.rotateHorizontal(hangingOffset, turns);
		direction = EnumFacing.HORIZONTALS[(Optional.ofNullable(entity.facingDirection).map(EnumFacing::getHorizontalIndex).orElse(0) + turns) % 4];
	}

	public TemplateRuleEntityHanging() {
		super();
	}

	@Override
	protected NBTTagCompound getEntityNBT(BlockPos pos, int turns) {
		NBTTagCompound tag = super.getEntityNBT(pos, turns);
		EnumFacing rotateDirection = EnumFacing.HORIZONTALS[(direction.getHorizontalIndex() + turns) % 4];
		tag.setByte("Facing", (byte) rotateDirection.getHorizontalIndex());

		return tag;
	}

	@Override
	protected void updateEntityOnPlacement(int turns, BlockPos pos, EntityHanging e) {
		super.updateEntityOnPlacement(turns, pos, e);
		e.hangingPosition = pos.add(BlockTools.rotateHorizontal(hangingOffset, turns));
		updateBoundingBox(e);
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		tag.setByte("direction", (byte) direction.getHorizontalIndex());
		tag.setLong("hangingOffset", hangingOffset.toLong());
	}

	@Override
	public void parseRule(NBTTagCompound tag) {
		super.parseRule(tag);
		direction = EnumFacing.HORIZONTALS[tag.getByte("direction") % 4];
		hangingOffset = BlockPos.fromLong(tag.getLong("hangingOffset"));
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}

	private static final Method UPDATE_BB = ObfuscationReflectionHelper.findMethod(EntityHanging.class, "func_174856_o", void.class);

	private static void updateBoundingBox(EntityHanging entityHanging) {
		try {
			UPDATE_BB.invoke(entityHanging);
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			AncientWarfareStructure.LOG.error("Unable to update entity hanging's bounding box", e);
		}
	}
}
