package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.tile.IBlockBreakHandler;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.structure.util.LootHelper;

public class TileUrn extends TileUpdatable implements ISpecialLootContainer, IBlockBreakHandler {
	private LootSettings lootSettings = new LootSettings();

	@Override
	public void setLootSettings(LootSettings settings) {
		lootSettings = settings;
	}

	@Override
	public LootSettings getLootSettings() {
		return lootSettings;
	}

	@Override
	public void onBlockBroken(IBlockState state) {
		LootHelper.dropLoot(this, EntityTools.findClosestPlayer(world, pos, 100));
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		writeNBT(tag);
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		readNBT(tag);
	}

	private void readNBT(NBTTagCompound tag) {
		lootSettings = LootSettings.deserializeNBT(tag.getCompoundTag("lootSettings"));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		return writeNBT(super.writeToNBT(compound));
	}

	private NBTTagCompound writeNBT(NBTTagCompound tagCompound) {
		tagCompound.setTag("lootSettings", lootSettings.serializeNBT());
		return tagCompound;
	}
}
