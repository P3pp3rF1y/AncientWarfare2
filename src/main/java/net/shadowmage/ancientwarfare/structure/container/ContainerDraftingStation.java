package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.SlotItemHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.tile.TileDraftingStation;

import java.util.List;
import java.util.Optional;

public class ContainerDraftingStation extends ContainerStructureSelectionBase {
	private static final String IS_STARTED_TAG = "isStarted";
	private static final String IS_FINISHED_TAG = "isFinished";
	private static final String REMAINING_TIME_TAG = "remainingTime";
	private static final String TOTAL_TIME_TAG = "totalTime";
	private static final String STRUCT_NAME_TAG = "structName";
	private static final String RESOURCE_LIST_TAG = "resourceList";
	public boolean isStarted = false;
	private boolean isFinished = false;
	private int remainingTime;
	private int totalTime;
	public final NonNullList<ItemStack> neededResources = NonNullList.create();

	private final TileDraftingStation tile;

	public ContainerDraftingStation(EntityPlayer player, int x, int y, int z) {
		super(player);
		Optional<TileDraftingStation> te = WorldTools.getTile(player.world, new BlockPos(x, y, z), TileDraftingStation.class);
		if (!te.isPresent()) {
			throw new IllegalArgumentException("No drafting station");
		}
		tile = te.get();
		structureName = tile.getCurrentTemplateName();
		neededResources.addAll(InventoryTools.copyStacks(tile.getNeededResources()));
		isStarted = tile.isStarted();
		isFinished = tile.isFinished();
		remainingTime = tile.getRemainingTime();
		totalTime = tile.getTotalTime();

		int y2 = 94;

		int xp;
		int yp;
		int slotNum;
		for (int y1 = 0; y1 < 3; y1++) {
			for (int x1 = 0; x1 < 9; x1++) {
				slotNum = y1 * 9 + x1;
				xp = 8 + x1 * 18;
				yp = y2 + y1 * 18;
				addSlotToContainer(new SlotItemHandler(tile.inputSlots, slotNum, xp, yp));
			}
		}

		addSlotToContainer(new SlotItemHandler(tile.outputSlot, 0, 8 + 4 * 18, 94 - 16 - 18) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return false;
			}
		});

		this.addPlayerSlots(156);
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return tile.getDistanceSq(var1.posX, var1.posY, var1.posZ) <= 64D;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot theSlot = this.getSlot(slotClickedIndex);
		if (theSlot != null && theSlot.getHasStack()) {
			ItemStack slotStack = theSlot.getStack();
			slotStackCopy = slotStack.copy();

			int playerSlotStart = tile.inputSlots.getSlots();
			int playerSlotEnd = playerSlotStart + playerSlots;
			if (slotClickedIndex < playerSlotStart)//storage slots
			{
				if (!this.mergeItemStack(slotStack, playerSlotStart, playerSlotEnd, false))//merge into player inventory
				{
					return ItemStack.EMPTY;
				}
			} else if (slotClickedIndex < playerSlotEnd)//player slots, merge into storage
			{
				if (!this.mergeItemStack(slotStack, 0, playerSlotStart, false))//merge into storage
				{
					return ItemStack.EMPTY;
				}
			}
			if (slotStack.getCount() == 0) {
				theSlot.putStack(ItemStack.EMPTY);
			} else {
				theSlot.onSlotChanged();
			}
			if (slotStack.getCount() == slotStackCopy.getCount()) {
				return ItemStack.EMPTY;
			}
			theSlot.onTake(par1EntityPlayer, slotStack);
		}
		return slotStackCopy;
	}

	@Override
	public void sendInitData() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean(IS_STARTED_TAG, isStarted);
		tag.setBoolean(IS_FINISHED_TAG, isFinished);
		tag.setInteger(REMAINING_TIME_TAG, remainingTime);
		tag.setInteger(TOTAL_TIME_TAG, totalTime);
		if (structureName != null) {
			tag.setString(STRUCT_NAME_TAG, structureName);
		}
		tag.setTag(RESOURCE_LIST_TAG, getResourceListTag(neededResources));
		this.sendDataToClient(tag);
	}

	private NBTTagList getResourceListTag(NonNullList<ItemStack> resources) {
		NBTTagList list = new NBTTagList();
		NBTTagCompound tag;
		for (ItemStack item : resources) {
			tag = new NBTTagCompound();
			item.writeToNBT(tag);
			tag.setInteger("RealCount", item.getCount());
			list.appendTag(tag);
		}
		return list;
	}

	private void readResourceList(NBTTagList list, NonNullList<ItemStack> resources) {
		NBTTagCompound tag;
		ItemStack stack;
		for (int i = 0; i < list.tagCount(); i++) {
			tag = list.getCompoundTagAt(i);
			stack = new ItemStack(tag);
			stack.setCount(tag.getInteger("RealCount"));
			if (!stack.isEmpty()) {
				resources.add(stack);
			}
		}
	}

	public void handleStopInput() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("stop", true);
		this.sendDataToServer(tag);
	}

	public void handleStartInput() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("start", true);
		this.sendDataToServer(tag);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey(STRUCT_NAME_TAG)) {
			if (player.world.isRemote) {
				this.structureName = tag.getString(STRUCT_NAME_TAG);
			} else {
				tile.setTemplate(tag.getString(STRUCT_NAME_TAG));
				updateResources();
			}
		} else if (tag.hasKey("clearName")) {
			structureName = null;
		}
		if (tag.hasKey(IS_STARTED_TAG)) {
			isStarted = tag.getBoolean(IS_STARTED_TAG);
		}
		if (tag.hasKey(IS_FINISHED_TAG)) {
			isFinished = tag.getBoolean(IS_FINISHED_TAG);
		}
		if (tag.hasKey(REMAINING_TIME_TAG)) {
			remainingTime = tag.getInteger(REMAINING_TIME_TAG);
		}
		if (tag.hasKey(TOTAL_TIME_TAG)) {
			totalTime = tag.getInteger(TOTAL_TIME_TAG);
		}
		if (tag.hasKey(RESOURCE_LIST_TAG)) {
			neededResources.clear();
			readResourceList(tag.getTagList(RESOURCE_LIST_TAG, Constants.NBT.TAG_COMPOUND), neededResources);
		}
		if (tag.hasKey("stop")) {
			tile.stopCurrentWork();
		} else if (tag.hasKey("start")) {
			if (player.isCreative()) {
				tile.tryFinish();
				tile.stopCurrentWork();
			} else {
				tile.tryStart();
			}
		}
		refreshGui();
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		String tileName = tile.getCurrentTemplateName();
		NBTTagCompound tag = null;
		if ((structureName == null && tileName != null) || (tileName == null && structureName != null)) {
			tag = new NBTTagCompound();
			this.structureName = tileName;
			if (this.structureName == null) {
				tag.setBoolean("clearName", true);
			} else {
				tag.setString(STRUCT_NAME_TAG, structureName);
			}
		} else if (structureName != null && !structureName.equals(tileName)) {
			structureName = tileName;
			tag = new NBTTagCompound();
			tag.setString(STRUCT_NAME_TAG, structureName);
		}
		if (tile.isFinished() != isFinished) {
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			isFinished = tile.isFinished();
			tag.setBoolean(IS_FINISHED_TAG, isFinished);
		}
		if (tile.isStarted() != isStarted) {
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			isStarted = tile.isStarted();
			tag.setBoolean(IS_STARTED_TAG, isStarted);
		}
		if (tile.getRemainingTime() != remainingTime) {
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			remainingTime = tile.getRemainingTime();
			tag.setInteger(REMAINING_TIME_TAG, remainingTime);
		}
		if (tile.getTotalTime() != totalTime) {
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			totalTime = tile.getTotalTime();
			tag.setInteger(TOTAL_TIME_TAG, totalTime);
		}
		if (neededResourcesChanged()) {
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			neededResources.clear();
			neededResources.addAll(InventoryTools.copyStacks(tile.getNeededResources()));
			NBTTagList list = getResourceListTag(neededResources);
			tag.setTag(RESOURCE_LIST_TAG, list);
		}
		if (tag != null) {
			sendDataToClient(tag);
		}
	}

	private boolean neededResourcesChanged() {
		List<ItemStack> tileResources = tile.getNeededResources();
		if (neededResources.size() != tileResources.size()) {
			return true;
		}

		for (int i = 0; i < neededResources.size(); i++) {
			if (!ItemStack.areItemStacksEqual(neededResources.get(i), tileResources.get(i))) {
				return true;
			}
		}

		return false;
	}

	private void updateResources() {
		NBTTagCompound tag = new NBTTagCompound();
		neededResources.clear();
		neededResources.addAll(InventoryTools.copyStacks(tile.getNeededResources()));
		NBTTagList list = getResourceListTag(neededResources);
		tag.setTag(RESOURCE_LIST_TAG, list);
		sendDataToClient(tag);
	}
}
