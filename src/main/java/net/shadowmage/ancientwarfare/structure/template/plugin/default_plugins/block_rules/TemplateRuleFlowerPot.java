package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;

public class TemplateRuleFlowerPot extends TemplateRuleVanillaBlocks {

    String itemName;
    int itemMeta;

    public TemplateRuleFlowerPot(World world, int x, int y, int z, Block block, int meta, int turns) {
        super(world, x, y, z, block, meta, turns);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityFlowerPot) {
            TileEntityFlowerPot tefp = (TileEntityFlowerPot) te;
            Item item = tefp.getFlowerPotItem();
            itemMeta = tefp.getFlowerPotData();
            if (item != null) {
                itemName = Item.itemRegistry.getNameForObject(item);
            }
        }
    }

    public TemplateRuleFlowerPot() {
    }

    @Override
    public boolean shouldReuseRule(World world, Block block, int meta, int turns, TileEntity te, int x, int y, int z) {
        return false;
    }

    @Override
    public void handlePlacement(World world, int turns, int x, int y, int z, IStructureBuilder builder) {
        super.handlePlacement(world, turns, x, y, z, builder);
        if (itemName != null) {
            Item item = (Item) Item.itemRegistry.getObject(itemName);
            TileEntity te = world.getTileEntity(x, y, z);
            if (item != null && te instanceof TileEntityFlowerPot) {
                TileEntityFlowerPot tefp = (TileEntityFlowerPot) te;
                tefp.func_145964_a(item, itemMeta);
            }
        }
    }

    @Override
    public void writeRuleData(NBTTagCompound tag) {
        super.writeRuleData(tag);
        if (itemName != null) {
            tag.setString("itemName", itemName);
        }
        tag.setInteger("itemMeta", itemMeta);
    }

    @Override
    public void parseRuleData(NBTTagCompound tag) {
        super.parseRuleData(tag);
        if (tag.hasKey("itemName")) {
            itemName = tag.getString("itemName");
        }
        itemMeta = tag.getInteger("itemMeta");
    }
}
