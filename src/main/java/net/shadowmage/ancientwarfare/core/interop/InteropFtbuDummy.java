package net.shadowmage.ancientwarfare.core.interop;

import net.minecraft.entity.EntityLivingBase;
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
}
