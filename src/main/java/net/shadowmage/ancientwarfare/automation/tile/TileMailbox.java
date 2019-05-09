package net.shadowmage.ancientwarfare.automation.tile;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.automation.gamedata.MailboxData;
import net.shadowmage.ancientwarfare.automation.gamedata.MailboxData.DeliverableItem;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;
import net.shadowmage.ancientwarfare.core.tile.IBlockBreakHandler;
import net.shadowmage.ancientwarfare.core.tile.TileOwned;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TileMailbox extends TileOwned implements IRotatableTile, ITickable, IBlockBreakHandler {

	private boolean autoExport;//TODO : should automatically try and export from output side
	private boolean privateBox;

	public ItemStackHandler sendInventory = new ItemStackHandler(18);
	public List<EnumFacing> sendSides = Lists.newArrayList();
	public ItemStackHandler receivedInventory = new ItemStackHandler(18);
	public List<EnumFacing> receivedSides = Lists.newArrayList();

	private String mailboxName;
	private String destinationName;

	public TileMailbox() {
		sendSides.add(EnumFacing.UP);
		receivedSides.add(EnumFacing.DOWN);
	}

	@Override
	public void update() {
		if (!hasWorld() || world.isRemote) {
			return;
		}
		if (mailboxName != null)//try to receive mail
		{
			MailboxData data = AWGameData.INSTANCE.getData(world, MailboxData.class);

			List<DeliverableItem> items = new ArrayList<>();
			data.getDeliverableItems(privateBox ? getOwner().getName() : null, mailboxName, items, world, pos.getX(), pos.getY(), pos.getZ());
			data.addMailboxReceiver(privateBox ? getOwner().getName() : null, mailboxName, this);

			if (destinationName != null)//try to send mail
			{
				trySendItems(data);
			}
		}
	}

	private void trySendItems(MailboxData data) {
		@Nonnull ItemStack item;
		String owner = privateBox ? getOwner().getName() : null;
		int dim = world.provider.getDimension();
		for (int slot = 0; slot < sendInventory.getSlots(); slot++) {
			item = sendInventory.getStackInSlot(slot);
			if (!item.isEmpty()) {
				data.addDeliverableItem(owner, destinationName, sendInventory.extractItem(slot, item.getCount(), false), dim, pos);
				break;
			}
		}
	}

	public String getMailboxName() {
		return mailboxName;
	}

	public String getTargetName() {
		return destinationName;
	}

	public void setMailboxName(String name) {
		if (world.isRemote) {
			return;
		}
		mailboxName = name;
		markDirty();
	}

	public void setTargetName(String name) {
		if (world.isRemote) {
			return;
		}
		destinationName = name;
		markDirty();
	}

	public boolean isAutoExport() {
		return autoExport;
	}

	public boolean isPrivateBox() {
		return privateBox;
	}

	public void setAutoExport(boolean val) {
		autoExport = val;
	}

	public void setPrivateBox(boolean val) {
		if (world.isRemote) {
			return;
		}
		if (val != privateBox) {
			mailboxName = null;
			destinationName = null;
			markDirty();
		}
		privateBox = val;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("targetName")) {
			destinationName = tag.getString("targetName");
		}
		if (tag.hasKey("mailboxName")) {
			mailboxName = tag.getString("mailboxName");
		}
		if (tag.hasKey("sendInventory")) {
			sendInventory.deserializeNBT(tag.getCompoundTag("sendInventory"));
		}
		if (tag.hasKey("receivedInventory")) {
			receivedInventory.deserializeNBT(tag.getCompoundTag("receivedInventory"));
		}
		if (tag.hasKey("sendSides")) {
			int[] sides = tag.getIntArray("sendSides");
			sendSides = Arrays.stream(sides).mapToObj(o -> EnumFacing.VALUES[o]).collect(Collectors.toList());
		}
		if (tag.hasKey("receivedSides")) {
			int[] sides = tag.getIntArray("receivedSides");
			receivedSides = Arrays.stream(sides).mapToObj(o -> EnumFacing.VALUES[o]).collect(Collectors.toList());
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (destinationName != null) {
			tag.setString("targetName", destinationName);
		}
		if (mailboxName != null) {
			tag.setString("mailboxName", mailboxName);
		}
		tag.setTag("sendInventory", sendInventory.serializeNBT());
		tag.setTag("receivedInventory", receivedInventory.serializeNBT());
		tag.setIntArray("sendSides", sendSides.stream().mapToInt(Enum::ordinal).toArray());
		tag.setIntArray("receivedSides", receivedSides.stream().mapToInt(Enum::ordinal).toArray());

		return tag;
	}

	@Override
	public EnumFacing getPrimaryFacing() {
		return world.getBlockState(pos).getValue(CoreProperties.FACING);
	}

	@Override
	public void setPrimaryFacing(EnumFacing face) {
		world.setBlockState(pos, world.getBlockState(pos).withProperty(CoreProperties.FACING, face), 0);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (sendSides.contains(facing) || receivedSides.contains(facing))) || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (receivedSides.contains(facing)) {
				return (T) receivedInventory;
			} else if (sendSides.contains(facing)) {
				return (T) sendInventory;
			}
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public void onBlockBroken(IBlockState state) {
		InventoryTools.dropItemsInWorld(world, sendInventory, pos);
		InventoryTools.dropItemsInWorld(world, receivedInventory, pos);
	}
}
