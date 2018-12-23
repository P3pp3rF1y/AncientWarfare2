package net.shadowmage.ancientwarfare.structure.worldgen;

import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;
import net.shadowmage.ancientwarfare.structure.town.WorldTownGenerator;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class WorldGenTickHandler {

	public static final WorldGenTickHandler INSTANCE = new WorldGenTickHandler();
	private final List<ChunkGenerationTicket> newWorldGenTickets;
	private final List<ChunkGenerationTicket> newTownGenTickets;
	private final List<ChunkGenerationTicket> chunksToGen;
	private final List<ChunkGenerationTicket> townChunksToGen;
	private final List<StructureTicket> newStructureGenTickets;
	private final List<StructureTicket> structuresToGen;

	private WorldGenTickHandler() {
		newWorldGenTickets = new ArrayList<>();
		newTownGenTickets = new ArrayList<>();
		newStructureGenTickets = new ArrayList<>();
		chunksToGen = new ArrayList<>();
		townChunksToGen = new ArrayList<>();
		structuresToGen = new ArrayList<>();
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

	public void finalTick() {
		while (!chunksToGen.isEmpty()) {
			genChunks();
		}
		while (!structuresToGen.isEmpty()) {
			genStructures();
		}
		while (!townChunksToGen.isEmpty()) {
			genTowns();
		}
	}

	private void genChunks() {
		while (!chunksToGen.isEmpty()) {
			ChunkGenerationTicket tk = chunksToGen.remove(0);
			World world = tk.getWorld();
			if (world != null) {
				WorldStructureGenerator.INSTANCE.generateAt(tk.chunkX, tk.chunkZ, world);
			}
		}
		if (!newWorldGenTickets.isEmpty()) {
			chunksToGen.addAll(newWorldGenTickets);
			newWorldGenTickets.clear();
		}
	}

	private void genTowns() {
		if (!townChunksToGen.isEmpty()) {
			ChunkGenerationTicket tk = townChunksToGen.remove(0);
			World world = tk.getWorld();
			if (world != null) {
				WorldTownGenerator.INSTANCE.attemptGeneration(world, tk.chunkX * 16, tk.chunkZ * 16);
			}
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
		private final int world;
		private final int chunkX;
		private final int chunkZ;

		private ChunkGenerationTicket(World world, int x, int z) {
			this.world = world.provider.getDimension();
			this.chunkX = x;
			this.chunkZ = z;
		}

		@Nullable
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
	private interface StructureTicket {
		void call();
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

	public interface StructureGenerationCallbackTicket extends StructureTicket {

	}

}
