package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nullable;

public class TileAdvancedLootChest extends TileEntityChest {
	private int lootRolls = 0;

	@Override
	public void fillWithLoot(@Nullable EntityPlayer player) {
		if (lootTable != null) {
			ResourceLocation lt = lootTable;
			lootTable = null;
			InventoryTools.generateLootFor(world, player, this, world.rand, lt, lootRolls);
		}
	}

	@Override
	protected boolean checkLootAndRead(NBTTagCompound compound) {
		if (super.checkLootAndRead(compound)) {
			setLootRolls(compound.getByte("lootRolls"));
			return true;
		}
		return false;
	}

	@Override
	protected boolean checkLootAndWrite(NBTTagCompound compound) {
		if (super.checkLootAndWrite(compound)) {
			compound.setByte("lootRolls", (byte) lootRolls);
			return true;
		}
		return false;
	}

	public void setLootRolls(int lootRolls) {
		this.lootRolls = lootRolls;
	}
}
