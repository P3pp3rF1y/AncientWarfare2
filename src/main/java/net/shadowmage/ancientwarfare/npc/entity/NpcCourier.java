package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDoor;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFleeHostiles;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedAlarmResponse;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedCourier;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedFollowCommand;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedGetFood;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedIdleWhenHungry;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedRideHorse;
import net.shadowmage.ancientwarfare.npc.item.ItemRoutingOrder;

public class NpcCourier extends NpcPlayerOwned {

	NpcAIPlayerOwnedCourier courierAI;
	public IItemHandler backpackInventory;

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
		return !stack.isEmpty() && stack.getItem() instanceof ItemRoutingOrder;
	}

	@Override
	public void onOrdersInventoryChanged() {
		courierAI.onOrdersChanged();
	}

	@Override
	public void onWeaponInventoryChanged() {
		super.onWeaponInventoryChanged();
		backpackInventory = getHeldItemMainhand().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing) null);
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
