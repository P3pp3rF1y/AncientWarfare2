package net.shadowmage.ancientwarfare.automation.gamedata;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.shadowmage.ancientwarfare.automation.tile.TileMailbox;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.Trig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics.mailboxTimeForDimension;
import static net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics.mailboxTimePerBlock;

public class MailboxData extends WorldSavedData {
	private MailboxSet publicMailboxes = new MailboxSet("public");
	private HashMap<String, MailboxSet> privateMailboxes = new HashMap<>();

	public MailboxData(String par1Str) {
		super(par1Str);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void serverTick(TickEvent.ServerTickEvent evt) {
		if (evt.phase == TickEvent.Phase.END) {
			onTick(1);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		publicMailboxes = new MailboxSet("public");
		publicMailboxes.readFromNBT(tag.getCompoundTag("publicBoxes"));

		NBTTagList privateBoxList = tag.getTagList("privateBoxes", Constants.NBT.TAG_COMPOUND);
		privateMailboxes.clear();
		MailboxSet boxSet;
		for (int i = 0; i < privateBoxList.tagCount(); i++) {
			boxSet = new MailboxSet();
			boxSet.readFromNBT(privateBoxList.getCompoundTagAt(i));
			privateMailboxes.put(boxSet.owningPlayerName, boxSet);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setTag("publicBoxes", publicMailboxes.writeToNBT(new NBTTagCompound()));

		NBTTagList privateBoxList = new NBTTagList();
		for (MailboxSet set : this.privateMailboxes.values()) {
			privateBoxList.appendTag(set.writeToNBT(new NBTTagCompound()));
		}
		tag.setTag("privateBoxes", privateBoxList);
		return tag;
	}

	public void onTick(int length) {
		synchronized (publicMailboxes) {
			boolean change = publicMailboxes.tick(length);
			if (change) {
				markDirty();
			}
		}
		synchronized (privateMailboxes) {
			boolean change = false;
			for (MailboxSet set : this.privateMailboxes.values()) {
				change |= set.tick(length);
			}
			if (change) {
				markDirty();
			}
		}
	}

	public boolean addMailbox(@Nullable String owner, String name) {
		MailboxSet set = owner == null ? publicMailboxes : getOrCreatePrivateMailbox(owner);
		markDirty();
		return set.addMailbox(name);
	}

	public boolean deleteMailbox(@Nullable String owner, String name) {
		MailboxSet set = owner == null ? publicMailboxes : getOrCreatePrivateMailbox(owner);
		markDirty();
		return set.deleteMailbox(name);
	}

	public void addDeliverableItem(@Nullable String owner, String name, ItemStack item, int dim, BlockPos pos) {
		MailboxSet set = owner == null ? publicMailboxes : getOrCreatePrivateMailbox(owner);
		MailboxEntry entry = set.getOrCreateMailbox(name);
		entry.addDeliverableItem(item, dim, pos);
		markDirty();
	}

	private MailboxSet getOrCreatePrivateMailbox(@Nullable String owner) {
		if (owner == null) {
			owner = "";
		}
		if (!privateMailboxes.containsKey(owner)) {
			privateMailboxes.put(owner, new MailboxSet(owner));
			markDirty();
		}
		return privateMailboxes.get(owner);
	}

	public List<String> getPublicBoxNames() {
		ArrayList<String> names = new ArrayList<>();
		MailboxSet set = publicMailboxes;
		names.addAll(set.mailboxes.keySet());
		return names;
	}

	public List<String> getPrivateBoxNames(String owner) {
		ArrayList<String> names = new ArrayList<>();
		if (privateMailboxes.containsKey(owner)) {
			names.addAll(privateMailboxes.get(owner).mailboxes.keySet());
		}
		return names;
	}

	public List<DeliverableItem> getDeliverableItems(@Nullable String owner, String name, List<DeliverableItem> items, World world, int x, int y, int z) {
		MailboxSet set = owner == null ? publicMailboxes : getOrCreatePrivateMailbox(owner);
		return set.getDeliverableItems(name, items, world, x, y, z);
	}

	public void removeDeliverableItem(@Nullable String owner, String name, DeliverableItem item) {
		MailboxSet set = owner == null ? publicMailboxes : getOrCreatePrivateMailbox(owner);
		set.removeDeliverableItem(name, item);
		markDirty();
	}

	public void addMailboxReceiver(@Nullable String owner, String name, TileMailbox box) {
		MailboxSet set = owner == null ? publicMailboxes : getOrCreatePrivateMailbox(owner);
		set.addReceiver(name, box);
		markDirty();
	}

	private final class MailboxSet {

		private String owningPlayerName;
		private HashMap<String, MailboxEntry> mailboxes = new HashMap<>();

		private MailboxSet(String name) {
			this.owningPlayerName = name;
		}

		private MailboxSet() {
		}//nbt constructor

		private boolean addMailbox(String name) {
			if (mailboxes.containsKey(name)) {
				return false;
			}
			mailboxes.put(name, new MailboxEntry(name));
			return true;
		}

		private boolean deleteMailbox(String name) {
			if (!mailboxes.containsKey(name)) {
				return false;
			}
			if (!mailboxes.get(name).incomingItems.isEmpty()) {
				return false;
			}
			mailboxes.remove(name);
			return true;
		}

		private void readFromNBT(NBTTagCompound tag) {
			NBTTagList mailboxList = tag.getTagList("mailboxList", Constants.NBT.TAG_COMPOUND);
			NBTTagCompound mailboxTag;
			mailboxes.clear();
			MailboxEntry entry;
			for (int i = 0; i < mailboxList.tagCount(); i++) {
				mailboxTag = mailboxList.getCompoundTagAt(i);
				entry = new MailboxEntry();
				entry.readFromNBT(mailboxTag);
				mailboxes.put(entry.mailboxName, entry);
			}
			owningPlayerName = tag.getString("ownerName");
		}

		private NBTTagCompound writeToNBT(NBTTagCompound tag) {
			NBTTagList mailboxList = new NBTTagList();
			NBTTagCompound mailboxTag;
			for (MailboxEntry entry : this.mailboxes.values()) {
				mailboxTag = entry.writeToNBT(new NBTTagCompound());
				mailboxList.appendTag(mailboxTag);
			}
			tag.setTag("mailboxList", mailboxList);
			tag.setString("ownerName", owningPlayerName);
			return tag;
		}

		private boolean tick(int length) {
			boolean ret = false;
			for (MailboxEntry entry : this.mailboxes.values()) {
				ret |= entry.tick(length);
			}
			return ret;
		}

		private MailboxEntry getOrCreateMailbox(String name) {
			if (!this.mailboxes.containsKey(name)) {
				this.mailboxes.put(name, new MailboxEntry(name));
			}
			return this.mailboxes.get(name);
		}

		private List<DeliverableItem> getDeliverableItems(String name, List<DeliverableItem> items, World world, int x, int y, int z) {
			if (this.mailboxes.containsKey(name)) {
				return this.mailboxes.get(name).getDeliverableItems(items, world, x, y, z);
			}
			return Collections.emptyList();
		}

		private void removeDeliverableItem(String name, DeliverableItem item) {
			if (this.mailboxes.containsKey(name)) {
				this.mailboxes.get(name).removeDeliverableItem(item);
			}
		}

		private void addReceiver(String name, TileMailbox box) {
			if (this.mailboxes.containsKey(name)) {
				this.mailboxes.get(name).addReceiver(box);
			}
		}
	}

	public final class MailboxEntry {
		private String mailboxName;
		private List<DeliverableItem> incomingItems = new ArrayList<>();
		private List<TileMailbox> receivers = new ArrayList<>();

		private MailboxEntry(String name) {
			this.mailboxName = name;
		}

		private MailboxEntry() {
		}//nbt-constructor

		private void addReceiver(TileMailbox tile) {
			this.receivers.add(tile);
		}

		private void removeDeliverableItem(DeliverableItem item) {
			incomingItems.remove(item);
		}

		private List<DeliverableItem> getDeliverableItems(List<DeliverableItem> items, World world, int x, int y, int z) {
			int dim = world.provider.getDimension();
			int time = 0;
			int timePerBlock = mailboxTimePerBlock;//set time from config for per-block time
			int timeForDimension = mailboxTimeForDimension;//set time from config for cross-dimensional items
			for (DeliverableItem item : this.incomingItems) {
				if (dim != item.originDimension) {
					time = timeForDimension;
				} else {
					float dist = Trig.getDistance(item.x, item.y, item.z, x, y, z);
					time = (int) (dist * (float) timePerBlock);
				}
				if (item.deliveryTime >= time) {
					items.add(item);
				}
			}
			return items;
		}

		private void addDeliverableItem(ItemStack item, int dimension, BlockPos pos) {
			DeliverableItem item1 = new DeliverableItem(item, dimension, pos.getX(), pos.getY(), pos.getZ());
			incomingItems.add(item1);
		}

		private void readFromNBT(NBTTagCompound tag) {
			mailboxName = tag.getString("name");
			NBTTagList itemList = tag.getTagList("itemList", Constants.NBT.TAG_COMPOUND);
			NBTTagCompound itemTag;
			DeliverableItem item;
			for (int i = 0; i < itemList.tagCount(); i++) {
				itemTag = itemList.getCompoundTagAt(i);
				item = new DeliverableItem();
				item.readFromNBT(itemTag);
				incomingItems.add(item);
			}
		}

		private NBTTagCompound writeToNBT(NBTTagCompound tag) {
			tag.setString("name", mailboxName);
			NBTTagList itemList = new NBTTagList();
			NBTTagCompound itemTag;
			for (DeliverableItem item : this.incomingItems) {
				itemTag = item.writeToNBT(new NBTTagCompound());
				itemList.appendTag(itemTag);
			}
			tag.setTag("itemList", itemList);
			return tag;
		}

		private boolean tick(int length) {
			boolean ret = incomingItems.size() > 0;
			for (DeliverableItem item : this.incomingItems) {
				item.tick(length);
			}

			int dim;
			int time = 0;
			int timePerBlock = mailboxTimePerBlock;//set time from config for per-block time
			int timeForDimension = mailboxTimeForDimension;//set time from config for cross-dimensional items
			int x, y, z;
			Iterator<DeliverableItem> it;
			DeliverableItem item;
			@Nonnull ItemStack stack;
			for (TileMailbox box : receivers) {
				dim = box.getWorld().provider.getDimension();
				x = box.getPos().getX();
				y = box.getPos().getY();
				z = box.getPos().getZ();
				it = this.incomingItems.iterator();
				while (it.hasNext() && (item = it.next()) != null) {
					if (dim != item.originDimension) {
						time = timeForDimension;
					} else {
						float dist = Trig.getDistance(item.x, item.y, item.z, x, y, z);
						time = (int) (dist * (float) timePerBlock);
					}
					if (item.deliveryTime >= time)//find if item is deliverable to this box
					{
						stack = InventoryTools.mergeItemStack(box.receivedInventory, item.item);
						if (stack.isEmpty()) {
							it.remove();
						}
						break;
					}
				}
			}
			receivers.clear();

			return ret;
		}

		@Override
		public String toString() {
			return "MailboxEntry: " + mapName + " Items List: " + incomingItems;
		}
	}

	public final class DeliverableItem {
		int originDimension, x, y, z;
		@Nonnull
		public ItemStack item;
		int deliveryTime;//system milis at which this stack is deliverable

		private DeliverableItem(ItemStack item, int dim, int x, int y, int z) {
			this.item = item;
			this.originDimension = dim;
			this.x = x;
			this.y = y;
			this.z = z;
		}

		private DeliverableItem() {
		}

		private void readFromNBT(NBTTagCompound tag) {
			item = new ItemStack(tag.getCompoundTag("item"));
			deliveryTime = tag.getInteger("time");
			originDimension = tag.getInteger("dim");
			this.x = tag.getInteger("x");
			this.y = tag.getInteger("y");
			this.z = tag.getInteger("z");
		}

		private NBTTagCompound writeToNBT(NBTTagCompound tag) {
			tag.setTag("item", item.writeToNBT(new NBTTagCompound()));
			tag.setInteger("time", deliveryTime);
			tag.setInteger("dim", originDimension);
			tag.setInteger("x", x);
			tag.setInteger("y", y);
			tag.setInteger("z", z);
			return tag;
		}

		private void tick(int length) {
			deliveryTime += length;
		}
	}

}
