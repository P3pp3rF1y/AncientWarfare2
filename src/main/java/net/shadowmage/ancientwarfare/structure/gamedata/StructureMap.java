//TODO world capability
package net.shadowmage.ancientwarfare.structure.gamedata;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.structure.network.PacketStructureEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class StructureMap extends WorldSavedData {

	private StructureDimensionMap map;

	public StructureMap(String name) {
		super(name);
		map = new StructureDimensionMap();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		NBTTagCompound mapTag = nbttagcompound.getCompoundTag("map");
		map.readFromNBT(mapTag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		NBTTagCompound mapTag = new NBTTagCompound();
		map.writeToNBT(mapTag);
		nbttagcompound.setTag("map", mapTag);
		return nbttagcompound;
	}

	public Collection<StructureEntry> getEntriesNear(World world, int worldX, int worldZ, int chunkRadius, boolean expandBySize, Collection<StructureEntry> list) {
		int cx = worldX >> 4;
		int cz = worldZ >> 4;
		return map.getEntriesNear(world.provider.getDimension(), cx, cz, chunkRadius, expandBySize, list);
	}

	public Optional<StructureEntry> getStructureAt(World world, BlockPos pos) {
		for (StructureEntry structure : getEntriesNear(world, pos.getX(), pos.getZ(), 1, true, new ArrayList<>())) {
			if (structure.getBB().contains(pos)) {
				return Optional.of(structure);
			}
		}
		return Optional.empty();
	}

	public Optional<StructureEntry> getStructureAt(World world, int chunkX, int chunkZ) {
		return map.getEntryAt(world.provider.getDimension(), chunkX, chunkZ);
	}

	public void setGeneratedAt(World world, int worldX, int worldY, int worldZ, EnumFacing face, StructureEntry entry, boolean unique) {
		int cx = worldX >> 4;
		int cz = worldZ >> 4;
		int dimension = world.provider.getDimension();
		setGeneratedAt(dimension, cx, cz, entry, unique);
	}

	public void setGeneratedAt(int dimension, int cx, int cz, StructureEntry entry, boolean unique) {
		map.setGeneratedAt(dimension, cx, cz, entry, unique);
		markDirty();
		NetworkHandler.sendToAllPlayers(new PacketStructureEntry(dimension, cx, cz, entry));
	}

	public boolean isGeneratedUnique(String name) {
		return this.map.generatedUniques.contains(name);
	}

	public void synchronizeFromNBT(NBTTagCompound mapTag) {
		map.readFromNBT(mapTag);
	}

	private class StructureDimensionMap {
		private HashMap<Integer, StructureWorldMap> mapsByDimension = new HashMap<>();
		private Set<String> generatedUniques = new HashSet<>();

		private Collection<StructureEntry> getEntriesNear(int dimension, int chunkX, int chunkZ, int chunkRadius, boolean expandBySize, Collection<StructureEntry> list) {
			if (mapsByDimension.containsKey(dimension)) {
				return mapsByDimension.get(dimension).getEntriesNear(chunkX, chunkZ, chunkRadius, expandBySize, list);
			}
			return Collections.emptyList();
		}

		private Optional<StructureEntry> getEntryAt(int dimension, int chunkX, int chunkZ) {
			if (!mapsByDimension.containsKey(dimension)) {
				return Optional.empty();
			}
			return mapsByDimension.get(dimension).getEntryAt(chunkX, chunkZ);
		}

		private void setGeneratedAt(int dimension, int chunkX, int chunkZ, StructureEntry entry, boolean unique) {
			if (!this.mapsByDimension.containsKey(dimension)) {
				this.mapsByDimension.put(dimension, new StructureWorldMap());
			}
			this.mapsByDimension.get(dimension).setGeneratedAt(chunkX, chunkZ, entry);
			if (unique) {
				generatedUniques.add(entry.name);
			}
		}

		public void readFromNBT(NBTTagCompound nbttagcompound) {
			NBTTagList uniquesList = nbttagcompound.getTagList("uniques", Constants.NBT.TAG_STRING);
			NBTTagList dimensionList = nbttagcompound.getTagList("dimensions", Constants.NBT.TAG_COMPOUND);

			NBTTagCompound dimensionTag;
			int dim;
			for (int i = 0; i < dimensionList.tagCount(); i++) {
				dimensionTag = dimensionList.getCompoundTagAt(i);
				dim = dimensionTag.getInteger("dim");
				if (!this.mapsByDimension.containsKey(dim)) {
					this.mapsByDimension.put(dim, new StructureWorldMap());
				}
				this.mapsByDimension.get(dim).readFromNBT(dimensionTag.getCompoundTag("data"));
			}

			for (int i = 0; i < uniquesList.tagCount(); i++) {
				generatedUniques.add(uniquesList.getStringTagAt(i));
			}
		}

		public void writeToNBT(NBTTagCompound nbttagcompound) {
			NBTTagList dimensionsList = new NBTTagList();
			NBTTagList uniquesList = new NBTTagList();
			NBTTagCompound dimensionTag;
			NBTTagCompound dimensionData;
			for (Integer dim : this.mapsByDimension.keySet()) {
				dimensionTag = new NBTTagCompound();
				dimensionData = new NBTTagCompound();
				dimensionTag.setInteger("dim", dim);
				mapsByDimension.get(dim).writeToNBT(dimensionData);
				dimensionTag.setTag("data", dimensionData);
				dimensionsList.appendTag(dimensionTag);
			}

			for (String name : this.generatedUniques) {
				uniquesList.appendTag(new NBTTagString(name));
			}
			nbttagcompound.setTag("dimensions", dimensionsList);
			nbttagcompound.setTag("uniques", uniquesList);
		}
	}//end structure dimension map

	public class StructureWorldMap {

		private HashMap<Integer, HashMap<Integer, StructureEntry>> worldMap = new HashMap<>();
		private int largestGeneratedX;
		private int largestGeneratedZ;

		public Collection<StructureEntry> getEntriesNear(int chunkX, int chunkZ, int chunkRadius, boolean expandBySize, Collection<StructureEntry> list) {
			StructureEntry entry;
			int crx = chunkRadius;
			int crz = chunkRadius;
			if (expandBySize) {
				crx += largestGeneratedX / 16;
				crz += largestGeneratedZ / 16;
			}
			for (int x = chunkX - crx; x <= chunkX + crx; x++) {
				if (worldMap.containsKey(x)) {
					for (int z = chunkZ - crz; z <= chunkZ + crz; z++) {
						entry = worldMap.get(x).get(z);
						if (entry != null) {
							list.add(entry);
						}
					}
				}
			}
			return list;
		}

		public void setGeneratedAt(int chunkX, int chunkZ, StructureEntry entry) {
			if (!this.worldMap.containsKey(chunkX)) {
				this.worldMap.put(chunkX, new HashMap<>());
			}
			this.worldMap.get(chunkX).put(chunkZ, entry);
			int x = entry.bb.getXSize();
			int z = entry.bb.getZSize();
			if (x > largestGeneratedX) {
				largestGeneratedX = x;
			}
			if (z > largestGeneratedZ) {
				largestGeneratedZ = z;
			}
		}

		public Optional<StructureEntry> getEntryAt(int chunkX, int chunkZ) {
			if (!worldMap.containsKey(chunkX)) {
				return Optional.empty();
			}
			return Optional.ofNullable(worldMap.get(chunkX).get(chunkZ));
		}

		public void readFromNBT(NBTTagCompound nbttagcompound) {
			NBTTagList entryList = nbttagcompound.getTagList("entries", Constants.NBT.TAG_COMPOUND);
			StructureEntry entry;
			NBTTagCompound entryTag;
			int x;
			int z;
			for (int i = 0; i < entryList.tagCount(); i++) {
				entryTag = entryList.getCompoundTagAt(i);
				x = entryTag.getInteger("x");
				z = entryTag.getInteger("z");
				entry = new StructureEntry();
				entry.readFromNBT(entryTag);
				if (!this.worldMap.containsKey(x)) {
					this.worldMap.put(x, new HashMap<>());
				}
				this.worldMap.get(x).put(z, entry);
			}
			this.largestGeneratedX = nbttagcompound.getInteger("largestX");
			this.largestGeneratedZ = nbttagcompound.getInteger("largestZ");
		}

		public void writeToNBT(NBTTagCompound nbttagcompound) {
			NBTTagList entryList = new NBTTagList();
			NBTTagCompound entryTag;
			for (Integer x : this.worldMap.keySet()) {
				for (Integer z : this.worldMap.get(x).keySet()) {
					entryTag = new NBTTagCompound();
					entryTag.setInteger("x", x);
					entryTag.setInteger("z", z);
					this.worldMap.get(x).get(z).writeToNBT(entryTag);
					entryList.appendTag(entryTag);
				}
			}
			nbttagcompound.setInteger("largestX", largestGeneratedX);
			nbttagcompound.setInteger("largestZ", largestGeneratedZ);
			nbttagcompound.setTag("entries", entryList);
		}
	}//end structure X Map

}
