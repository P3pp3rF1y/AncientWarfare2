package net.shadowmage.ancientwarfare.core.interop;

import ftb.utils.world.LMPlayerServer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public interface InteropFtbuInterface {
    public boolean areFriends(String player1, String player2);
    public boolean isFriendOfClient(UUID otherPlayer);
    //public void claimChunks(EntityLivingBase placer, int posX, int posY, int posZ);
    public void addClaim(World world, String ownerName, int posX, int posY, int posZ);
    public void addClaim(World world, EntityLivingBase placer, int posX, int posY, int posZ);
    //public void unclaimChunks(World world, String ownerName, int posX, int posY, int posZ);
    public void notifyPlayer(EnumChatFormatting titleColor, String ownerName, String title, TextComponentTranslation msg, List<TextComponentTranslation> hoverTextLines);
    public LMPlayerServer getChunkClaimOwner(int dimId, int chunkX, int chunkY);
    
    public void startWorkerThread();
    public void stopWorkerThread();
}
