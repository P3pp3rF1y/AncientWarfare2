package net.shadowmage.ancientwarfare.core.interop;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class InteropTreecapitator extends InteropTreecapitatorDummy {
    
    @Override
    public void doTreecapitate(World world, Block block, int meta, int posX, int posY, int posZ) {
        FakePlayer fakePlayer = FakePlayerFactory.get(MinecraftServer.getServer().worldServerForDimension(world.provider.dimensionId), AncientWarfareCore.gameProfile);
        fakePlayer.inventory.setInventorySlotContents(0, new ItemStack(Items.diamond_axe));
        fakePlayer.inventory.changeCurrentItem(0);
        new bspkrs.treecapitator.forge.ForgeEventHandler().onBlockHarvested(new BreakEvent(posX, posY, posZ, world, block, meta, fakePlayer));
    }
}
