package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nullable;

public class TileAdvancedLootChest extends TileEntityChest implements ISpecialLootContainer {
	private static final String LOOT_SETTINGS_TAG = "lootSettings";

	private LootSettings lootSettings = new LootSettings();

	@Override
	public void fillWithLoot(@Nullable EntityPlayer player) {
		if (!world.isRemote && lootSettings.hasLoot()) {
			lootSettings.setHasLoot(false);
			lootSettings.getLootTableName().ifPresent(lootTable -> {
				//noinspection ConstantConditions
				InventoryTools.generateLootFor(world, player, getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), world.rand, lootTable, lootSettings.getLootRolls());
				BlockTools.notifyBlockUpdate(this);
			});
			lootSettings.removeLoot();
		}
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
		if (world.isRemote) {
			return this.getItems().get(index);
		}
		return super.getStackInSlot(index);
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
