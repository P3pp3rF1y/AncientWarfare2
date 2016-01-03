package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.core.item.ItemBackpack;
import net.shadowmage.ancientwarfare.npc.ai.*;
import net.shadowmage.ancientwarfare.npc.ai.owned.*;
import net.shadowmage.ancientwarfare.npc.item.ItemRoutingOrder;

public class NpcCourier extends NpcPlayerOwned {

    NpcAIPlayerOwnedCourier courierAI;
    public InventoryBackpack backpackInventory;

    public NpcCourier(World par1World) {
        super(par1World);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(0, new NpcAIDoor(this, true));
        this.tasks.addTask(0, (horseAI = new NpcAIPlayerOwnedRideHorse(this)));
        this.tasks.addTask(2, new NpcAIFollowPlayer(this));
        this.tasks.addTask(2, new NpcAIPlayerOwnedFollowCommand(this));
        this.tasks.addTask(3, new NpcAIFleeHostiles(this));
        this.tasks.addTask(3, new NpcAIPlayerOwnedAlarmResponse(this));
        this.tasks.addTask(4, new NpcAIPlayerOwnedGetFood(this));
        this.tasks.addTask(5, new NpcAIPlayerOwnedIdleWhenHungry(this));
        this.tasks.addTask(6, (courierAI = new NpcAIPlayerOwnedCourier(this)));
        this.tasks.addTask(7, new NpcAIMoveHome(this, 50F, 3F, 30F, 3F));

        //post-100 -- used by delayed shared tasks (look at random stuff, wander)
        this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(102, new NpcAIWander(this));
        this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
    }

    @Override
    public boolean isValidOrdersStack(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemRoutingOrder;
    }

    @Override
    public void onOrdersInventoryChanged() {
        courierAI.onOrdersChanged();
    }

    @Override
    public void onWeaponInventoryChanged() {
        super.onWeaponInventoryChanged();
        backpackInventory = ItemBackpack.getInventoryFor(getHeldItem());
    }

    /**
     * should be called from courier routing AI whenever work done>0, to ensure items are saved to backpack
     */
    public void updateBackpackItemContents() {
        ItemBackpack.writeBackpackToItem(backpackInventory, getHeldItem());
    }

    @Override
    public String getNpcSubType() {
        return "";
    }

    @Override
    public String getNpcType() {
        return "courier";
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        if (tag.hasKey("courierAI")) {
            courierAI.readFromNBT(tag.getCompoundTag("courierAI"));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setTag("courierAI", courierAI.writeToNBT(new NBTTagCompound()));
    }


}
