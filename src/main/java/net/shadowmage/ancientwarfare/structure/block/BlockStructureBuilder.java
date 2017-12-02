package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateClient;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManagerClient;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureBuilder;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BlockStructureBuilder extends BlockBaseStructure {

    List<ItemStack> displayCache = null;

    public BlockStructureBuilder() {
        super(Material.ROCK, "structure_builder_ticked");
        setHardness(2.f);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab != AWStructuresItemLoader.structureTab) {
            return;
        }

        if (displayCache == null || displayCache.isEmpty()) {
            displayCache = new ArrayList<>();
            List<StructureTemplateClient> templates = StructureTemplateManagerClient.instance().getSurvivalStructures();
            @Nonnull ItemStack item;
            for (StructureTemplateClient t : templates) {
                item = new ItemStack(this);
                item.setTagInfo("structureName", new NBTTagString(t.name));
                displayCache.add(item);
            }
        }
        if (!displayCache.isEmpty()) {
            items.addAll(displayCache);
        }
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
}
