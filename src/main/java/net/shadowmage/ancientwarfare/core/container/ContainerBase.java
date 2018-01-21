package net.shadowmage.ancientwarfare.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IContainerGuiCallback;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketGui;

public class ContainerBase extends Container {

	public final EntityPlayer player;
	private IContainerGuiCallback gui;
	public int playerSlots = 0;

	public ContainerBase(EntityPlayer player) {
		this.player = player;
	}

	/*
	 * set the gui for this container for callback (refreshGui) purposes
	 */
	public final void setGui(IContainerGuiCallback gui) {
		this.gui = gui;
	}

	/*
	 * @param tx  the upper-left X coordinate of the 9x3 inventory block
	 * @param ty  the upper-left Y coordinate of the 9x3 inventory block
	 * @param gap the gap size between upper (9x3) and lower(9x1) inventory blocks, in pixels
	 */
	protected int addPlayerSlots(int tx, int ty, int gap) {
		int y;
		int x;
		int slotNum;
		int xPos;
		int yPos;
		IItemHandler playerInventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		for (x = 0; x < 9; ++x)//add player hotbar slots
		{
			xPos = tx + x * 18;
			yPos = ty + gap + 3 * 18;
			this.addSlotToContainer(new SlotItemHandler(playerInventory, x, xPos, yPos));
		}
		for (y = 0; y < 3; ++y) {
			for (x = 0; x < 9; ++x) {
				slotNum = y * 9 + x + 9;// +9 is to increment past hotbar slots
				xPos = tx + x * 18;
				yPos = ty + y * 18;
				this.addSlotToContainer(new SlotItemHandler(playerInventory, slotNum, xPos, yPos));
			}
		}
		playerSlots = 36;
		return ty + (4 * 18) + gap;
	}

	protected int addPlayerSlots(int ty) {
		return addPlayerSlots(8, ty, 4);
	}

	protected int addPlayerSlots() {
		return addPlayerSlots(8, 240 - 4 - 8 - 4 * 18, 4);
	}

	/*
	 * server side method to send a data-packet to the client-side GUI attached to the client-side version of this container
	 */
	protected final void sendDataToGui(NBTTagCompound data) {
		if (!player.world.isRemote) {
			PacketGui pkt = new PacketGui();
			pkt.setTag("gui", data);
			NetworkHandler.sendToPlayer((EntityPlayerMP) player, pkt);
		}
	}

	/*
	 * send data from client-container to server container
	 */
	protected void sendDataToServer(NBTTagCompound data) {
		if (player.world.isRemote) {
			PacketGui pkt = new PacketGui();
			pkt.setData(data);
			NetworkHandler.sendToServer(pkt);
		}
	}

	/*
	 * send data from server container to client container
	 */
	protected void sendDataToClient(NBTTagCompound data) {
		if (!player.world.isRemote) {
			PacketGui pkt = new PacketGui();
			pkt.setData(data);
			NetworkHandler.sendToPlayer((EntityPlayerMP) player, pkt);
		}
	}

	/*
	 * client/server side method to receive packet data from PacketGui
	 */
	public final void onPacketData(NBTTagCompound data) {
		if (data.hasKey("gui")) {
			if (this.gui != null) {
				this.gui.handlePacketData(data.getCompoundTag("gui"));
			}
		} else {
			handlePacketData(data);
		}
	}

	/*
	 * subclasses should override this method to send any data from server to the client-side container.
	 * This method is called immediately after the container has been constructed and set as the active container.
	 * The data is received client-side immediately after the GUI has been constructed, initialized, and opened.
	 */
	public void sendInitData() {

	}

	/*
	 * sub-classes should override this method to handle any packet data they are expecting to receive.
	 * packets destined to the GUI or for slot-click have already been filtered out
	 */
	public void handlePacketData(NBTTagCompound tag) {

	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return true;
	}

	/*
	 * Causes the GUI to be re-setup on its next update tick
	 */
	public void refreshGui() {
		if (this.gui != null) {
			this.gui.refreshGui();
		}
	}

    /*
	 * remove the inventory slots from view on the screen, effectively disabling them
     */

	public void removeSlots() {
		for (Slot s : this.inventorySlots) {
			if (s.yPos >= 0) {
				s.yPos -= 10000;
			}
		}
	}

    /*
	 * add any removed from screen slots back into view
     */

	public void addSlots() {
		for (Slot s : this.inventorySlots) {
			if (s.yPos < 0) {
				s.yPos += 10000;
			}
		}
	}

	/*
	 * override default CRASH with default DO NOTHING
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotClickedIndex) {
		return ItemStack.EMPTY;
	}
}
