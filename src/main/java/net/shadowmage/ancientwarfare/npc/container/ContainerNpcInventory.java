package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.shadowmage.ancientwarfare.core.inventory.SlotArmor;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.inventory.InventoryNpcEquipment;

public class ContainerNpcInventory extends ContainerNpcBase<NpcBase> {

    InventoryNpcEquipment inventory;
    public int guiHeight;
    public String name;

    ItemStack orderStack;

    public ContainerNpcInventory(final EntityPlayer player, int x, int y, int z) {
        super(player, x);
        inventory = new InventoryNpcEquipment(entity);
        addSlotToContainer(new Slot(inventory, 0, 8, 8) {
            @Override
            public void onSlotChanged() {
                if (!player.worldObj.isRemote) {
                    entity.onWeaponInventoryChanged();
                }
                super.onSlotChanged();
            }
        }); //weapon slot
        addSlotToContainer(new Slot(inventory, 7, 8, 8 + 18 * 1));//shield slot
        addSlotToContainer(new SlotArmor(inventory, 1, 8, 8 + 18 * 5, 3, entity));//boots
        addSlotToContainer(new SlotArmor(inventory, 2, 8, 8 + 18 * 4, 2, entity));//legs
        addSlotToContainer(new SlotArmor(inventory, 3, 8, 8 + 18 * 3, 1, entity));//chest
        addSlotToContainer(new SlotArmor(inventory, 4, 8, 8 + 18 * 2, 0, entity));//helm
        addSlotToContainer(new Slot(inventory, 6, 8 + 18 * 2, 8 + 18 * 2));//upkeep orders slot  TODO add slot validation
        addSlotToContainer(new Slot(inventory, 5, 8 + 18 * 2, 8 + 18 * 3) {
            @Override
            public void onSlotChanged() {
                if (!player.worldObj.isRemote) {
                    entity.onOrdersInventoryChanged();
                }
                super.onSlotChanged();
            }
        });//work/combat/route orders slot   TODO add slot validation

        guiHeight = addPlayerSlots(8, 8 + 5 * 18 + 8 + 18, 4) + 8;
        name = entity.getCustomNameTag();
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("customName")) {
            this.name = tag.getString("customName");
        }
        if (tag.hasKey("repack")) {
            entity.repackEntity(player);
        }
        if (tag.hasKey("setHome")) {
            entity.setHomeAreaAtCurrentPosition();
        }
        if (tag.hasKey("clearHome")) {
            entity.detachHome();
        }
        if (tag.hasKey("customTexture")) {
            entity.setCustomTexRef(tag.getString("customTexture"));
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

}
