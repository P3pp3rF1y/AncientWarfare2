package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.entity_rules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntity;

import java.util.List;

public class TemplateRuleVanillaEntity extends TemplateRuleEntity {
	public static final String PLUGIN_NAME = "vanillaEntities";
	public ResourceLocation registryName;
	public float xOffset;
	public float zOffset;
	public float rotation;

	public TemplateRuleVanillaEntity(World world, Entity entity, int turns, int x, int y, int z) {
		super(world, entity, turns, x, y, z);
		this.registryName = EntityList.getKey(entity);
		rotation = (entity.rotationYaw + 90.f * turns) % 360.f;
		float x1, z1;
		x1 = (float) (entity.posX % 1.d);
		z1 = (float) (entity.posZ % 1.d);
		if (x1 < 0) {
			x1++;
		}
		if (z1 < 0) {
			z1++;
		}
		xOffset = BlockTools.rotateFloatX(x1, z1, turns);
		zOffset = BlockTools.rotateFloatZ(x1, z1, turns);
	}

	public TemplateRuleVanillaEntity(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException {
		super(ruleNumber, lines);
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		Entity e = EntityList.createEntityByIDFromName(registryName, world);
		if (e == null) {
			AncientWarfareStructure.LOG.warn("Could not create entity for type: " + registryName.toString());
			return;
		}
		float x1 = BlockTools.rotateFloatX(xOffset, zOffset, turns);
		float z1 = BlockTools.rotateFloatZ(xOffset, zOffset, turns);
		float yaw = (rotation + 90.f * turns) % 360.f;
		e.setPosition(pos.getX() + x1, pos.getY(), pos.getZ() + z1);
		e.rotationYaw = yaw;
		world.spawnEntity(e);
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		tag.setString("mobID", registryName.toString());
		tag.setFloat("xOffset", xOffset);
		tag.setFloat("zOffset", zOffset);
		tag.setFloat("rotation", rotation);
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		registryName = new ResourceLocation(tag.getString("mobID"));
		xOffset = tag.getFloat("xOffset");
		zOffset = tag.getFloat("zOffset");
		rotation = tag.getFloat("rotation");
	}

	@Override
	public boolean shouldPlaceOnBuildPass(World world, int turns, BlockPos pos, int buildPass) {
		return buildPass == 3;
	}

	@Override
	public void addResources(NonNullList<ItemStack> resources) {
		//noop
	}

	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}
}
