package net.shadowmage.ancientwarfare.structure.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase;

import java.util.Optional;

public class CommonProxyStructure extends CommonProxyBase {
	public void clearTemplatePreviewCache() {
		//noop here, overriden in the client proxy
	}

	public void resetSoundAt(BlockPos pos) {
		//noop here, overriden in the client proxy
	}

	public void setSoundAt(BlockPos pos, SoundEvent currentTune, float volume) {
		//noop here, overriden in the client proxy
	}

	public void stopSoundAt(BlockPos pos) {
		//noop here, overriden in the client proxy
	}

	@SuppressWarnings("squid:S1172") // used in client proxy
	public boolean hasSoundAt(BlockPos pos) {
		return false;
	}

	@SuppressWarnings("squid:S1172") // used in client proxy
	public boolean isSoundPlayingAt(BlockPos pos) {
		return false;
	}

	public void playSoundAt(BlockPos pos) {
		//noop here, overriden in the client proxy
	}

	@SuppressWarnings("squid:S1172") // used in client proxy
	public double getClientPlayerDistanceTo(BlockPos pos) {
		return 0;
	}

	public Optional<EntityPlayer> getPlayer() { return Optional.empty(); }
}
