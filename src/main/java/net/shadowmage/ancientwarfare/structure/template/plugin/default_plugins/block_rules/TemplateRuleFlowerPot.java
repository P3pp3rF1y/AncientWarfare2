package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;

public class TemplateRuleFlowerPot extends TemplateRuleVanillaBlocks {

    String itemName;
    int itemMeta;

    public TemplateRuleFlowerPot(World world, BlockPos pos, Block block, int meta, int turns) {
        super(world, pos, block, meta, turns);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityFlowerPot) {
            TileEntityFlowerPot tefp = (TileEntityFlowerPot) te;
            Item item = tefp.getFlowerPotItem();
            itemMeta = tefp.getFlowerPotData();
            if (item != null) {
                itemName = item.getRegistryName().toString();
            }
        }
    }

    public TemplateRuleFlowerPot() {
    }

    @Override
    public boolean shouldReuseRule(World world, Block block, int meta, int turns, BlockPos pos) {
        return false;
    }

    @Override
    public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
        super.handlePlacement(world, turns, pos, builder);
        if (itemName != null) {
            Item item = Item.REGISTRY.getObject(new ResourceLocation(itemName));
            TileEntity te = world.getTileEntity(pos);
            if (item != null && te instanceof TileEntityFlowerPot) {
                TileEntityFlowerPot tefp = (TileEntityFlowerPot) te;
                tefp.setItemStack(new ItemStack(item, 1, itemMeta));
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
