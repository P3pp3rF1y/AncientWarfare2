package net.shadowmage.ancientwarfare.structure.tile;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.block.BlockStoneCoffin;
import net.shadowmage.ancientwarfare.structure.init.AWStructureSounds;
import net.shadowmage.ancientwarfare.structure.util.LootHelper;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class TileStoneCoffin extends TileMulti implements ITickable, ISpecialLootContainer {

	private BlockStoneCoffin.CoffinDirection direction = BlockStoneCoffin.CoffinDirection.NORTH;
	private boolean opening = false;
	private boolean open = false;
	private float prevLidAngle = 0;
	private float lidAngle = 0;
	private int openTime = 0;
	private LootSettings lootSettings = new LootSettings();
	private static final float OPEN_ANGLE = 15F;
	private static final int TOTAL_OPEN_TIME = 60;
	Random rand = new Random();

	public int getVariant() {
		return variant;
	}

	public void setVariant(int variant) {
		this.variant = variant;
	}

	private int variant = 1;

	@Override
	public Set<BlockPos> getAdditionalPositions(IBlockState state) {
		return
				ImmutableSet.of(
						pos.offset(direction.getFacing()),
						pos.offset(direction.getFacing()).offset(direction.getFacing()),
						pos.offset(direction.getFacing()).offset(direction.getFacing()).offset(direction.getFacing()),
						pos.offset(direction.getFacing().rotateYCCW()),
						pos.offset(direction.getFacing().rotateYCCW()).offset(direction.getFacing()),
						pos.offset(direction.getFacing().rotateYCCW()).offset(direction.getFacing()).offset(direction.getFacing()),
						pos.offset(direction.getFacing().rotateYCCW()).offset(direction.getFacing()).offset(direction.getFacing()).offset(direction.getFacing())
				);
	}

	@Override
	public void setPlacementDirection(World world, BlockPos pos, IBlockState state, EnumFacing horizontalFacing, float rotationYaw) {
		setDirection(BlockStoneCoffin.CoffinDirection.fromFacing(horizontalFacing));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readNBT(compound);
	}

	private void readNBT(NBTTagCompound compound) {
		direction = BlockStoneCoffin.CoffinDirection.fromName(compound.getString("direction"));
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

	public void setDirection(BlockStoneCoffin.CoffinDirection direction) {
		this.direction = direction;
	}

	public BlockStoneCoffin.CoffinDirection getDirection() {
		return direction;
	}

	private static final Map<Integer, SoundEvent> COFFIN_SOUNDS = ImmutableMap.of(
			1, AWStructureSounds.STONE_COFFIN_OPENS,
			2, AWStructureSounds.SANDSTONE_SARCOPHAGUS_OPENS,
			3, AWStructureSounds.PRISMARINE_COFFIN_OPENS,
			4, AWStructureSounds.DEMONIC_COFFIN_OPENS);

	public void open(EntityPlayer player) {
		Optional<BlockPos> mainPos = getMainBlockPos();
		if (!mainPos.isPresent() || mainPos.get().equals(pos)) {
			if (!open && !opening) {
				playSound(getVariant(), pos);
				opening = true;
			}
			BlockTools.notifyBlockUpdate(this);
			return;
		}
		WorldTools.getTile(world, mainPos.get(), TileStoneCoffin.class).ifPresent(te -> te.playSound(te.getVariant(), mainPos.get()));
		WorldTools.getTile(world, mainPos.get(), TileStoneCoffin.class).ifPresent(te -> te.setOpening(true));
		WorldTools.getTile(world, mainPos.get(), TileStoneCoffin.class).ifPresent(BlockTools::notifyBlockUpdate);
	}

	private void playSound(int variant, BlockPos pos) {
		world.playSound(null, pos, COFFIN_SOUNDS.get(variant), SoundCategory.BLOCKS, 1, 1);

	}

	private void dropLoot(EntityPlayer player) {
		if (open) {
			return;
		}
		Optional<BlockPos> mainPos = getMainBlockPos();
		if (!mainPos.isPresent() || mainPos.get().equals(pos)) {
			if (!world.isRemote) {
				LootHelper.dropLoot(this, player); // csak ezt kapcsoltam ki be
			}
			return;
		}
		WorldTools.getTile(world, mainPos.get(), TileStoneCoffin.class).ifPresent(te -> te.dropLoot(player));
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
				dropLoot(EntityTools.findClosestPlayer(world, pos, 100));
				open = true;
			}
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		Vec3i vec = direction.getFacing().getDirectionVec();
		return new AxisAlignedBB(pos.add(-3, 0, -3), pos.add(3, 2, 3)).expand(vec.getX(), vec.getY(), vec.getZ());
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

	public void setOpening(boolean opening) {
		this.opening = opening;
	}
}
