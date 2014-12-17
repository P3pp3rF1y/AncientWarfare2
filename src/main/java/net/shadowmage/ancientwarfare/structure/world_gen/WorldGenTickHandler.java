package net.shadowmage.ancientwarfare.structure.world_gen;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;
import net.shadowmage.ancientwarfare.structure.town.WorldTownGenerator;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class WorldGenTickHandler
{

private WorldGenTickHandler(){}
private static WorldGenTickHandler INSTANCE = new WorldGenTickHandler();
public static WorldGenTickHandler instance(){return INSTANCE;}

private List<ChunkGenerationTicket> newWorldGenTickets = new ArrayList<ChunkGenerationTicket>();
private List<ChunkGenerationTicket> newTownGenTickets = new ArrayList<ChunkGenerationTicket>();
private List<StructureGenerationTicket> newStructureGenTickets = new ArrayList<StructureGenerationTicket>();

private List<ChunkGenerationTicket> chunksToGen = new ArrayList<ChunkGenerationTicket>();
private List<ChunkGenerationTicket> townChunksToGen = new ArrayList<ChunkGenerationTicket>();
private List<StructureGenerationTicket> structuresToGen = new ArrayList<StructureGenerationTicket>();

public void addChunkForGeneration(World world, int chunkX, int chunkZ)
  {
  newWorldGenTickets.add(new ChunkGenerationTicket(world, chunkX, chunkZ));
  }

public void addChunkForTownGeneration(World world, int chunkX, int chunkZ)
  {
  newTownGenTickets.add(new ChunkGenerationTicket(world, chunkX, chunkZ));
  }

public void addStructureForGeneration(StructureBuilder builder)
  {
  newStructureGenTickets.add(new StructureGenerationTicket(builder));
  }

@SubscribeEvent
public void serverTick(ServerTickEvent evt)
  {
  if(evt.phase==Phase.END)
    {
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

private void genChunks()
  {
  if(!chunksToGen.isEmpty())
    {
    ChunkGenerationTicket tk = chunksToGen.remove(0);
    WorldStructureGenerator.instance().generateAt(tk.chunkX, tk.chunkZ, tk.world);
    }
  }

private void genTowns()
  {
  if(!townChunksToGen.isEmpty())
    {
    ChunkGenerationTicket tk = townChunksToGen.remove(0);
    WorldTownGenerator.instance().attemptGeneration(tk.world, tk.world.rand, tk.chunkX*16, tk.chunkZ*16);
    }
  }

private void genStructures()
  {
  if(!structuresToGen.isEmpty())
    {
    StructureGenerationTicket tk = structuresToGen.remove(0);    
    StructureBuilder b = tk.builder;
    b.instantConstruction();
    }
  }

private static class ChunkGenerationTicket
{
World world;
int chunkX, chunkZ;
public ChunkGenerationTicket(World world, int x, int z)
  {
  this.world = world;
  this.chunkX = x;
  this.chunkZ = z;
  }
}

private static class StructureGenerationTicket
{
StructureBuilder builder;
public StructureGenerationTicket(StructureBuilder builder)
  {
  this.builder = builder;
  }
}

}
