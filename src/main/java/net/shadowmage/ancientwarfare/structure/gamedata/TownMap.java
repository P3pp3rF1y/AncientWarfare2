package net.shadowmage.ancientwarfare.structure.gamedata;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.core.util.Zone;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.util.ConquerHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

//TODO world capability
public class TownMap extends WorldSavedData {
	private static final Cache<Zone, Set<TownEntry>> CHUNK_TOWN_ENTRIES = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();

	private Set<TownEntry> townEntries = new HashSet<>();

	public TownMap(String name) {
		super(name);
	}

	public void setGenerated(TownEntry townEntry) {
		townEntry.setTownMap(this);
		townEntries.add(townEntry);
		markDirty();
	}

	public boolean shouldPreventSpawnAtPos(World world, BlockPos pos) {
		for (TownEntry entry : getTownsInChunk(pos)) {
			if (entry.getBB().contains(pos) && entry.shouldPreventNaturalHostileSpawns() && !entry.getConquered()) {
				if (ConquerHelper.checkBBNotConquered(world, entry.getBB())) {
					return true;
				} else {
					entry.setConquered();
				}
			}
		}
		return false;
	}

	private Set<TownEntry> getTownsInChunk(BlockPos pos) {
		Set<TownEntry> towns;
		ChunkPos chunkPos = new ChunkPos(pos);
		BlockPos min = new BlockPos(chunkPos.x * 16, 1, chunkPos.z * 16);
		BlockPos max = new BlockPos(chunkPos.x * 16 + 15, 255, chunkPos.z * 16 + 15);
		Zone chunkZone = new Zone(min, max);
		try {
			towns = CHUNK_TOWN_ENTRIES.get(chunkZone, () -> getTownsIn(chunkZone));
		}
		catch (ExecutionException e) {
			AncientWarfareNPC.LOG.error("Error getting structure entries in chunk for hostile entity check: ", e);
			return new HashSet<>();
		}
		return towns;
	}

	private Set<TownEntry> getTownsIn(Zone zone) {
		Set<TownEntry> ret = new HashSet<>();
		for (TownEntry townEntry : townEntries) {
			if (townEntry.getBB().intersects(zone)) {
				ret.add(townEntry);
			}
		}
		return ret;
	}

	/*
	 * return the distance of the closest found town or defaultVal if no town was found closer
	 */
	public float getClosestTown(int bx, int bz, float defaultVal) {
		float distance = defaultVal;
		for (TownEntry townEntry : townEntries) {
			StructureBB bb = townEntry.getBB();
			float d = Trig.getDistance(bx, 0, bz, bb.getCenterX(), 0, bb.getCenterZ());
			if (d < distance) {
				distance = d;
			}
		}
		return distance;
	}

	public boolean intersectsWithTown(StructureBB bb) {
		for (TownEntry townEntry : townEntries) {
			if (townEntry.getBB().intersects(bb)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		townEntries.clear();
		legacyDeserialization(tag);

		NBTTagList list = tag.getTagList("townEntries", Constants.NBT.TAG_COMPOUND);
		for (NBTBase nbt : list) {
			townEntries.add(TownEntry.deserializeNBT((NBTTagCompound) nbt).setTownMap(this));
		}
	}

	//TODO remove in the future
	private void legacyDeserialization(NBTTagCompound tag) {
		NBTTagList list = tag.getTagList("boundingBoxes", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			StructureBB bb = new StructureBB(BlockPos.ORIGIN, BlockPos.ORIGIN);
			bb.deserializeNBT(list.getCompoundTagAt(i));
			townEntries.add(new TownEntry(bb, false).setTownMap(this));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		NBTTagList list = new NBTTagList();
		for (TownEntry townEntry : townEntries) {
			list.appendTag(townEntry.serializeNBT());
		}
		tag.setTag("townEntries", list);
		return tag;
	}
}
