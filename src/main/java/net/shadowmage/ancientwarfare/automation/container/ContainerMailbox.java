package net.shadowmage.ancientwarfare.automation.container;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.SlotItemHandler;
import net.shadowmage.ancientwarfare.automation.gamedata.MailboxData;
import net.shadowmage.ancientwarfare.automation.tile.TileMailbox;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.util.StringTools;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ContainerMailbox extends ContainerTileBase<TileMailbox> {

	public int guiHeight;
	/*
	 * synched stats
	 */
	public String targetName;
	public String mailboxName;
	public boolean autoExport;
	public boolean privateBox;
	public List<String> publicBoxNames = Lists.newArrayList();
	public List<String> privateBoxNames = Lists.newArrayList();
	public List<EnumFacing> sendSides = Lists.newArrayList();
	public List<EnumFacing> receivedSides = Lists.newArrayList();

	public ContainerMailbox(EntityPlayer player, int x, int y, int z) {
		super(player, x, y, z);

		int xPos, yPos, x1, y1;
		for(int i = 0; i < 18; i++) {
			x1 = i % 9;
			y1 = i / 9;
			xPos = x1 * 18 + 8;
			yPos = y1 * 18 + 8 + 12;
			addSlotToContainer(new SlotItemHandler(tileEntity.receivedInventory, i, xPos, yPos) {
				@Override
				public boolean isItemValid(@Nonnull ItemStack stack) {
					return false;
				}
			});
		}

		for(int i = 0; i < 18; i++) {
			x1 = i % 9;
			y1 = i / 9;
			xPos = x1 * 18 + 8;
			yPos = y1 * 18 + 8 + 12 + 2 * 18 + 12;
			addSlotToContainer(new SlotItemHandler(tileEntity.sendInventory, i, xPos, yPos));
		}

		y1 = 8 + 12 + 12 + 4 * 18;
		guiHeight = addPlayerSlots(y1 + 12) + 8 + 24;

		if(!player.world.isRemote) {
			MailboxData data = AWGameData.INSTANCE.getData(player.world, MailboxData.class);
			publicBoxNames.addAll(data.getPublicBoxNames());
			privateBoxNames.addAll(data.getPrivateBoxNames(tileEntity.getOwnerName()));
			privateBox = tileEntity.isPrivateBox();
			autoExport = tileEntity.isAutoExport();
			mailboxName = tileEntity.getMailboxName();
			targetName = tileEntity.getTargetName();
			sendSides = tileEntity.sendSides;
			receivedSides = tileEntity.receivedSides;
		}
	}

	@Override
	public void sendInitData() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setIntArray("sendSides", tileEntity.sendSides.stream().mapToInt(Enum::ordinal).toArray());
		tag.setIntArray("receivedSides", tileEntity.receivedSides.stream().mapToInt(Enum::ordinal).toArray());
		if(mailboxName != null) {
			tag.setString("mailboxName", mailboxName);
		}
		if(targetName != null) {
			tag.setString("targetName", targetName);
		}

		tag.setBoolean("privateBox", privateBox);
		tag.setBoolean("autoExport", autoExport);

		NBTTagList nameList = new NBTTagList();
		for(String boxName : publicBoxNames) {
			nameList.appendTag(new NBTTagString(boxName));
		}
		tag.setTag("publicBoxNames", nameList);

		nameList = new NBTTagList();
		for(String boxName : privateBoxNames) {
			nameList.appendTag(new NBTTagString(boxName));
		}
		tag.setTag("privateBoxNames", nameList);
		sendDataToClient(tag);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		handleAccessChange(tag);
		if(tag.hasKey("sendSides")) {
			int[] sides = tag.getIntArray("sendSides");
			tileEntity.sendSides = Arrays.stream(sides).mapToObj(o -> EnumFacing.VALUES[o]).collect(Collectors.toList());
			sendSides = tileEntity.sendSides;
		}
		if(tag.hasKey("receivedSides")) {
			int[] sides = tag.getIntArray("receivedSides");
			tileEntity.receivedSides = Arrays.stream(sides).mapToObj(o -> EnumFacing.VALUES[o]).collect(Collectors.toList());
			receivedSides = tileEntity.receivedSides;
		}
		if(tag.hasKey("autoExport")) {
			autoExport = tag.getBoolean("autoExport");
			tileEntity.setAutoExport(autoExport);
		}
		if(tag.hasKey("privateBox")) {
			privateBox = tag.getBoolean("privateBox");
			tileEntity.setPrivateBox(privateBox);
		}
		if(tag.hasKey("clearMailbox")) {
			mailboxName = null;
			tileEntity.setMailboxName(null);
		} else if(tag.hasKey("mailboxName")) {
			mailboxName = tag.getString("mailboxName");
			tileEntity.setMailboxName(mailboxName);
		}
		if(tag.hasKey("clearTarget")) {
			targetName = null;
			tileEntity.setTargetName(null);
		} else if(tag.hasKey("targetName")) {
			targetName = tag.getString("targetName");
			tileEntity.setTargetName(targetName);
		}
		if(tag.hasKey("publicBoxNames")) {
			publicBoxNames.clear();
			NBTTagList nameList = tag.getTagList("publicBoxNames", Constants.NBT.TAG_STRING);
			for(int i = 0; i < nameList.tagCount(); i++) {
				publicBoxNames.add(nameList.getStringTagAt(i));
			}
		}
		if(tag.hasKey("privateBoxNames")) {
			privateBoxNames.clear();
			NBTTagList nameList = tag.getTagList("privateBoxNames", Constants.NBT.TAG_STRING);
			for(int i = 0; i < nameList.tagCount(); i++) {
				privateBoxNames.add(nameList.getStringTagAt(i));
			}
		}
		if(tag.hasKey("addMailbox") || tag.hasKey("deleteMailbox")) {
			MailboxData data = AWGameData.INSTANCE.getData(player.world, MailboxData.class);
			String name = tag.getString("addMailbox");
			if(!name.isEmpty())
				data.addMailbox(tileEntity.isPrivateBox() ? tileEntity.getOwnerName() : null, name);
			name = tag.getString("deleteMailbox");
			if(!name.isEmpty())
				data.deleteMailbox(tileEntity.isPrivateBox() ? tileEntity.getOwnerName() : null, name);
		}
		refreshGui();
	}

	private void handleAccessChange(NBTTagCompound tag) {
		if(tag.hasKey("accessChange")) {
			NBTTagCompound slotTag = tag.getCompoundTag("accessChange");
			RelativeSide base = RelativeSide.values()[slotTag.getInteger("baseSide")];
			RelativeSide access = RelativeSide.values()[slotTag.getInteger("accessSide")];

			updateSides(base, access);
		}
	}

	public void updateSides(RelativeSide base, RelativeSide access) {
		EnumFacing facing = RelativeSide.getMCSideToAccess(BlockRotationHandler.RotationType.FOUR_WAY, tileEntity.getPrimaryFacing(), base);
		List<EnumFacing> removeFrom;
		List<EnumFacing> addTo;
		if(access == RelativeSide.TOP || access == RelativeSide.BOTTOM) {
			if(access == RelativeSide.TOP) {
				removeFrom = tileEntity.sendSides;
				addTo = tileEntity.receivedSides;
			} else {
				removeFrom = tileEntity.receivedSides;
				addTo = tileEntity.sendSides;
			}
			removeFrom.remove(facing);
			addTo.add(facing);

		} else {
			tileEntity.sendSides.remove(facing);
			tileEntity.receivedSides.remove(facing);
		}
		sendSides = tileEntity.sendSides;
		receivedSides = tileEntity.receivedSides;
	}

	public void sendSlotChange(RelativeSide base, RelativeSide access) {
		NBTTagCompound tag;
		NBTTagCompound slotTag;
		tag = new NBTTagCompound();
		slotTag = new NBTTagCompound();
		slotTag.setInteger("baseSide", base.ordinal());
		slotTag.setInteger("accessSide", access.ordinal());
		tag.setTag("accessChange", slotTag);
		sendDataToServer(tag);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		NBTTagCompound tag = null;
		/*
		 * DETECT CHANGES TO NAME AND TARGET AND SEND TO CLIENT
         */
		if(!sendSides.equals(tileEntity.sendSides)) {
			if(tag == null) {
				tag = new NBTTagCompound();
			}
			sendSides = tileEntity.sendSides;
			tag.setIntArray("sendSides", sendSides.stream().mapToInt(Enum::ordinal).toArray());
		}
		if(!receivedSides.equals(tileEntity.receivedSides)) {
			receivedSides = tileEntity.receivedSides;
			tag.setIntArray("receivedSides", receivedSides.stream().mapToInt(Enum::ordinal).toArray());
		}
		String name = tileEntity.getMailboxName();
		if(!StringTools.doStringsMatch(name, mailboxName)) {
			if(tag == null) {
				tag = new NBTTagCompound();
			}
			mailboxName = tileEntity.getMailboxName();
			if(mailboxName == null) {
				tag.setBoolean("clearMailbox", true);
			} else {
				tag.setString("mailboxName", mailboxName);
			}
		}
		name = tileEntity.getTargetName();
		if(!StringTools.doStringsMatch(name, targetName)) {
			if(tag == null) {
				tag = new NBTTagCompound();
			}
			targetName = tileEntity.getTargetName();
			if(targetName == null) {
				tag.setBoolean("clearTarget", true);
			} else {
				tag.setString("targetName", targetName);
			}
		}
		/*
         * detect changes to auto export and private box setting
         */
		if(autoExport != tileEntity.isAutoExport()) {
			if(tag == null) {
				tag = new NBTTagCompound();
			}
			autoExport = tileEntity.isAutoExport();
			tag.setBoolean("autoExport", autoExport);
		}
		if(privateBox != tileEntity.isPrivateBox()) {
			if(tag == null) {
				tag = new NBTTagCompound();
			}
			privateBox = tileEntity.isPrivateBox();
			tag.setBoolean("privateBox", privateBox);
		}
		/*
         * detect changes to public or private names list
         */
		MailboxData data = AWGameData.INSTANCE.getData(player.world, MailboxData.class);
		if(!publicBoxNames.equals(data.getPublicBoxNames())) {
			if(tag == null) {
				tag = new NBTTagCompound();
			}
			publicBoxNames.clear();
			publicBoxNames.addAll(data.getPublicBoxNames());
			NBTTagList nameList = new NBTTagList();
			for(String boxName : publicBoxNames) {
				nameList.appendTag(new NBTTagString(boxName));
			}
			tag.setTag("publicBoxNames", nameList);
		}
		if(!privateBoxNames.equals(data.getPrivateBoxNames(tileEntity.getOwnerName()))) {
			if(tag == null) {
				tag = new NBTTagCompound();
			}
			privateBoxNames.clear();
			privateBoxNames.addAll(data.getPrivateBoxNames(tileEntity.getOwnerName()));
			NBTTagList nameList = new NBTTagList();
			for(String boxName : privateBoxNames) {
				nameList.appendTag(new NBTTagString(boxName));
			}
			tag.setTag("privateBoxNames", nameList);
		}
		/*
         * if tag is not null (something has changed), send it to client
         */
		if(tag != null) {
			sendDataToClient(tag);
		}
	}

	/*
	 * client-side input method
	 */
	public void handleNameAdd(String name) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("addMailbox", name);
		sendDataToServer(tag);
	}

	/*
	 * client-side input method
	 */
	public void handleNameDelete(String name) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("deleteMailbox", name);
		sendDataToServer(tag);
	}

	/*
	 * client-side input method
	 */
	public void handleNameSelection(String name) {
		NBTTagCompound tag = new NBTTagCompound();
		if(name == null) {
			tag.setBoolean("clearMailbox", true);
		} else {
			tag.setString("mailboxName", name);
		}
		mailboxName = name;
		sendDataToServer(tag);
	}

	/*
	 * client-side input method
	 */
	public void handleTargetSelection(String name) {
		NBTTagCompound tag = new NBTTagCompound();
		if(name == null) {
			tag.setBoolean("clearTarget", true);
		} else {
			tag.setString("targetName", name);
		}
		targetName = name;
		sendDataToServer(tag);
	}

	/*
	 * client-side input method
	 */
	public void handlePrivateBoxToggle(boolean newVal) {
		targetName = null;
		mailboxName = null;
		privateBox = newVal;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("privateBox", privateBox);
		sendDataToServer(tag);
	}

	public void handleAutoExportToggle(boolean newVal) {
		autoExport = newVal;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("autoExport", newVal);
		sendDataToServer(tag);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
		@Nonnull ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot theSlot = this.getSlot(slotClickedIndex);
		if(theSlot != null && theSlot.getHasStack()) {
			@Nonnull ItemStack slotStack = theSlot.getStack();
			slotStackCopy = slotStack.copy();
			if(slotStack.getCount() == 0) {
				theSlot.putStack(ItemStack.EMPTY);
			} else {
				theSlot.onSlotChanged();
			}
			if(slotStack.getCount() == slotStackCopy.getCount()) {
				return ItemStack.EMPTY;
			}
			theSlot.onTake(par1EntityPlayer, slotStack);
		}
		return slotStackCopy;
	}

}
