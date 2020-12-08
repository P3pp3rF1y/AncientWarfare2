package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.tile.IBlockBreakHandler;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.structure.util.LootHelper;

public class TileGravestone extends TileUpdatable implements ISpecialLootContainer, IBlockBreakHandler, BlockRotationHandler.IRotatableTile {
	private EnumFacing facing = EnumFacing.NORTH;
	private int variant = 1;
	private LootSettings lootSettings = new LootSettings();

	@Override
	public EnumFacing getPrimaryFacing() {
		return facing;
	}

	@Override
	public void setPrimaryFacing(EnumFacing face) {
		facing = face;
	}

	public void setVariant(int variant) {
		this.variant = variant;
	}

	public int getVariant() {
		return variant;
	}

	@Override
	public void setLootSettings(LootSettings settings) {
		lootSettings = settings;
	}

	@Override
	public LootSettings getLootSettings() {
		return lootSettings;
	}

	@Override
	public void onBlockBroken(IBlockState state) {
		LootHelper.dropLoot(this, EntityTools.findClosestPlayer(world, pos, 100));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readNBT(compound);
		markDirty();
	}

	private void readNBT(NBTTagCompound compound) {
		variant = compound.getInteger("variant");
		lootSettings = LootSettings.deserializeNBT(compound.getCompoundTag("lootSettings"));
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		writeNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		writeNBT(compound);
		return compound;
	}

	private NBTTagCompound writeNBT(NBTTagCompound compound) {
		compound.setTag("lootSettings", lootSettings.serializeNBT());
		compound.setInteger("variant", variant);
		return compound;
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		readNBT(tag);
		BlockTools.notifyBlockUpdate(this);
	}

	public void activate(EntityPlayer player) {
		if (6 <= getVariant() && getVariant() <= 8) { // only for runestones: variant 6,7,8
			dropLoot(player);
		}
	}

	private void dropLoot(EntityPlayer player) {
		if (!world.isRemote) {
			LootHelper.dropLoot(this, player);
		}
	}
}
