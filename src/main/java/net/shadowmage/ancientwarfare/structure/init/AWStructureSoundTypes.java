package net.shadowmage.ancientwarfare.structure.init;

import net.minecraft.block.SoundType;
import net.minecraft.init.SoundEvents;

public class AWStructureSoundTypes {
	private AWStructureSoundTypes() {}

	public static final SoundType URN = new SoundType(1.0F, 1.0F, AWStructureSounds.URN_BREAK, SoundEvents.BLOCK_STONE_STEP,
			SoundEvents.BLOCK_STONE_PLACE, SoundEvents.BLOCK_STONE_HIT, SoundEvents.BLOCK_STONE_FALL);

	public static final SoundType COINSTACK = new SoundType(1.0F, 1.3F, AWStructureSounds.COINSTACK_BREAK, AWStructureSounds.COINSTACK_INTERACT,
			AWStructureSounds.COINSTACK_INTERACT, AWStructureSounds.COINSTACK_INTERACT, AWStructureSounds.COINSTACK_BREAK);
}
