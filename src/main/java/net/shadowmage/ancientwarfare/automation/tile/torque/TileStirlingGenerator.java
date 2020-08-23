package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.tile.IBlockBreakHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nullable;

public class TileStirlingGenerator extends TileTorqueSingleCell implements IBlockBreakHandler {

	private final ItemStackHandler fuelHandler = new ItemStackHandler(1) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			return TileEntityFurnace.getItemBurnTime(stack) > 0 ? super.insertItem(slot, stack, simulate) : stack;
		}
	};

	private int burnTime = 0;
	private int burnTimeBase = 0;

	public TileStirlingGenerator() {
		torqueCell = new TorqueCell(0, 4, 1600, AWAutomationStatics.med_efficiency_factor);
	}

	@Override
	public void update() {
		super.update();
		if (!world.isRemote) {
			if (burnTime <= 0 && torqueCell.getEnergy() < torqueCell.getMaxEnergy()) {
				//if fueled, consume one, set burn-ticks to fuel value
				int ticks = TileEntityFurnace.getItemBurnTime(fuelHandler.getStackInSlot(0));
				if (ticks > 0) {
					fuelHandler.extractItem(0, 1, false);
					burnTime = ticks;
					burnTimeBase = ticks;
				}
			} else if (burnTime > 0) {
				torqueCell.setEnergy(torqueCell.getEnergy() + AWAutomationStatics.stirling_generator_output);
				burnTime--;
			}
		}
	}

	@Override
	public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
		if (!player.world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_STIRLING_GENERATOR, pos);
		}
		return true;
	}

	public int getBurnTime() {
		return burnTime;
	}

	public int getBurnTimeBase() {
		return burnTimeBase;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		burnTime = tag.getInteger("burnTicks");
		burnTimeBase = tag.getInteger("burnTicksBase");
		if (tag.hasKey("inventory")) {
			fuelHandler.deserializeNBT(tag.getCompoundTag("inventory"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("burnTicks", burnTime);
		tag.setInteger("burnTicksBase", burnTimeBase);
		tag.setTag("inventory", fuelHandler.serializeNBT());
		return tag;
	}

	@Override
	public boolean canInputTorque(EnumFacing from) {
		return false;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) fuelHandler;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void onBlockBroken(IBlockState state) {
		InventoryTools.dropItemsInWorld(world, fuelHandler, pos);
	}
}
