/* TODO implement integration with the new treecapitator port ??
package net.shadowmage.ancientwarfare.core.interop;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class InteropTreecapitator extends InteropTreecapitatorDummy {
    
    @Override
    public void doTreecapitate(WorldServer world, Block block, int meta, int posX, int posY, int posZ) {
        FakePlayer fakePlayer = FakePlayerFactory.get(world, AncientWarfareCore.gameProfile);
        fakePlayer.inventory.setInventorySlotContents(0, new ItemStack(Items.DIAMOND_AXE));
        new bspkrs.treecapitator.forge.ForgeEventHandler().onBlockHarvested(new BreakEvent(posX, posY, posZ, world, block, meta, fakePlayer));
    }
}
*/
