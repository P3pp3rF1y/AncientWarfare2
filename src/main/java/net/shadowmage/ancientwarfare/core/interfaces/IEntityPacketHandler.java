package net.shadowmage.ancientwarfare.core.interfaces;

/**
 * blind entity packet handling
 * should be implemented by any entity that is a target of
 * network packets
 * @author Shadowmage
 */
public interface IEntityPacketHandler
{

public void handlePacketData(Object datas);//TODO determine best way to handle data input

}
