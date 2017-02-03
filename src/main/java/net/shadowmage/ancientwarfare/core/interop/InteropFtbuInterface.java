package net.shadowmage.ancientwarfare.core.interop;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public interface InteropFtbuInterface {
    public boolean areFriends(String player1, String player2);
    //public void claimChunks(EntityLivingBase placer, int posX, int posY, int posZ);
    public void claimChunks(World world, String ownerName, int posX, int posY, int posZ);
    public void claimChunks(World world, EntityLivingBase placer, int posX, int posY, int posZ);
    public void unclaimChunks(World world, String ownerName, int posX, int posY, int posZ);
    public void notifyPlayer(EnumChatFormatting titleColor, String ownerName, String title, ChatComponentTranslation msg, List<ChatComponentTranslation> hoverTextLines);
}
