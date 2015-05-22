package net.shadowmage.ancientwarfare.structure.world_gen;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;
import net.shadowmage.ancientwarfare.structure.town.WorldTownGenerator;

import java.util.ArrayList;
import java.util.List;

public final class WorldGenTickHandler {

    public static final WorldGenTickHandler INSTANCE = new WorldGenTickHandler();
    private final List<ChunkGenerationTicket> newWorldGenTickets, newTownGenTickets, chunksToGen, townChunksToGen;
    private final List<StructureTicket> newStructureGenTickets, structuresToGen;

    private WorldGenTickHandler() {
        newWorldGenTickets = new ArrayList<ChunkGenerationTicket>();
        newTownGenTickets = new ArrayList<ChunkGenerationTicket>();
        newStructureGenTickets = new ArrayList<StructureTicket>();
        chunksToGen = new ArrayList<ChunkGenerationTicket>();
        townChunksToGen = new ArrayList<ChunkGenerationTicket>();
        structuresToGen = new ArrayList<StructureTicket>();
    }

    public void addChunkForGeneration(World world, int chunkX, int chunkZ) {
        newWorldGenTickets.add(new ChunkGenerationTicket(world, chunkX, chunkZ));
    }

    public void addChunkForTownGeneration(World world, int chunkX, int chunkZ) {
        newTownGenTickets.add(new ChunkGenerationTicket(world, chunkX, chunkZ));
    }

    public void addStructureForGeneration(StructureBuilder builder) {
        newStructureGenTickets.add(new StructureGenerationTicket(builder));
    }

    public void addStructureGenCallback(StructureGenerationCallbackTicket tk) {
        newStructureGenTickets.add(tk);
    }

    @SubscribeEvent
    public void serverTick(ServerTickEvent evt) {
        if (evt.phase == Phase.END) {
            genChunks();
            genStructures();
            genTowns();
        }
    }

    public void finalTick(){
        while(!chunksToGen.isEmpty()){
            genChunks();
        }
        while (!structuresToGen.isEmpty()){
            genStructures();
        }
        while (!townChunksToGen.isEmpty()){
            genTowns();
        }
    }

    private void genChunks() {
        if (!chunksToGen.isEmpty()) {
            ChunkGenerationTicket tk = chunksToGen.remove(0);
            WorldStructureGenerator.INSTANCE.generateAt(tk.chunkX, tk.chunkZ, tk.getWorld());
        }
        if (!newWorldGenTickets.isEmpty()) {
            chunksToGen.addAll(newWorldGenTickets);
            newWorldGenTickets.clear();
        }
    }

    private void genTowns() {
        if (!townChunksToGen.isEmpty()) {
            ChunkGenerationTicket tk = townChunksToGen.remove(0);
            WorldTownGenerator.INSTANCE.attemptGeneration(tk.getWorld(), tk.chunkX * 16, tk.chunkZ * 16);
        }
        if (!newTownGenTickets.isEmpty()) {
            townChunksToGen.addAll(newTownGenTickets);
            newTownGenTickets.clear();
        }
    }

    private void genStructures() {
        if (!structuresToGen.isEmpty()) {
            structuresToGen.remove(0).call();
        }
        if (!newStructureGenTickets.isEmpty()) {
            structuresToGen.addAll(newStructureGenTickets);
            newStructureGenTickets.clear();
        }
    }

    private static class ChunkGenerationTicket {
        final int world, chunkX, chunkZ;

        public ChunkGenerationTicket(World world, int x, int z) {
            this.world = world.provider.dimensionId;
            this.chunkX = x;
            this.chunkZ = z;
        }

        public World getWorld() {
            return DimensionManager.getWorld(world);
        }
    }

    /**
     * Base structure ticket class.  Changed to a callback mechanism to allow anonymous callback classes,
     * to inform town-gen of when first / second pass structures are finished being generated; to allow
     * the road to generate after walls, etc
     *
     * @author Shadowmage
     */
    private static interface StructureTicket {
        public void call();
    }

    private static final class StructureGenerationTicket implements StructureTicket {
        private final StructureBuilder builder;

        public StructureGenerationTicket(StructureBuilder builder) {
            this.builder = builder;
        }

        @Override
        public void call() {
            builder.instantConstruction();
        }
    }

    public static interface StructureGenerationCallbackTicket extends StructureTicket {

    }

}
