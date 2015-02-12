package net.shadowmage.ancientwarfare.structure.world_gen;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;
import net.shadowmage.ancientwarfare.structure.town.WorldTownGenerator;

import java.util.ArrayList;
import java.util.List;

public class WorldGenTickHandler {

    private WorldGenTickHandler() {
    }

    private static WorldGenTickHandler INSTANCE = new WorldGenTickHandler();

    public static WorldGenTickHandler instance() {
        return INSTANCE;
    }

    private List<ChunkGenerationTicket> newWorldGenTickets = new ArrayList<ChunkGenerationTicket>();
    private List<ChunkGenerationTicket> newTownGenTickets = new ArrayList<ChunkGenerationTicket>();
    private List<StructureTicket> newStructureGenTickets = new ArrayList<StructureTicket>();

    private List<ChunkGenerationTicket> chunksToGen = new ArrayList<ChunkGenerationTicket>();
    private List<ChunkGenerationTicket> townChunksToGen = new ArrayList<ChunkGenerationTicket>();
    private List<StructureTicket> structuresToGen = new ArrayList<StructureTicket>();

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
            chunksToGen.addAll(newWorldGenTickets);
            newWorldGenTickets.clear();
            structuresToGen.addAll(newStructureGenTickets);
            newStructureGenTickets.clear();
            townChunksToGen.addAll(newTownGenTickets);
            newTownGenTickets.clear();
        }
    }

    private void genChunks() {
        if (!chunksToGen.isEmpty()) {
            ChunkGenerationTicket tk = chunksToGen.remove(0);
            WorldStructureGenerator.instance().generateAt(tk.chunkX, tk.chunkZ, tk.world);
        }
    }

    private void genTowns() {
        if (!townChunksToGen.isEmpty()) {
            ChunkGenerationTicket tk = townChunksToGen.remove(0);
            WorldTownGenerator.instance().attemptGeneration(tk.world, tk.world.rand, tk.chunkX * 16, tk.chunkZ * 16);
        }
    }

    private void genStructures() {
        if (!structuresToGen.isEmpty()) {
            structuresToGen.remove(0).call();
        }
    }

    private static class ChunkGenerationTicket {
        World world;
        int chunkX, chunkZ;

        public ChunkGenerationTicket(World world, int x, int z) {
            this.world = world;
            this.chunkX = x;
            this.chunkZ = z;
        }
    }

    /**
     * Base structure ticket class.  Changed to a callback mechanism to allow anonymous callback classes,
     * to inform town-gen of when first / second pass structures are finished being generated; to allow
     * the road to generate after walls, etc
     *
     * @author Shadowmage
     */
    private static class StructureTicket {
        public void call() {
        }
    }

    private static class StructureGenerationTicket extends StructureTicket {
        StructureBuilder builder;

        public StructureGenerationTicket(StructureBuilder builder) {
            this.builder = builder;
        }

        @Override
        public void call() {
            builder.instantConstruction();
        }
    }

    public abstract static class StructureGenerationCallbackTicket extends StructureTicket {
        @Override
        public abstract void call();
    }

}
