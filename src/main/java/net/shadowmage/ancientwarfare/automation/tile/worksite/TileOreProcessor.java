package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

public class TileOreProcessor extends TileWorksiteBase {

	private final ItemStackHandler inventory;

	public TileOreProcessor() {
		inventory = new ItemStackHandler(2) {
			@Override
			protected void onContentsChanged(int slot) {
				markDirty();
			}
		};

	}

	@Override
	public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
		// TODO implement GUI
		return true;
	}

	@Override
	public void onBlockBroken() {
		super.onBlockBroken();
		if (!world.isRemote) {
			InventoryTools.dropItemsInWorld(world, inventory, pos);
		}
	}

	@Override
	protected Optional<IWorksiteAction> getNextAction() {
		return null;
	}

	@Override
	protected boolean processAction(IWorksiteAction action) {
		return false;
	}

	@Override
	protected void updateWorksite() {
		// TODO Auto-generated method stub
	}

	@Override
	public WorkType getWorkType() {
		return WorkType.CRAFTING;
	}

	//************************************* BRIDGE/TEMPLATE/ACCESSOR METHODS ****************************************//

	@Override
	public Set<WorksiteUpgrade> getValidUpgrades() {
		return EnumSet.noneOf(WorksiteUpgrade.class);
	}//NOOP

	//************************************* STANDARD NBT / DATA PACKET METHODS ****************************************//
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.deserializeNBT(tag.getCompoundTag("inventory"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("inventory", inventory.serializeNBT());
		return tag;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) inventory;
		}
		return super.getCapability(capability, facing);
	}
}
