package net.shadowmage.ancientwarfare.structure.tile;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.block.BlockCoffin;
import net.shadowmage.ancientwarfare.structure.init.AWStructureSounds;
import net.shadowmage.ancientwarfare.structure.util.LootHelper;

import java.util.Optional;
import java.util.Set;

public class TileCoffin extends TileMulti implements ITickable, ISpecialLootContainer {
	private boolean upright = false;
	private BlockCoffin.CoffinDirection direction = BlockCoffin.CoffinDirection.NORTH;
	private boolean opening = false;
	private boolean open = false;
	private float prevLidAngle = 0;
	private float lidAngle = 0;
	private int openTime = 0;
	private LootSettings lootSettings = new LootSettings();
	private static final float OPEN_ANGLE = 15F;
	private static final int TOTAL_OPEN_TIME = 20;

	public int getVariant() {
		return variant;
	}

	public void setVariant(int variant) {
		this.variant = variant;
	}

	private int variant = 1;

	@Override
	public Set<BlockPos> getAdditionalPositions(IBlockState state) {
		return upright ? ImmutableSet.of(pos.up(), pos.up().up()) :
				ImmutableSet.of(pos.offset(direction.getFacing()), pos.offset(direction.getFacing()).offset(direction.getFacing()));
	}

	@Override
	public void setPlacementDirection(World world, BlockPos pos, IBlockState state, EnumFacing horizontalFacing, float rotationYaw) {
		setDirection(upright ? BlockCoffin.CoffinDirection.fromYaw(rotationYaw) : BlockCoffin.CoffinDirection.fromFacing(horizontalFacing));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readNBT(compound);
	}

	private void readNBT(NBTTagCompound compound) {
		upright = compound.getBoolean("upright");
		direction = BlockCoffin.CoffinDirection.fromName(compound.getString("direction"));
		variant = compound.getInteger("variant");
		opening = compound.getBoolean("opening");
		open = compound.getBoolean("open");
		if (open) {
			lidAngle = prevLidAngle = OPEN_ANGLE;
		}
		lootSettings = LootSettings.deserializeNBT(compound.getCompoundTag("lootSettings"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		writeNBT(compound);
		return compound;
	}

	private void writeNBT(NBTTagCompound compound) {
		compound.setBoolean("upright", upright);
		compound.setString("direction", direction.getName());
		compound.setInteger("variant", variant);
		compound.setBoolean("opening", opening);
		compound.setBoolean("open", open);
		compound.setTag("lootSettings", lootSettings.serializeNBT());
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		writeNBT(tag);
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		readNBT(tag);
	}

	public void setUpright(boolean upright) {
		this.upright = upright;
	}

	public void setDirection(BlockCoffin.CoffinDirection direction) {
		this.direction = direction;
	}

	public BlockCoffin.CoffinDirection getDirection() {
		return direction;
	}

	public boolean getUpright() {
		return upright;
	}

	public void open(EntityPlayer player) {
		Optional<BlockPos> mainPos = getMainBlockPos();
		if (!mainPos.isPresent() || mainPos.get().equals(pos)) {
			if (!open && !opening) {
				world.playSound(null, pos, AWStructureSounds.COFFIN_OPENS, SoundCategory.BLOCKS, 1, 1);
				opening = true;
			}
			dropLoot(player);
			return;
		}
		WorldTools.getTile(world, mainPos.get(), TileCoffin.class).ifPresent(te -> te.open(player));
	}

	private void dropLoot(EntityPlayer player) {
		if (open) {
			return;
		}
		Optional<BlockPos> mainPos = getMainBlockPos();
		if (!mainPos.isPresent() || mainPos.get().equals(pos)) {
			if (!world.isRemote) {
				LootHelper.dropLoot(this, player);
			}
			return;
		}
		WorldTools.getTile(world, mainPos.get(), TileCoffin.class).ifPresent(te -> te.dropLoot(player));
	}

	@Override
	public void onBlockBroken(IBlockState state) {
		dropLoot(EntityTools.findClosestPlayer(world, pos, 100));
		super.onBlockBroken(state);
	}

	@Override
	public void update() {
		if (opening && !open) {
			prevLidAngle = lidAngle;
			openTime++;

			float halfAngle = OPEN_ANGLE / 2;
			float halfTime = (float) TOTAL_OPEN_TIME / 2;
			if (openTime > halfTime) {
				float ratio = (TOTAL_OPEN_TIME - openTime) / halfTime;
				lidAngle = OPEN_ANGLE - (halfAngle * ratio * ratio);
			} else {
				float ratio = openTime / halfTime;
				lidAngle = halfAngle * ratio * ratio;
			}
			if (lidAngle >= OPEN_ANGLE) {
				prevLidAngle = lidAngle;
				open = true;
			}
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if (upright) {
			return new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 3, 2));
		}

		Vec3i vec = direction.getFacing().getDirectionVec();
		return new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 1, 2)).expand(vec.getX(), vec.getY(), vec.getZ());
	}

	public float getPrevLidAngle() {
		return prevLidAngle;
	}

	public float getLidAngle() {
		return lidAngle;
	}

	@Override
	public void setLootSettings(LootSettings settings) {
		this.lootSettings = settings;
	}

	@Override
	public LootSettings getLootSettings() {
		return lootSettings;
	}
}
