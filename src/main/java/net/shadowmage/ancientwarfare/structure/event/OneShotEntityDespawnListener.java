package net.shadowmage.ancientwarfare.structure.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.structure.util.CapabilityRespawnData;
import net.shadowmage.ancientwarfare.structure.util.IRespawnData;
import net.shadowmage.ancientwarfare.structure.util.SpawnerHelper;

import javax.annotation.Nullable;

public class OneShotEntityDespawnListener implements IWorldEventListener {
	public static final OneShotEntityDespawnListener INSTANCE = new OneShotEntityDespawnListener();

	private OneShotEntityDespawnListener() {}


	@Override
	public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
		//noop
	}

	@Override
	public void notifyLightSet(BlockPos pos) {
		//noop

	}

	@Override
	public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
		//noop

	}

	@Override
	public void playSoundToAllNearExcept(
			@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {
		//noop
	}

	@Override
	public void playRecord(SoundEvent soundIn, BlockPos pos) {
		//noop
	}

	@Override
	public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
		//noop
	}

	@Override
	public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
		//noop
	}

	@Override
	public void onEntityAdded(Entity entityIn) {
		//noop
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void onEntityRemoved(Entity entityIn) {
		if (entityIn.hasCapability(CapabilityRespawnData.RESPAWN_DATA_CAPABILITY, null)) {
			IRespawnData respawnData = entityIn.getCapability(CapabilityRespawnData.RESPAWN_DATA_CAPABILITY, null);

			if (respawnData.canRespawn() && ((EntityLivingBase) entityIn).getHealth() > 0 && !(entityIn instanceof NpcFaction)) { //NpcFaction is handled separately
				SpawnerHelper.createSpawner(respawnData, entityIn.world);
			}
		}
	}

	@Override
	public void broadcastSound(int soundID, BlockPos pos, int data) {
		//noop
	}

	@Override
	public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
		//noop
	}

	@Override
	public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
		//noop
	}
}
