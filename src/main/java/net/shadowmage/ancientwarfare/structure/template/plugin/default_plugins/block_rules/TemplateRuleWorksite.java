package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteBase;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

import java.util.List;

public class TemplateRuleWorksite extends TemplateRuleBlock {


    public String blockName;
    public int meta;
    public int orientation;
    BlockPosition p1, p2;
    NBTTagCompound tag;

    public TemplateRuleWorksite(World world, int x, int y, int z, Block block, int meta, int turns) {
        super(world, x, y, z, block, meta, turns);
        this.blockName = BlockDataManager.INSTANCE.getNameForBlock(block);
        this.meta = meta;
        TileWorksiteBase worksite = (TileWorksiteBase) world.getTileEntity(x, y, z);
        ForgeDirection o = worksite.getPrimaryFacing();
        for (int i = 0; i < turns; i++) {
            o = o.getRotation(ForgeDirection.UP);
        }
        this.orientation = o.ordinal();
        if (worksite.hasWorkBounds()) {
            p1 = worksite.getWorkBoundsMin().copy();
            p2 = worksite.getWorkBoundsMax().copy();
            p1.offset(-x, -y, -z);
            p2.offset(-x, -y, -z);
            BlockTools.rotateAroundOrigin(p1, turns);
            BlockTools.rotateAroundOrigin(p2, turns);
        }
        tag = new NBTTagCompound();
        worksite.writeToNBT(tag);
    }

    public TemplateRuleWorksite() {
    }

    @Override
    public boolean shouldReuseRule(World world, Block block, int meta, int turns, int x, int y, int z) {
        return false;
    }

    @Override
    public void handlePlacement(World world, int turns, int x, int y, int z, IStructureBuilder builder) {
        Block block = BlockDataManager.INSTANCE.getBlockForName(blockName);
        BlockPosition pos1, pos2;
        world.setBlock(x, y, z, block, meta, 2);
        TileWorksiteBase worksite = (TileWorksiteBase) world.getTileEntity(x, y, z);
        tag.setInteger("x", x);
        tag.setInteger("y", y);
        tag.setInteger("z", z);
        worksite.readFromNBT(tag);
        ForgeDirection o = ForgeDirection.getOrientation(orientation);
        for (int i = 0; i < turns; i++) {
            o = o.getRotation(ForgeDirection.UP);
        }
        worksite.setPrimaryFacing(o);
        if (p1 != null && p2 != null) {
            pos1 = p1.copy();
            pos2 = p2.copy();
            BlockTools.rotateAroundOrigin(pos1, turns);
            BlockTools.rotateAroundOrigin(pos2, turns);
            pos1.offset(x, y, z);
            pos2.offset(x, y, z);
            worksite.setBounds(pos1, pos2);
        }
        world.markBlockForUpdate(x, y, z);
    }

    @Override
    public void parseRuleData(NBTTagCompound tag) {
        this.blockName = tag.getString("blockId");
        this.meta = tag.getInteger("meta");
        this.orientation = tag.getInteger("orientation");
        if (tag.hasKey("teData")) {
            this.tag = tag.getCompoundTag("teData");
        }
        if (tag.hasKey("pos1")) {
            this.p1 = new BlockPosition(tag.getCompoundTag("pos1"));
        }
        if (tag.hasKey("pos2")) {
            this.p2 = new BlockPosition(tag.getCompoundTag("pos2"));
        }
    }

    @Override
    public void writeRuleData(NBTTagCompound tag) {
        tag.setString("blockId", blockName);
        tag.setInteger("meta", meta);
        tag.setInteger("orientation", orientation);
        if (p1 != null) {
            tag.setTag("pos1", p1.writeToNBT(new NBTTagCompound()));
        }
        if (p2 != null) {
            tag.setTag("pos2", p2.writeToNBT(new NBTTagCompound()));
        }
        if (this.tag != null) {
            tag.setTag("teData", this.tag);
        }
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
