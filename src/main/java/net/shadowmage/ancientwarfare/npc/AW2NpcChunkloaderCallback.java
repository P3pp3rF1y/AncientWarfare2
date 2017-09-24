package net.shadowmage.ancientwarfare.npc;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.shadowmage.ancientwarfare.npc.block.AWNPCBlocks;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

import java.util.ArrayList;
import java.util.List;

public class AW2NpcChunkloaderCallback implements ForgeChunkManager.OrderedLoadingCallback {

    @Override
    public void ticketsLoaded(List<Ticket> tickets, World world) {
        for (Ticket ticket: tickets) {
            BlockPos pos = BlockPos.fromLong(ticket.getModData().getLong("pos"));
            TileTownHall te = (TileTownHall) world.getTileEntity(pos);
            te.loadTicket(ticket);
        }
    }

    @Override
    public List<Ticket> ticketsLoaded(List<Ticket> tickets, World world, int maxTicketCount) {
        List<Ticket> validTickets = new ArrayList<>();
        for(Ticket ticket: tickets) {
            BlockPos pos = BlockPos.fromLong(ticket.getModData().getLong("pos"));
            Block block = world.getBlockState(pos).getBlock();
            if(block == AWNPCBlocks.townHall)
                validTickets.add(ticket);
        }
        return validTickets;
    }
}
