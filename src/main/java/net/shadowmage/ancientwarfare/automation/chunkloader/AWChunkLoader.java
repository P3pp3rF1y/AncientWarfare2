package net.shadowmage.ancientwarfare.automation.chunkloader;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.shadowmage.ancientwarfare.core.interfaces.IChunkLoaderTile;

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
      if(te instanceof IChunkLoaderTile)
        {
        ((IChunkLoaderTile)te).setTicket(tk);
        }
      }
    }
  }

public static void writeDataToTicket(Ticket tk, int x, int y, int z)
  {
  NBTTagCompound posTag = new NBTTagCompound();
  posTag.setInteger("x", x);
  posTag.setInteger("y", y);
  posTag.setInteger("z", z);
  tk.getModData().setTag("tilePosition", posTag);
  }

}
