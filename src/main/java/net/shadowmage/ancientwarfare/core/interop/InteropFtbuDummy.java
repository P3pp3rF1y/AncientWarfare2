package net.shadowmage.ancientwarfare.core.interop;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.gamedata.ChunkClaimWorkerThread;

public class InteropFtbuDummy implements InteropFtbuInterface {
    @Override
    public boolean areFriends(String player1, String player2) { return false; }
    
    @Override
    public boolean isFriendOfClient(UUID otherPlayer) { return false; };

    @Override
    public void addClaim(World world, String ownerName, int posX, int posY, int posZ) {}
    
    @Override
    public void addClaim(World world, EntityLivingBase placer, int posX, int posY, int posZ) {}

    //@Override
    //public void unclaimChunks(World world, String ownerName, int posX, int posY, int posZ) {}

    @Override
    public void notifyPlayer(EnumChatFormatting titleColor, String ownerName, String title, ChatComponentTranslation msg, List<ChatComponentTranslation> hoverTextLines) {}
    
    @Override
    public void startWorkerThread() {};
    
    @Override
    public void stopWorkerThread() {};
}
