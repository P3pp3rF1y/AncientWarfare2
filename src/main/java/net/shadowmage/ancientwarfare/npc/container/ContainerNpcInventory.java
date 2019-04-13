package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.SlotItemHandler;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.inventory.NpcEquipmentHandler;

public class ContainerNpcInventory extends ContainerNpcBase<NpcBase> {

	public final int guiHeight;
	private String name;

	public ContainerNpcInventory(EntityPlayer player, int x, int y, int z) {
		super(player, x);
		NpcEquipmentHandler inventory = new NpcEquipmentHandler(entity);
		addSlotToContainer(new SlotItemHandler(inventory, 0, 8, 8)); //weapon slot
		addSlotToContainer(new SlotItemHandler(inventory, 2, 8, 8 + 18 * 5));//boots
		addSlotToContainer(new SlotItemHandler(inventory, 3, 8, 8 + 18 * 4));//legs
		addSlotToContainer(new SlotItemHandler(inventory, 4, 8, 8 + 18 * 3));//chest
		addSlotToContainer(new SlotItemHandler(inventory, 5, 8, 8 + 18 * 2));//helm
		addSlotToContainer(new SlotItemHandler(inventory, 6, 8 + 18 * 2, 8 + 18 * 3));//work/combat/route orders slot
		addSlotToContainer(new SlotItemHandler(inventory, 7, 8 + 18 * 2, 8 + 18 * 2));//upkeep orders slot
		addSlotToContainer(new SlotItemHandler(inventory, 1, 8, 8 + 18 * 1));//shield slot

		guiHeight = addPlayerSlots(8 + 5 * 18 + 8 + 18) + 8;
		name = entity.getCustomNameTag();
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey("customName")) {
			this.name = tag.getString("customName");
		}
		if (entity != null && !entity.isDead) {
			if (tag.hasKey("repack")) {
				entity.repackEntity(player);
			} else if (tag.hasKey("setHome")) {
				entity.setHomeAreaAtCurrentPosition();
			} else if (tag.hasKey("clearHome")) {
				entity.detachHome();
			} else if (tag.hasKey("togglefollow")) {
				if (entity.getFollowingEntity() != null && entity.getFollowingEntity().getName().equals(player.getName()))
					entity.clearFollowingEntity();
				else
					entity.setFollowingEntity(player);
			}
			if (tag.hasKey("customTexture")) {
				entity.setCustomTexRef(tag.getString("customTexture"));
			}
		}
	}

	public void handleNpcNameUpdate(String newName) {
		name = newName;
		if (name == null) {
			name = "";
		}
	}

	public void handleNpcTextureUpdate(String tex) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("customTexture", tex);
		sendDataToServer(tag);
	}

	@Override
	public void onContainerClosed(EntityPlayer p_75134_1_) {
		super.onContainerClosed(p_75134_1_);
		entity.setCustomNameTag(name);
		entity.updateTexture();
	}

	public void setName() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("customName", name);
		sendDataToServer(tag);
	}
}
