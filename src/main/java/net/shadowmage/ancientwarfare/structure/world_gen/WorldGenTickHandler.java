package net.shadowmage.ancientwarfare.structure.world_gen;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class WorldGenTickHandler
{

private WorldGenTickHandler(){}
private static WorldGenTickHandler INSTANCE = new WorldGenTickHandler();
public static WorldGenTickHandler instance(){return INSTANCE;}

private List<ChunkGenerationTicket> newChunkGenTickets = new ArrayList<ChunkGenerationTicket>();
private List<StructureGenerationTicket> newStructureGenTickets = new ArrayList<StructureGenerationTicket>();

private List<ChunkGenerationTicket> chunksToGen = new ArrayList<ChunkGenerationTicket>();
private List<StructureGenerationTicket> structuresToGen = new ArrayList<StructureGenerationTicket>();

public void addChunkForGeneration(World world, int chunkX, int chunkZ)
  {
  newChunkGenTickets.add(new ChunkGenerationTicket(world, chunkX, chunkZ));
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
    chunksToGen.addAll(newChunkGenTickets);
    newChunkGenTickets.clear();
    structuresToGen.addAll(newStructureGenTickets);
    newStructureGenTickets.clear();
    }
  }

private void genChunks()
  {
  for(ChunkGenerationTicket tk : chunksToGen)
    {
    WorldStructureGenerator.instance().generateAt(tk.chunkX, tk.chunkZ, tk.world);
    }
  chunksToGen.clear();
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
