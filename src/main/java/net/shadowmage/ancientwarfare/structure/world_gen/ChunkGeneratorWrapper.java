package net.shadowmage.ancientwarfare.structure.world_gen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.ChunkGeneratorFlat;
import net.minecraft.world.gen.ChunkGeneratorHell;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.shadowmage.ancientwarfare.core.config.AWLog;

import java.lang.reflect.Field;

public abstract class ChunkGeneratorWrapper {
    private ChunkGeneratorWrapper() {
    }

    public static ChunkGeneratorWrapper getWrapper(IChunkGenerator chunkGenerator) {
        if (chunkGenerator instanceof ChunkGeneratorOverworld) {
            return new Overworld((ChunkGeneratorOverworld) chunkGenerator);
        } else if (chunkGenerator instanceof ChunkGeneratorEnd) {
            return new End((ChunkGeneratorEnd) chunkGenerator);
        } else if (chunkGenerator instanceof ChunkGeneratorFlat) {
            ChunkGeneratorFlat flatGenerator = (ChunkGeneratorFlat) chunkGenerator;
            return new Flat(flatGenerator);
        } else if (chunkGenerator instanceof ChunkGeneratorHell) {
            return new Hell((ChunkGeneratorHell) chunkGenerator);
        }

        return new Bedrock();
    }

    public abstract void setBlocksInChunk(int chunkX, int chunkZ, ChunkPrimer primer);

    private static void fillPrimer(ChunkPrimer primer, IBlockState stateToFill) {
        for(int x = 0; x < 16; x++) {
            for(int y = 0; y < 256; y++) {
                for(int z = 0; z < 16; z++) {
                    primer.setBlockState(x, y, z, stateToFill);
                }
            }
        }
    }

    public static class Bedrock extends ChunkGeneratorWrapper {
        @Override
        public void setBlocksInChunk(int chunkX, int chunkZ, ChunkPrimer primer) {
            fillPrimer(primer, Blocks.BEDROCK.getDefaultState());
        }
    }

    public static class Overworld extends ChunkGeneratorWrapper {
        private final ChunkGeneratorOverworld generator;
        public Overworld(ChunkGeneratorOverworld generator) {
            this.generator = generator;
        }

        @Override
        public void setBlocksInChunk(int chunkX, int chunkZ, ChunkPrimer primer) {
            generator.setBlocksInChunk(chunkX, chunkZ, primer);
        }
    }

    public static class End extends ChunkGeneratorWrapper {
        private final ChunkGeneratorEnd generator;
        public End(ChunkGeneratorEnd generator) {
            this.generator = generator;
        }

        @Override
        public void setBlocksInChunk(int chunkX, int chunkZ, ChunkPrimer primer) {
            generator.setBlocksInChunk(chunkX, chunkZ, primer);
        }
    }

    public static class Flat extends ChunkGeneratorWrapper {
        private IBlockState[] levelStates;
        private static final Field CACHED_BLOCK_IDS = ReflectionHelper.findField(ChunkGeneratorFlat.class, "field_82700_c", "cachedBlockIDs");
        public Flat(ChunkGeneratorFlat generator) {
            try {
                levelStates = (IBlockState[]) CACHED_BLOCK_IDS.get(generator);
            } catch (IllegalAccessException e) {
                AWLog.logError(e);
                levelStates = new IBlockState[0];
            }
        }

        private IBlockState getStateOnLevel(int level) {
            if (level < levelStates.length) {
                return levelStates[level];
            }
            return Blocks.AIR.getDefaultState();
        }

        @Override
        public void setBlocksInChunk(int chunkX, int chunkZ, ChunkPrimer primer) {
            for(int y = 0; y < 256; y++) {
                IBlockState state = getStateOnLevel(y);
                for(int x = 0; x < 16; x++) {
                    for(int z = 0; z < 16; z++) {
                        primer.setBlockState(x, y, z, state);
                    }
                }
            }
        }
    }
    public static class Hell extends ChunkGeneratorWrapper {
        private final ChunkGeneratorHell generator;

        public Hell(ChunkGeneratorHell generator) {
            this.generator = generator;
        }

        @Override
        public void setBlocksInChunk(int chunkX, int chunkZ, ChunkPrimer primer) {
            generator.prepareHeights(chunkX, chunkZ, primer);
            generator.buildSurfaces(chunkX, chunkZ, primer);
        }
    }
}
