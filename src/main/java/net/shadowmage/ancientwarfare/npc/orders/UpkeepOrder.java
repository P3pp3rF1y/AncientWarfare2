package net.shadowmage.ancientwarfare.npc.orders;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.block.BlockTownHall;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.item.ItemUpkeepOrder;

import java.util.Optional;

public class UpkeepOrder implements INBTSerializable<NBTTagCompound> {
	private static final String UPKEEP_POSITION_TAG = "upkeepPosition";
	private static final String ORDERS_TAG = "orders";
	private BlockPos upkeepPosition;
	private int upkeepDimension;
	private EnumFacing blockSide = EnumFacing.DOWN;
	private int upkeepAmount = 6000;

	public void changeBlockSide() {
		blockSide = EnumFacing.VALUES[(blockSide.ordinal() + 1) % EnumFacing.VALUES.length];
	}

	public void removeUpkeepPoint() {
		upkeepPosition = null;
		blockSide = EnumFacing.DOWN;
		upkeepDimension = 0;
		upkeepAmount = 6000;
	}

	public void setUpkeepAmount(int amt) {
		this.upkeepAmount = amt;
	}

	public EnumFacing getUpkeepBlockSide() {
		return blockSide;
	}

	public int getUpkeepDimension() {
		return upkeepDimension;
	}

	public Optional<BlockPos> getUpkeepPosition() {
		return Optional.ofNullable(upkeepPosition);
	}

	public final int getUpkeepAmount() {
		return upkeepAmount;
	}

	public boolean addUpkeepPosition(World world, BlockPos pos) {
		if (WorldTools.getTile(world, pos, TileEntity.class).map(InventoryTools::isInventory).orElse(false)) {
			if (!AWNPCStatics.npcAllowUpkeepAnyInventory && (!(world.getBlockState(pos).getBlock() instanceof BlockTownHall)))
				return false;
			upkeepPosition = pos;
			upkeepDimension = world.provider.getDimension();
			blockSide = EnumFacing.DOWN;
			upkeepAmount = 6000;
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Upkeep Orders[" + upkeepPosition + "]";
	}

	public static Optional<UpkeepOrder> getUpkeepOrder(ItemStack stack) {
		if (!stack.isEmpty() && stack.getItem() instanceof ItemUpkeepOrder) {
			UpkeepOrder order = new UpkeepOrder();
			//noinspection ConstantConditions
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey(ORDERS_TAG)) {
				order.deserializeNBT(stack.getTagCompound().getCompoundTag(ORDERS_TAG));
			}
			return Optional.of(order);
		}
		return Optional.empty();
	}

	public void write(ItemStack stack) {
		if (!stack.isEmpty() && stack.getItem() instanceof ItemUpkeepOrder) {
			stack.setTagInfo(ORDERS_TAG, serializeNBT());
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		if (getUpkeepPosition().isPresent()) {
			tag.setLong(UPKEEP_POSITION_TAG, upkeepPosition.toLong());
			tag.setInteger("dim", upkeepDimension);
			tag.setByte("side", (byte) blockSide.ordinal());
			tag.setInteger("upkeepAmount", upkeepAmount);
		}
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		if (tag.hasKey(UPKEEP_POSITION_TAG)) {
			upkeepPosition = BlockPos.fromLong(tag.getLong(UPKEEP_POSITION_TAG));
			upkeepDimension = tag.getInteger("dim");
			blockSide = EnumFacing.VALUES[tag.getByte("side")];
			upkeepAmount = tag.getInteger("upkeepAmount");
		}
	}
}
