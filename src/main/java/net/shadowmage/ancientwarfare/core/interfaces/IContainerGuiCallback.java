package net.shadowmage.ancientwarfare.core.interfaces;

/**
 * proxy interface to allow for advanced gui interaction from containers
 * base gui class should implement this interface
 * @author Shadowmage
 *
 */
public interface IContainerGuiCallback
{

public void refreshGui();
public void handlePacketData(Object data);

}
