package net.shadowmage.ancientwarfare.structure.worldgen;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.registry.TerritorySettingRegistry;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Territory implements INBTSerializable<NBTTagCompound> {
	private String territoryId;
	private String territoryName;
	private int totalClusterValue = 0;
	private Set<Long> chunkPositions = new HashSet<>();
	private BlockPos territoryCenter = BlockPos.ORIGIN;
	private Set<BlockPos> chunkCenters = new HashSet<>();

	Territory() {
	}

	Territory(String territoryId, String territoryName) {
		this.territoryId = territoryId;
		this.territoryName = territoryName;
	}

	String getTerritoryId() {
		return territoryId;
	}

	public void addClusterValue(int value) {
		totalClusterValue += value;
	}

	void addChunk(long chunkPos) {
		chunkPositions.add(chunkPos);
		chunkCenters.add(getChunkCenter(chunkPos));
		territoryCenter = TerritoryManager.getTerritoryCenter(chunkCenters);
	}

	private BlockPos getChunkCenter(long chunkPos) {
		return TerritoryManager.getChunkCenterPos((int) (chunkPos & 4294967295L), (int) (chunkPos >> 32 & 4294967295L));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Territory territory = (Territory) o;
		return territoryId.equals(territory.territoryId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(territoryId);
	}

	public String getTerritoryName() {
		return territoryName;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound ret = new NBTTagCompound();
		ret.setString("territoryId", territoryId);
		ret.setString("territoryName", territoryName);
		ret.setInteger("totalClusterValue", totalClusterValue);
		ret.setTag("chunkPositions", NBTHelper.getTagList(chunkPositions, NBTTagLong::new));
		return ret;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		territoryId = nbt.getString("territoryId");
		territoryName = nbt.getString("territoryName");
		totalClusterValue = nbt.getInteger("totalClusterValue");
		chunkPositions = NBTHelper.getSet(nbt.getTagList("chunkPositions", Constants.NBT.TAG_LONG), n -> ((NBTTagLong) n).getLong());
		chunkCenters.clear();
		chunkPositions.forEach(cp -> chunkCenters.add(getChunkCenter(cp)));
		territoryCenter = TerritoryManager.getTerritoryCenter(chunkCenters);
	}

	Set<Long> getChunkPositions() {
		return chunkPositions;
	}

	public BlockPos getTerritoryCenter() {
		return territoryCenter;
	}

	public int getRemainingClusterValue() {
		return (int) (TerritorySettingRegistry.getTerritorySettings(territoryName).getPerChunkClusterValueMultiplier() * AWStructureStatics.chunkClusterValue * chunkPositions.size()) - totalClusterValue;
	}
}
