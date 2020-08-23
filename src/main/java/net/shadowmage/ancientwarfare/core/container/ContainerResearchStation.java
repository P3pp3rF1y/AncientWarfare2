package net.shadowmage.ancientwarfare.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.SlotItemHandler;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import net.shadowmage.ancientwarfare.core.tile.TileResearchStation;
import net.shadowmage.ancientwarfare.core.util.StreamUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ContainerResearchStation extends ContainerTileBase<TileResearchStation> {

	public boolean useAdjacentInventory;
	public String researcherName;
	public String currentGoal = "";
	public int progress = 0;
	public List<String> queuedResearch = new ArrayList<>();

	public ContainerResearchStation(EntityPlayer player, int x, int y, int z) {
		super(player, x, y, z);
		researcherName = tileEntity.getCrafterName();
		useAdjacentInventory = tileEntity.useAdjacentInventory;
		if (!player.world.isRemote) {
			if (researcherName != null) {
				currentGoal = ResearchTracker.INSTANCE.getCurrentGoal(player.world, researcherName).orElse("");
				progress = ResearchTracker.INSTANCE.getProgress(player.world, researcherName);
				queuedResearch.addAll(ResearchTracker.INSTANCE.getResearchQueueFor(player.world, researcherName));
			}
		}

		Slot slot = new SlotItemHandler(tileEntity.bookInventory, 0, 8, 18 + 4) {
			@Override
			public boolean isItemValid(ItemStack par1ItemStack) {
				return ItemResearchBook.getResearcherName(par1ItemStack) != null;
			}
		};
		addSlotToContainer(slot);

		int yBase = 8 + 3 * 18 + 10 + 12 + 4 + 10;
		int slotNum = 0;
		for (int y1 = 0; y1 < 3; y1++) {
			int y2 = y1 * 18 + yBase;
			for (int x1 = 0; x1 < 3; x1++) {
				int x2 = x1 * 18 + 8 + 18;
				slot = new SlotItemHandler(tileEntity.resourceInventory, slotNum, x2, y2);
				addSlotToContainer(slot);
				slotNum++;
			}
		}

		this.addPlayerSlots();
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
		ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot theSlot = this.getSlot(slotClickedIndex);
		if (theSlot != null && theSlot.getHasStack()) {
			ItemStack slotStack = theSlot.getStack();
			slotStackCopy = slotStack.copy();

			int playerSlotStart = tileEntity.bookInventory.getSlots() + tileEntity.resourceInventory.getSlots();
			int playerSlotEnd = playerSlotStart + playerSlots;
			if (slotClickedIndex < playerSlotStart)//book , storage slot
			{
				if (!this.mergeItemStack(slotStack, playerSlotStart, playerSlotEnd, false))//merge into player inventory
				{
					return ItemStack.EMPTY;
				}
			} else if (slotClickedIndex < playerSlotEnd)//player slots, merge into storage
			{
				if (!this.mergeItemStack(slotStack, 1, playerSlotStart, false))//merge into storage
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
		if (researcherName != null) {
			tag.setString("researcherName", researcherName);
		} else {
			tag.setBoolean("clearResearcher", true);
		}
		tag.setString("currentGoal", currentGoal);

		tag.setInteger("progress", progress);
		if (!queuedResearch.isEmpty()) {
			tag.setTag("queuedResearch", queuedResearch.stream().map(NBTTagString::new).collect(StreamUtils.toNBTTagList));
		}
		tag.setBoolean("useAdjacentInventory", useAdjacentInventory);
		tag.setInteger("inventoryDirection", tileEntity.inventoryDirection.ordinal());
		tag.setInteger("inventorySide", tileEntity.inventorySide.ordinal());
		this.sendDataToClient(tag);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey("researcherName")) {
			researcherName = tag.getString("researcherName");
		} else if (tag.hasKey("clearResearcher")) {
			researcherName = null;
		}
		if (tag.hasKey("currentGoal")) {
			currentGoal = tag.getString("currentGoal");
		}
		if (tag.hasKey("progress")) {
			progress = tag.getInteger("progress");
		}
		if (tag.hasKey("queuedResearch")) {
			queuedResearch.clear();
			tag.getTagList("queuedResearch", Constants.NBT.TAG_STRING).forEach(t -> queuedResearch.add(((NBTTagString) t).getString()));
		}
		if (this.researcherName == null) {
			this.queuedResearch.clear();
			this.progress = 0;
			this.currentGoal = "";
		}
		if (tag.hasKey("useAdjacentInventory")) {
			this.useAdjacentInventory = tag.getBoolean("useAdjacentInventory");
			if (!player.world.isRemote) {
				tileEntity.useAdjacentInventory = useAdjacentInventory;
			}
		}
		if (tag.hasKey("inventoryDirection")) {
			tileEntity.inventoryDirection = EnumFacing.VALUES[tag.getInteger("inventoryDirection")];
		}
		if (tag.hasKey("inventorySide")) {
			tileEntity.inventorySide = EnumFacing.VALUES[tag.getInteger("inventorySide")];
		}
		if (!player.world.isRemote) {
			tileEntity.markDirty();
		}
		this.refreshGui();
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (player.world.isRemote) {
			return;
		}
		tileEntity.addTorque(null, AWCoreStatics.researchPerTick);//do research whenever the GUI is open
		NBTTagCompound tag = null;
		String name = tileEntity.getCrafterName();
		/*
		 * synch researcher name
		 */
		if (name == null && researcherName == null) {

		} else if (name == null) {
			tag = new NBTTagCompound();
			researcherName = null;
			tag.setBoolean("clearResearcher", true);
			tag.setInteger("currentGoal", -1);
			tag.setInteger("progress", 0);
		} else if (researcherName == null || !name.equals(researcherName)) {
			tag = new NBTTagCompound();
			researcherName = name;
			tag.setString("researcherName", name);
			currentGoal = ResearchTracker.INSTANCE.getCurrentGoal(player.world, researcherName).orElse("");
			tag.setString("currentGoal", currentGoal);
			progress = ResearchTracker.INSTANCE.getProgress(player.world, researcherName);
			tag.setInteger("progress", progress);
		} else {
			String g = ResearchTracker.INSTANCE.getCurrentGoal(player.world, researcherName).orElse("");
			if (!g.equals(currentGoal)) {
				tag = new NBTTagCompound();
				currentGoal = g;
				tag.setString("currentGoal", currentGoal);
			}
			int p = ResearchTracker.INSTANCE.getProgress(player.world, researcherName);
			if (p != progress) {
				if (tag == null) {
					tag = new NBTTagCompound();
				}
				progress = p;
				tag.setInteger("progress", progress);
			}
		}

		/*
		 * synch queued research
		 */
		if (researcherName != null) {
			List<String> queue = ResearchTracker.INSTANCE.getResearchQueueFor(player.world, researcherName);
			if (!queue.equals(queuedResearch)) {
				if (tag == null) {
					tag = new NBTTagCompound();
				}
				queuedResearch.clear();
				queuedResearch.addAll(queue);
				tag.setTag("queuedResearch", queuedResearch.stream().map(NBTTagString::new).collect(StreamUtils.toNBTTagList));
			}
		} else {
			queuedResearch.clear();
		}

		/*
		 * synch use-adjacent inventory status
		 */
		if (tileEntity.useAdjacentInventory != useAdjacentInventory) {
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			this.useAdjacentInventory = tileEntity.useAdjacentInventory;
			tag.setBoolean("useAdjacentInventory", useAdjacentInventory);
		}

		if (tag != null) {
			this.sendDataToClient(tag);
		}

	}

	/*
	 * should be called from client-side to send update packet to server
	 */
	public void toggleUseAdjacentInventory() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("useAdjacentInventory", !useAdjacentInventory);
		useAdjacentInventory = !useAdjacentInventory;
		sendDataToServer(tag);
	}

	public void onSidePressed() {
		int o = (tileEntity.inventorySide.ordinal() + 1) % EnumFacing.VALUES.length;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("inventorySide", o);
		sendDataToServer(tag);
		tileEntity.inventorySide = EnumFacing.VALUES[o];
	}

	public void onDirPressed() {
		int o = (tileEntity.inventoryDirection.ordinal() + 1) % EnumFacing.VALUES.length;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("inventoryDirection", o);
		sendDataToServer(tag);
		tileEntity.inventoryDirection = EnumFacing.VALUES[o];
	}
}
