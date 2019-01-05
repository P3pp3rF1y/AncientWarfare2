package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nullable;

public class TileAdvancedLootChest extends TileEntityChest {
	private static final String LOOT_TABLE_TAG = "lootTable";
	private static final String LOOT_ROLLS_TAG = "lootRolls";
	private int lootRolls = 0;

	@Override
	public void fillWithLoot(@Nullable EntityPlayer player) {
		if (lootTable != null) {
			ResourceLocation lt = lootTable;
			lootTable = null;
			//noinspection ConstantConditions
			InventoryTools.generateLootFor(world, player, getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), world.rand, lt, lootRolls);
			lootRolls = 0;
			BlockTools.notifyBlockUpdate(this);
		}
	}

	@Override
	protected boolean checkLootAndRead(NBTTagCompound compound) {
		if (super.checkLootAndRead(compound)) {
			setLootRolls(compound.getByte(LOOT_ROLLS_TAG));
			return true;
		}
		return false;
	}

	@Override
	protected boolean checkLootAndWrite(NBTTagCompound compound) {
		if (super.checkLootAndWrite(compound)) {
			compound.setByte(LOOT_ROLLS_TAG, (byte) lootRolls);
			return true;
		}
		return false;
	}

	public void setLootRolls(int lootRolls) {
		this.lootRolls = lootRolls;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		if (lootTable != null) {
			tag.setByte(LOOT_ROLLS_TAG, (byte) lootRolls);
			tag.setString(LOOT_TABLE_TAG, lootTable.toString());
		}
		return new SPacketUpdateTileEntity(pos, 0, tag);
	}

	@SuppressWarnings("squid:S4449")
	@Override
	@SideOnly(Side.CLIENT)
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound tag = pkt.getNbtCompound();
		if (tag.hasKey(LOOT_TABLE_TAG)) {
			setLootTable(new ResourceLocation(tag.getString(LOOT_TABLE_TAG)), 0);
			setLootRolls(tag.getByte(LOOT_ROLLS_TAG));
		} else {
			//noinspection ConstantConditions
			setLootTable(null, 0);
			setLootRolls(0);
		}
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		if (lootTable != null) {
			tag.setByte(LOOT_ROLLS_TAG, (byte) lootRolls);
			tag.setString(LOOT_TABLE_TAG, lootTable.toString());
		}
		return tag;
	}

	@Override
	@SuppressWarnings("squid:S4449")
	public void handleUpdateTag(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey(LOOT_TABLE_TAG)) {
			setLootTable(new ResourceLocation(tag.getString(LOOT_TABLE_TAG)), 0);
			setLootRolls(tag.getByte(LOOT_ROLLS_TAG));
		} else {
			//noinspection ConstantConditions
			setLootTable(null, 0);
			setLootRolls(0);
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
	public String getName() {
		return lootTable != null ? lootRolls + " x " + lootTable : "";
	}
}
