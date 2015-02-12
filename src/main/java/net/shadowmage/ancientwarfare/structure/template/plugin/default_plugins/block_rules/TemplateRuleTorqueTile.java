package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueBase;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

import java.util.List;

public class TemplateRuleTorqueTile extends TemplateRuleBlock {

    int orientation;
    int meta;
    String blockName;
    NBTTagCompound tag;

    public TemplateRuleTorqueTile(World world, int x, int y, int z, Block block, int meta, int turns) {
        super(world, x, y, z, block, meta, turns);
        TileTorqueBase tile = (TileTorqueBase) world.getTileEntity(x, y, z);
        this.blockName = BlockDataManager.instance().getNameForBlock(block);
        this.meta = meta;
        ForgeDirection o = tile.getPrimaryFacing();
        for (int i = 0; i < turns; i++) {
            o = o.getRotation(ForgeDirection.UP);
        }
        this.orientation = o.ordinal();
        this.tag = new NBTTagCompound();
        tile.writeToNBT(tag);
    }

    public TemplateRuleTorqueTile() {
    }

    @Override
    public boolean shouldReuseRule(World world, Block block, int meta, int turns, TileEntity te, int x, int y, int z) {
        return false;
    }

    @Override
    public void handlePlacement(World world, int turns, int x, int y, int z, IStructureBuilder builder) {
        Block block = BlockDataManager.instance().getBlockForName(blockName);
        world.setBlock(x, y, z, block, meta, 2);
        TileTorqueBase tile = (TileTorqueBase) world.getTileEntity(x, y, z);
        tag.setInteger("x", x);
        tag.setInteger("y", y);
        tag.setInteger("z", z);
        tile.readFromNBT(tag);
        ForgeDirection o = ForgeDirection.getOrientation(orientation);
        for (int i = 0; i < turns; i++) {
            o = o.getRotation(ForgeDirection.UP);
        }
        tile.setPrimaryFacing(o);
        world.markBlockForUpdate(x, y, z);
    }

    @Override
    public void parseRuleData(NBTTagCompound tag) {
        blockName = tag.getString("blockId");
        meta = tag.getInteger("meta");
        orientation = tag.getInteger("orientation");
        this.tag = tag.getCompoundTag("teData");
    }

    @Override
    public void writeRuleData(NBTTagCompound tag) {
        tag.setString("blockId", blockName);
        tag.setInteger("meta", meta);
        tag.setInteger("orientation", orientation);
        tag.setTag("teData", this.tag);
    }

    @Override
    public void addResources(List<ItemStack> resources) {
        resources.add(new ItemStack(Item.getItemFromBlock(BlockDataManager.instance().getBlockForName(blockName)), 1, meta));
    }

    @Override
    public boolean shouldPlaceOnBuildPass(World world, int turns, int x, int y, int z, int buildPass) {
        return buildPass == 0;
    }

}
