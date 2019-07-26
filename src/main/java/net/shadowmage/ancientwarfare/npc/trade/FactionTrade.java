package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class FactionTrade extends Trade {
	private static final String REFILL_TIME_TAG = "refillTime";
	private int refillFrequency;
	private long refillTime = -1;
	private int maxAvailable;
	private int currentAvailable;
	//TODO add minLevel stat -- used to determine if trade should be available

	public FactionTrade() {
		refillFrequency = 20 * 60 * 5;//five minutes per item refilled
		maxAvailable = 1;
		currentAvailable = 1;
	}

	public boolean hasItems() {
		for (int i = 0; i < size(); i++) {
			if (!getInputStack(i).isEmpty() || !getOutputStack(i).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public int getRefillFrequency() {
		return refillFrequency;
	}

	public int getMaxAvailable() {
		return maxAvailable;
	}

	public int getCurrentAvailable() {
		return currentAvailable;
	}

	public void setRefillFrequency(int refill) {
		refillFrequency = refill;
	}

	public void setMaxAvailable(int max) {
		maxAvailable = max;
		currentAvailable = max;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("refillFrequency", refillFrequency);
		tag.setLong(REFILL_TIME_TAG, refillTime);
		tag.setInteger("maxAvailable", maxAvailable);
		tag.setInteger("currentAvailable", currentAvailable);
		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		refillFrequency = tag.getInteger("refillFrequency");
		refillTime = tag.hasKey(REFILL_TIME_TAG) ? tag.getLong(REFILL_TIME_TAG) : -1;
		maxAvailable = tag.getInteger("maxAvailable");
		currentAvailable = tag.getInteger("currentAvailable");
		super.readFromNBT(tag);
	}

	public void updateTrade(long totalWorldTime) {
		if (refillTime == -1) {
			refillTime = totalWorldTime + refillFrequency;
		}

		if (refillFrequency > 0 && refillTime > 0 && refillTime <= totalWorldTime)//update per freq period
		{
			long timeDiff = totalWorldTime - refillTime;
			while (currentAvailable < maxAvailable && timeDiff >= 0) {
				timeDiff -= refillFrequency;
				currentAvailable++;
			}
			refillTime = currentAvailable < maxAvailable ? totalWorldTime + refillFrequency : 0;
		} else if (refillFrequency == 0)//full refill automatically if frequency==0
		{
			currentAvailable = maxAvailable;
		}//dont refill if frequency<0
	}

	@Override
	public boolean performTrade(EntityPlayer player, @Nullable IItemHandler storage) {
		return currentAvailable > 0 && super.performTrade(player, null);
	}

	@Override
	protected void doTrade(EntityPlayer player, @Nullable IItemHandler storage) {
		if (refillFrequency != 0) {
			currentAvailable--;
			refillTime = player.world.getTotalWorldTime() + refillFrequency;
		}//0 denotes instant restock, no reason to decrease qty if it will just be instantly restocked when GUI is opened next
		super.doTrade(player, storage);
	}

	public long getRefillTime() {
		return refillTime;
	}
}
