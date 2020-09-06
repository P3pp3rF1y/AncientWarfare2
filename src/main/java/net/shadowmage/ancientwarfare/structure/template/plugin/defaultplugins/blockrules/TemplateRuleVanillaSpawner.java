package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.stream.StreamSupport;

import static net.shadowmage.ancientwarfare.npc.event.EventHandler.NO_SPAWN_PREVENTION_TAG;

public class TemplateRuleVanillaSpawner extends TemplateRuleBlockTile<TileEntityMobSpawner> {
	public static final String PLUGIN_NAME = "vanillaSpawner";
	private static final String SPAWN_DATA_TAG = "SpawnData";
	private static final String TAGS_TAG = "Tags";

	public TemplateRuleVanillaSpawner(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, state, turns);
		if (tag.hasKey(SPAWN_DATA_TAG)) {
			NBTTagCompound compound = tag.getCompoundTag(SPAWN_DATA_TAG);
			compound.setTag(TAGS_TAG, getTags(compound));
		}
		if (tag.hasKey("SpawnPotentials")) {
			NBTTagList spawnPotentials = tag.getTagList("SpawnPotentials", Constants.NBT.TAG_COMPOUND);
			for (NBTBase tag : spawnPotentials) {
				NBTTagCompound compound = (NBTTagCompound) tag;
				compound.setTag(TAGS_TAG, getTags(compound));
			}
		}
	}

	private NBTTagList getTags(NBTTagCompound compound) {
		NBTTagList tags = compound.hasKey(TAGS_TAG) ? compound.getTagList(TAGS_TAG, Constants.NBT.TAG_STRING) : new NBTTagList();
		if (!hasNoSpawnPreventionTag(tags)) {
			tags.appendTag(new NBTTagString(NO_SPAWN_PREVENTION_TAG));
		}
		return tags;
	}

	private boolean hasNoSpawnPreventionTag(NBTTagList tags) {
		return StreamSupport.stream(tags.spliterator(), false).anyMatch(n -> ((NBTTagString) n).getString().equals(NO_SPAWN_PREVENTION_TAG));
	}

	public TemplateRuleVanillaSpawner() {
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}
}
