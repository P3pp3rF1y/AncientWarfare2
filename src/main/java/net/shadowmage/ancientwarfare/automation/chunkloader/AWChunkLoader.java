package net.shadowmage.ancientwarfare.automation.chunkloader;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.shadowmage.ancientwarfare.automation.tile.TileChunkLoaderSimple;

public class AWChunkLoader implements LoadingCallback
{

public AWChunkLoader()
  {
  
  }

@Override
public void ticketsLoaded(List<Ticket> tickets, World world)
  {
  for(Ticket tk : tickets)
    {
    if(tk.getModData().hasKey("tilePosition"))
      {
      NBTTagCompound posTag = tk.getModData().getCompoundTag("tilePosition");
      int x = posTag.getInteger("x");
      int y = posTag.getInteger("y");
      int z = posTag.getInteger("z");
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileChunkLoaderSimple)
        {
        ((TileChunkLoaderSimple)te).setTicketFromCallback(tk);
        }
      }
    }
  }

}
