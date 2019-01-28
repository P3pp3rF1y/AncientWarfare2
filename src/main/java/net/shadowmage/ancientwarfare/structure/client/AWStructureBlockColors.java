package net.shadowmage.ancientwarfare.structure.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.tile.TileColored;
import net.shadowmage.ancientwarfare.structure.tile.TileSoundBlock;

@SideOnly(Side.CLIENT)
public class AWStructureBlockColors {
	private AWStructureBlockColors() {}

	public static void init() {
		BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();

		blockColors.registerBlockColorHandler((state, world, pos, tintIndex) -> WorldTools.getTile(world, pos, TileColored.class).map(TileColored::getColor).orElse(-1)
				, AWStructureBlocks.ALTAR_CANDLE);

		blockColors.registerBlockColorHandler((state, world, pos, tintIndex) -> {
			IBlockState disguiseState = WorldTools.getTile(world, pos, TileSoundBlock.class).filter(t -> t.getDisguiseState() != null)
					.map(TileSoundBlock::getDisguiseState).orElse(Blocks.JUKEBOX.getDefaultState());
			return Minecraft.getMinecraft().getBlockColors().colorMultiplier(disguiseState, world, pos, 0);
		}, AWStructureBlocks.SOUND_BLOCK);
	}
}
