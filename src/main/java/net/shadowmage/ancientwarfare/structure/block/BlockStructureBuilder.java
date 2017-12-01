package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.shadowmage.ancientwarfare.structure.render.RenderStructureBuilder;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureBuilder;

public class BlockStructureBuilder extends BlockBaseStructure {

    public BlockStructureBuilder() {
        super(Material.ROCK, "structure_builder_ticked");
        setHardness(2.f);
        setCreativeTab(null);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileStructureBuilder();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileStructureBuilder) {
                ((TileStructureBuilder) te).onBlockBroken();
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileStructureBuilder) {
                TileStructureBuilder builder = (TileStructureBuilder) te;
                builder.onBlockClicked(player);
            }
        }
        return true;
    }

    @Override
    public void registerClient() {
        super.registerClient();

        ClientRegistry.bindTileEntitySpecialRenderer(TileStructureBuilder.class, new RenderStructureBuilder());
    }
}
