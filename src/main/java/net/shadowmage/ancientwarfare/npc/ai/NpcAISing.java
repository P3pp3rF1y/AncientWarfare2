package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.shadowmage.ancientwarfare.core.interfaces.ISinger;
import net.shadowmage.ancientwarfare.core.util.SongPlayData;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.util.List;

public class NpcAISing extends NpcAI<NpcBase> {
	private static final int PLAYER_DELAY = 10;
	private static final int PLAYER_RANGE = 20;
	private boolean playing = false;//if currently playing a tune.
	private int currentDelay;//the current cooldown delay.  if not playing, this delay will be incremented before attempting to start next song
	private int tuneIndex = -1;//will be incremented to 0 before first song selected
	private int playerCheckDelay;//used to not check for players -every- tick. checks every 10 ticks
	private int playTime;//tracking current play time.  when this exceeds length, cooldown delay is triggered
	private int maxPlayTime;

	private final ISinger bard;

	public NpcAISing(NpcBase npc) {
		super(npc);
		bard = (ISinger) npc;
	}

	@Override
	public boolean shouldExecute() {
		return super.shouldExecute() && bard.getSongs().size() > 0;
	}

	@Override
	public void startExecuting() {
		//noop
	}

	@Override
	public void updateTask() {
		SongPlayData data = bard.getSongs();
		if (playing) {
			playTime++;
			if (playTime >= maxPlayTime) {
				playTime = 0;
				playing = false;
				int d = data.getMaxDelay() - data.getMinDelay();
				currentDelay = data.getMinDelay() + (d > 0 ? npc.getRNG().nextInt(d) : 0);
			}
		} else if (currentDelay > 0) {
			currentDelay--;
		} else {
			if (data.getPlayOnPlayerEntry()) {
				playerCheckDelay--;
				if (playerCheckDelay <= 0) {
					playerCheckDelay = PLAYER_DELAY;
					AxisAlignedBB aabb = npc.getEntityBoundingBox().expand(PLAYER_RANGE, PLAYER_RANGE, PLAYER_RANGE);
					List<EntityPlayer> list = npc.world.getEntitiesWithinAABB(EntityPlayer.class, aabb);
					if (!list.isEmpty()) {
						setNextSong(data);
					}
				}
			} else {
				setNextSong(data);
			}
		}
	}

	private void setNextSong(SongPlayData data) {
		if (data.getIsRandom()) {
			//TODO (ensure it was not the last song played?)
			tuneIndex = npc.getRNG().nextInt(data.size());
		} else {
			tuneIndex++;
			if (tuneIndex >= data.size()) {
				tuneIndex = 0;
			}
		}
		SongPlayData.SongEntry entry = data.get(tuneIndex);
		maxPlayTime = (int) (entry.length() * 20.f);//convert minutes into ticks
		float volume = (float) entry.volume() * 0.03f;
		entry.getSound().ifPresent(s -> npc.world.playSound(null, npc.posX, npc.posY, npc.posZ, s, SoundCategory.NEUTRAL, volume, 1.f));
		playing = true;
		playTime = 0;
	}
}
