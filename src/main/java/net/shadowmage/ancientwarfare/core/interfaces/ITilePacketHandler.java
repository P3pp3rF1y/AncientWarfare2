package net.shadowmage.ancientwarfare.core.interfaces;

/**
 * any tile entities that are targets of network packets should implement this interface
 * for packet comms
 * @author Shadowmage
 */
public interface ITilePacketHandler
{

public void handlePacketData(Object data);

}
