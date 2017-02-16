package net.shadowmage.ancientwarfare.core.interop;

import java.util.List;
import java.util.UUID;

import ftb.lib.FTBLib;
import ftb.lib.api.notification.MouseAction;
import ftb.lib.api.notification.Notification;
import ftb.utils.api.FriendsAPI;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.LMWorldServer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.gamedata.ChunkClaimWorkerThread;
import net.shadowmage.ancientwarfare.core.gamedata.ChunkClaims;
import net.shadowmage.ancientwarfare.core.gamedata.ChunkClaims.ChunkClaimInfo;
import net.shadowmage.ancientwarfare.core.gamedata.ChunkClaims.TownHallEntry;

public class InteropFtbu extends InteropFtbuDummy {
    
    public static final ChunkClaimWorkerThread WORKER_THREAD = new ChunkClaimWorkerThread();
    
    @Override
    public boolean areFriends(String player1, String player2) {
        return FriendsAPI.areFriends(player1, player2);
    }
    
    public boolean isFriendOfClient(UUID otherPlayer) { 
        return FriendsAPI.isClientFriend(otherPlayer);
    }
    
    @Override
    public void addClaim(World world, EntityLivingBase placer, int posX, int posY, int posZ) {
        addClaim(world, placer.getCommandSenderName(), posX, posY, posZ);
    }
    
    @Override
    public void addClaim(World world, String ownerName, int posX, int posY, int posZ) {
        LMPlayerServer p = LMWorldServer.inst.getPlayer(ownerName);
        if (p != null) {
            Chunk newChunkClaim = world.getChunkFromBlockCoords(posX, posZ);
            ChunkClaimInfo newChunkClaimInfo = new ChunkClaimInfo(newChunkClaim.xPosition, newChunkClaim.zPosition, world.provider.dimensionId);
            ChunkClaims.get(world).addTownHallEntry(newChunkClaimInfo, new TownHallEntry(ownerName, posX, posY, posZ));
        } else {
            AncientWarfareCore.log.error("A chunk claim action was requested at " + posX + "x" + posY + "x" + posZ + " but the player name '" + ownerName + "' could not be found...");
        }
    }
    
    @Override
    public void notifyPlayer(EnumChatFormatting titleColor, String ownerName, String title, ChatComponentTranslation msg, List<ChatComponentTranslation> hoverTextLines) {
        if (ownerName.isEmpty() || LMWorldServer.inst == null)
            return;
        
        LMPlayerServer p = LMWorldServer.inst.getPlayer(ownerName);
        
        if (p != null) {
            IChatComponent cc = new ChatComponentTranslation(title);
            cc.getChatStyle().setColor(titleColor);
            Notification n = new Notification("claim_change" + p.world.getMCWorld().getTotalWorldTime(), cc, 6000);
            n.setDesc(msg);
            
            MouseAction mouse = new MouseAction();
            for(IChatComponent hoverTextLine : hoverTextLines)
                mouse.hover.add(hoverTextLine);
            n.setMouseAction(mouse);
            
            if (p.getPlayer() != null)
                FTBLib.notifyPlayer(p.getPlayer(), n);
        } else {
            // TODO
            System.out.println("Target player is not online!");
        }
    }

    @Override
    public void startWorkerThread() {
        WORKER_THREAD.enable();
    };
    
    @Override
    public void stopWorkerThread() {
        WORKER_THREAD.disable();
    };
    
}
