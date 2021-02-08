package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.structure.util.LootHelper;

import javax.annotation.Nullable;

public class TileAdvancedLootChest extends TileEntityChest implements ISpecialLootContainer {
	private static final String LOOT_SETTINGS_TAG = "lootSettings";

	private LootSettings lootSettings = new LootSettings();

	public boolean fillWithLootAndCheckIfGoodToOpen(@Nullable EntityPlayer player) {
		return LootHelper.fillWithLootAndCheckIfGoodToOpen(this, player != null ? player : EntityTools.findClosestPlayer(world, pos, 100));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		lootSettings = LootSettings.deserializeNBT(compound.getCompoundTag(LOOT_SETTINGS_TAG));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		compound.setTag(LOOT_SETTINGS_TAG, lootSettings.serializeNBT());

		return compound;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag(LOOT_SETTINGS_TAG, lootSettings.serializeNBT());
		return new SPacketUpdateTileEntity(pos, 0, tag);
	}

	@SuppressWarnings("squid:S4449")
	@Override
	@SideOnly(Side.CLIENT)
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound tag = pkt.getNbtCompound();
		if (tag.hasKey(LOOT_SETTINGS_TAG)) {
			lootSettings = LootSettings.deserializeNBT(tag.getCompoundTag(LOOT_SETTINGS_TAG));
		}
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		tag.setTag(LOOT_SETTINGS_TAG, lootSettings.serializeNBT());
		return tag;
	}

	@Override
	@SuppressWarnings("squid:S4449")
	public void handleUpdateTag(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey(LOOT_SETTINGS_TAG)) {
			lootSettings = LootSettings.deserializeNBT(tag.getCompoundTag(LOOT_SETTINGS_TAG));
		}
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if (world == null || world.isRemote) {
			return this.getItems().get(index);
		}
		return super.getStackInSlot(index);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 2, 2));
	}

	@Override
	public void setLootSettings(LootSettings settings) {
		this.lootSettings = settings;
	}

	@Override
	public LootSettings getLootSettings() {
		return lootSettings;
	}
}
