package net.shadowmage.ancientwarfare.structure.world_gen;

import com.google.common.collect.Lists;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;
import net.shadowmage.ancientwarfare.structure.town.WorldTownGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class WorldGenTickHandler {

    public static final WorldGenTickHandler INSTANCE = new WorldGenTickHandler();
    private final List<ChunkGenerationTicket> newTownGenTickets, townChunksToGen;
    private final CopyOnWriteArrayList<StructureTicket> structuresToGen;

    private WorldGenTickHandler() {
        newTownGenTickets = new ArrayList<>();
        townChunksToGen = new ArrayList<>();
        structuresToGen = Lists.newCopyOnWriteArrayList();
    }

    public void addChunkForTownGeneration(World world, int chunkX, int chunkZ) {
        newTownGenTickets.add(new ChunkGenerationTicket(world, chunkX, chunkZ));
    }

    public void addStructureForGeneration(StructureBuilder builder) {
        structuresToGen.add(new StructureGenerationTicket(builder));
    }

    public void addStructureGenCallback(StructureGenerationCallbackTicket tk) {
        structuresToGen.add(tk);
    }

    @SubscribeEvent
    public void serverTick(ServerTickEvent evt) {
        if (evt.phase == Phase.END) {
            genStructures();
            genTowns();
        }
    }

    public void finalTick(){
        while (!structuresToGen.isEmpty()){
            genStructures();
        }
        while (!townChunksToGen.isEmpty()){
            genTowns();
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
    }

    private static class ChunkGenerationTicket {
        final int world, chunkX, chunkZ;

        public ChunkGenerationTicket(World world, int x, int z) {
            this.world = world.provider.getDimension();
            this.chunkX = x;
            this.chunkZ = z;
        }

        public World getWorld() {
            return DimensionManager.getWorld(world);
        }
    }

    /*
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
