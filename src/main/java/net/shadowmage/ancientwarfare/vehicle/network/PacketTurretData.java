package net.shadowmage.ancientwarfare.vehicle.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

/**
 * Turreted vehicles will use this to relay turret values from client-> server-> other_clients
 * @author Shadowmage
 */
public class PacketTurretData extends PacketBase
{
int entityId;
short value;

public PacketTurretData(){}//reflection constructor

public PacketTurretData(int id, short value)
  {
  entityId = id;
  this.value = value;
  }

@Override
protected void writeToStream(ByteBuf data)
  {
  data.writeInt(entityId);
  data.writeShort(value);
  }

@Override
protected void readFromStream(ByteBuf data)
  {
  entityId = data.readInt();
  value = data.readShort();
  }

@Override
protected void execute()
  {
  Entity e = player.worldObj.getEntityByID(entityId);
  if( e instanceof VehicleBase)
    {
    
    //((VehicleBase)e)
    }
  }

}
