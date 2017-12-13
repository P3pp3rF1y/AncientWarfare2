package net.shadowmage.ancientwarfare.structure.world_gen;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.concurrent.TimeUnit;

public class NoGenWorld {
    private Cache<Long, ChunkPrimer> chunkPrimers = CacheBuilder.newBuilder().maximumSize(500).expireAfterAccess(10, TimeUnit.MINUTES).build();

    private final ChunkGeneratorWrapper generatorWrapper;
    private final WorldServer world;
    private int actualHeight;

    public NoGenWorld(WorldServer world) {
        this.world = world;
        generatorWrapper = ChunkGeneratorWrapper.getWrapper(world.getChunkProvider().chunkGenerator);
    }

    public IBlockState getBlockState(BlockPos pos) {
        if (chunkExists(pos)) {
            return world.getBlockState(pos);
        } else {
            return getChunkPrimer(pos).getBlockState(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public BlockPos getGroundPos(BlockPos pos) {
        if (chunkExists(pos)) {
            BlockPos groundPos = new BlockPos(pos.getX(), world.getActualHeight(), pos.getZ());
            for(; groundPos.getY() > 0; groundPos = groundPos.down()) {
                if (world.getBlockState(groundPos).getBlock() != Blocks.AIR) {
                    break;
                }
            }

            return groundPos;
        } else {
            return new BlockPos(pos.getX(), getChunkPrimer(pos).findGroundBlockIdx(pos.getX() & 15, pos.getZ() & 15), pos.getZ());
        }
    }


    private boolean chunkExists(BlockPos pos) {
        return world.getChunkProvider().chunkExists(pos.getX() >> 4, pos.getZ() >> 4);
    }

    private ChunkPrimer getChunkPrimer(BlockPos pos) {
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;

        long id = ChunkPos.asLong(chunkX, chunkZ);
        ChunkPrimer ret = chunkPrimers.getIfPresent(id);
        if (ret == null) {
            ChunkPrimer chunkPrimer = new ChunkPrimer();
            generatorWrapper.setBlocksInChunk(chunkX, chunkZ, chunkPrimer);
            chunkPrimers.put(id, chunkPrimer);
        }
        return chunkPrimers.getIfPresent(id);
    }

    public int getActualHeight() {
        return world.getActualHeight();
    }
}
