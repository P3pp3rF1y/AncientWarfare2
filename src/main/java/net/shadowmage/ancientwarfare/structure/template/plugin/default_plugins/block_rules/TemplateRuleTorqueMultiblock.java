package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

import java.util.List;

public class TemplateRuleTorqueMultiblock extends TemplateRuleBlock {

    int meta;
    String blockName;
    NBTTagCompound tag;

    public TemplateRuleTorqueMultiblock(World world, int x, int y, int z, Block block, int meta, int turns) {
        super(world, x, y, z, block, meta, turns);
        this.blockName = BlockDataManager.INSTANCE.getNameForBlock(block);
        this.meta = meta;
        this.tag = new NBTTagCompound();
        TileEntity tile = world.getTileEntity(x, y, z);
        tile.writeToNBT(tag);
    }

    public TemplateRuleTorqueMultiblock() {
    }

    @Override
    public boolean shouldReuseRule(World world, Block block, int meta, int turns, int x, int y, int z) {
        return false;
    }

    @Override
    public void handlePlacement(World world, int turns, int x, int y, int z, IStructureBuilder builder) {
        Block block = BlockDataManager.INSTANCE.getBlockForName(blockName);
        if(world.setBlock(x, y, z, block, meta, 3)) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if(tile != null) {
                tag.setInteger("x", x);
                tag.setInteger("y", y);
                tag.setInteger("z", z);
                tile.readFromNBT(tag);
            }
            world.markBlockForUpdate(x, y, z);
            block.onPostBlockPlaced(world, x, y, z, meta);
        }
    }

    @Override
    public void parseRuleData(NBTTagCompound tag) {
        blockName = tag.getString("blockId");
        meta = tag.getInteger("meta");
        this.tag = tag.getCompoundTag("teData");
    }

    @Override
    public void writeRuleData(NBTTagCompound tag) {
        tag.setString("blockId", blockName);
        tag.setInteger("meta", meta);
        tag.setTag("teData", this.tag);
    }

    @Override
    public void addResources(List<ItemStack> resources) {
        resources.add(new ItemStack(Item.getItemFromBlock(BlockDataManager.INSTANCE.getBlockForName(blockName)), 1, meta));
    }

    @Override
    public boolean shouldPlaceOnBuildPass(World world, int turns, int x, int y, int z, int buildPass) {
        return buildPass == 0;
    }
}
