package net.shadowmage.ancientwarfare.core.interop;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class InteropFtbuDummy implements InteropFtbuInterface {

    @Override
    public boolean areFriends(String player1, String player2) { return false; }

    @Override
    public void claimChunks(World world, String ownerName, int posX, int posY, int posZ) {}
    
    @Override
    public void claimChunks(World world, EntityLivingBase placer, int posX, int posY, int posZ) {}

    @Override
    public void unclaimChunks(World world, String ownerName, int posX, int posY, int posZ) {}

    @Override
    public void notifyPlayer(EnumChatFormatting titleColor, String ownerName, String title, IChatComponent msg, List<IChatComponent> hoverTextLines) {}
    
    @Override
    public IChatComponent chatComponent(String s, Object... obj) { 
        return new ChatComponentTranslation(s, obj);
    }
}
