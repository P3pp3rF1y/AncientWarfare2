package net.shadowmage.ancientwarfare.npc;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.shadowmage.ancientwarfare.npc.block.AWNPCBlockLoader;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

import java.util.ArrayList;
import java.util.List;

public class AW2NpcChunkloaderCallback implements ForgeChunkManager.OrderedLoadingCallback {

    @Override
    public void ticketsLoaded(List<Ticket> tickets, World world) {
        for (Ticket ticket: tickets) {
            int blockX = ticket.getModData().getInteger("blockX");
            int blockY = ticket.getModData().getInteger("blockY");
            int blockZ = ticket.getModData().getInteger("blockZ");
            TileTownHall te = (TileTownHall) world.getTileEntity(blockX, blockY, blockZ);
            te.loadTicket(ticket);
        }
    }

    @Override
    public List<Ticket> ticketsLoaded(List<Ticket> tickets, World world, int maxTicketCount) {
        List<Ticket> validTickets = new ArrayList<>();
        for(Ticket ticket: tickets) {
            int blockX = ticket.getModData().getInteger("blockX");
            int blockY = ticket.getModData().getInteger("blockY");
            int blockZ = ticket.getModData().getInteger("blockZ");
            Block block = world.getBlock(blockX, blockY, blockZ);
            if(block == AWNPCBlockLoader.townHall)
                validTickets.add(ticket);
        }
        return validTickets;
    }
}
