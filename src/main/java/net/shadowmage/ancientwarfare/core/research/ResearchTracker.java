package net.shadowmage.ancientwarfare.core.research;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketResearchInit;
import net.shadowmage.ancientwarfare.core.network.PacketResearchStart;
import net.shadowmage.ancientwarfare.core.network.PacketResearchUpdate;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class ResearchTracker {

    public static final ResearchTracker INSTANCE = new ResearchTracker();
    private final ResearchData clientData;
    private ResearchTracker() {
        clientData = new ResearchData("AWResearchData");
    }

    /**
     * SERVER ONLY
     */
    @SubscribeEvent
    public void playerLogInEvent(PlayerEvent.PlayerLoggedInEvent evt) {
        getResearchData(evt.player.world).onPlayerLogin(evt.player);
        PacketResearchInit init = new PacketResearchInit(getResearchData(evt.player.world));
        NetworkHandler.sendToPlayer((EntityPlayerMP) evt.player, init);
    }

    public void clearResearch(World world, String playerName) {
        if (world.isRemote) {
            clientData.clearResearchFor(playerName);
        } else {
            getResearchData(world).clearResearchFor(playerName);
            PacketResearchInit pkt = new PacketResearchInit(getResearchData(world));
            NetworkHandler.sendToAllPlayers(pkt);
        }
    }

    public void removeResearch(World world, String playerName, int research) {
        if (world.isRemote) {
            clientData.removeResearchFrom(playerName, research);
        } else {
            getResearchData(world).removeResearchFrom(playerName, research);
            PacketResearchInit pkt = new PacketResearchInit(getResearchData(world));
            NetworkHandler.sendToAllPlayers(pkt);
        }
    }

    public void fillResearch(World world, String playerName) {
        if (world.isRemote) {
            clientData.fillResearchFor(playerName);
        } else {
            getResearchData(world).fillResearchFor(playerName);
            PacketResearchInit pkt = new PacketResearchInit(getResearchData(world));
            NetworkHandler.sendToAllPlayers(pkt);
        }
    }

    public void addResearch(World world, String playerName, int research) {
        if (world.isRemote) {
            clientData.addResearchTo(playerName, research);
        } else {
            getResearchData(world).addResearchTo(playerName, research);
            PacketResearchUpdate pkt = new PacketResearchUpdate(playerName, research, true, true);
            NetworkHandler.sendToAllPlayers(pkt);
        }
    }

    /**
     * @param world
     * @param player
     * @param research
     * @return
     */
    public boolean hasPlayerCompleted(World world, String player, int research) {
        if (world.isRemote) {
            return clientData.hasPlayerCompletedResearch(player, research);
        }
        return getResearchData(world).hasPlayerCompletedResearch(player, research);
    }

    public boolean addResearchFromNotes(World world, String player, int research) {
        if (hasPlayerCompleted(world, player, research)) {
            return false;
        }
        addResearch(world, player, research);
        return true;
    }

    public boolean addProgressFromNotes(World world, String player, int research) {
        if (world.isRemote) {
            return false;
        }
        ResearchGoal goal = ResearchGoal.getGoal(research);
        return getResearchData(world).addProgress(player, goal.getTotalResearchTime() / 4);
    }

    /**
     * @param world
     * @param playerName
     * @return
     */
    public Set<Integer> getCompletedResearchFor(World world, String playerName) {
        if (world.isRemote) {
            return clientData.getResearchFor(playerName);
        }
        return getResearchData(world).getResearchFor(playerName);
    }

    public List<Integer> getResearchQueueFor(World world, String playerName) {
        if (world.isRemote) {
            return Collections.emptyList();
        }
        return getResearchData(world).getQueuedResearch(playerName);
    }

    public Set<Integer> getResearchableGoals(World world, String playerName) {
        if (world.isRemote) {
            return clientData.getResearchableGoals(playerName);
        } else {
            return getResearchData(world).getResearchableGoals(playerName);
        }
    }

    /**
     * @param world
     * @return
     */
    private ResearchData getResearchData(World world) {
        if (world.isRemote) {
            return clientData;
        }
        return AWGameData.INSTANCE.getData(world, ResearchData.class);
    }

    /**
     * CLIENT ONLY
     */
    public void onClientResearchReceived(NBTTagCompound researchDataTag) {
        this.clientData.readFromNBT(researchDataTag);
    }

    public int getCurrentGoal(World world, String playerName) {
        if (world.isRemote) {
            return clientData.getInProgressResearch(playerName);
        }
        return getResearchData(world).getInProgressResearch(playerName);
    }

    public int getProgress(World world, String playerName) {
        if (world.isRemote) {
            return clientData.getResearchProgress(playerName);
        }
        return getResearchData(world).getResearchProgress(playerName);
    }

    public void setProgress(World world, String playerName, int progress) {
        if (world.isRemote) {
            clientData.setCurrentResearchProgress(playerName, progress);
        } else {
            getResearchData(world).setCurrentResearchProgress(playerName, progress);
        }
    }

    public void removeQueuedGoal(World world, String playerName, int goal) {
        if (world.isRemote) {
            clientData.removeQueuedResearch(playerName, goal);
        } else {
            getResearchData(world).removeQueuedResearch(playerName, goal);
            PacketResearchUpdate pkt = new PacketResearchUpdate(playerName, goal, false, false);
            NetworkHandler.sendToAllPlayers(pkt);
        }
    }

    public void addQueuedGoal(World world, String playerName, int goal) {
        if (world.isRemote) {
            clientData.addQueuedResearch(playerName, goal);
        } else {
            getResearchData(world).addQueuedResearch(playerName, goal);
            PacketResearchUpdate pkt = new PacketResearchUpdate(playerName, goal, true, false);
            NetworkHandler.sendToAllPlayers(pkt);
        }
    }

    public void startResearch(World world, String playerName, int goal) {
        if (world.isRemote) {
            clientData.startResearch(playerName, goal);
        } else {
            getResearchData(world).startResearch(playerName, goal);
            PacketResearchStart pkt = new PacketResearchStart(playerName, goal, true);
            NetworkHandler.sendToAllPlayers(pkt);
        }
    }

    public void finishResearch(World world, String playerName, int goal) {
        if (world.isRemote) {
            clientData.finishResearch(playerName, goal);
        } else {
            getResearchData(world).finishResearch(playerName, goal);
            PacketResearchStart pkt = new PacketResearchStart(playerName, goal, false);
            NetworkHandler.sendToAllPlayers(pkt);
        }
    }

}
