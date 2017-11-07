package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.gui.GuiChunkLoaderDeluxe;
import net.shadowmage.ancientwarfare.automation.tile.TileChunkLoaderDeluxe;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public class BlockChunkLoaderDeluxe extends BlockChunkLoaderSimple {

    protected BlockChunkLoaderDeluxe(String regName) {
        super(regName);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileChunkLoaderDeluxe();
    }

    @Override
    public void registerClient() {
        super.registerClient();

        NetworkHandler.registerGui(NetworkHandler.GUI_CHUNK_LOADER_DELUXE, GuiChunkLoaderDeluxe.class);
    }
}
