package net.shadowmage.ancientwarfare.core.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import java.util.UUID;

public class AWFakePlayer extends FakePlayer {
	private static final String PLAYER_NAME = "AncientWarfareFakePlayer";
	private static AWFakePlayer instance;

	private AWFakePlayer(WorldServer world) {
		super(world, new GameProfile(UUID.nameUUIDFromBytes(PLAYER_NAME.getBytes()), PLAYER_NAME));
	}

	public static AWFakePlayer get(World world) {
		if (instance == null && world instanceof WorldServer) {
			instance = new AWFakePlayer((WorldServer) world);
		}
		return instance;
	}

	@Override
	protected void onInsideBlock(IBlockState p_191955_1_) {
		//noop
	}

	public static void onWorldUnload() {
		instance = null;
	}
}
