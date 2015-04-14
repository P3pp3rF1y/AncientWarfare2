package net.shadowmage.ancientwarfare.structure.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.event.IBoxRenderer;

import java.util.List;
import java.util.Set;

public class ItemConstructionTool extends Item implements IItemClickable, IItemKeyInterface, IBoxRenderer {

    public ItemConstructionTool(String regName) {
        this.setUnlocalizedName(regName);
        this.setTextureName("ancientwarfare:structure/" + regName);
        this.setCreativeTab(AWStructuresItemLoader.structureTab);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean wtf) {
        ConstructionSettings settings = getSettings(stack);
        if (settings == null) {
            return;
        }
        String text = StatCollector.translateToLocal("guistrings.construction.mode") + ": " + settings.type;
        list.add(text);
        text = StatCollector.translateToLocal("guistrings.construction.fill_block") + ": " + Block.blockRegistry.getNameForObject(settings.block);
        list.add(text);

        String keyText;
        text = "RMB" + " = " + StatCollector.translateToLocal("guistrings.construction.do_action");
        list.add(text);

        keyText = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_0);
        text = keyText + " = " + StatCollector.translateToLocal("guistrings.construction.toggle_mode");
        list.add(text);

        keyText = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_1);
        text = keyText + " = " + StatCollector.translateToLocal("guistrings.construction.set_fill_block");
        list.add(text);

        keyText = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_2);
        text = keyText + " = " + StatCollector.translateToLocal("guistrings.construction.set_pos_1");
        list.add(text);

        keyText = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_3);
        text = keyText + " = " + StatCollector.translateToLocal("guistrings.construction.set_pos_2");
        list.add(text);

        keyText = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_4);
        text = keyText + " = " + StatCollector.translateToLocal("guistrings.construction.clear_positions");
        list.add(text);
    }

    @Override
    public boolean cancelRightClick(EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public boolean cancelLeftClick(EntityPlayer player, ItemStack stack) {
        return false;
    }

    @Override
    public boolean onLeftClickClient(EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public void onLeftClick(EntityPlayer player, ItemStack stack) {
    }

    @Override
    public boolean onRightClickClient(EntityPlayer player, ItemStack stack) {
        return true;
    }//return true for send packet to act on server-side

    @Override
    public void onRightClick(EntityPlayer player, ItemStack stack) {
        ConstructionSettings settings = getSettings(stack);
        switch (settings.type) {
            case SOLID_FILL: {
                handleSolidFill(player, settings);
            }
            break;
            case BOX_FILL: {
                handleBoxFill(player, settings);
            }
            break;
            case LAKE_FILL: {
                handleLakeFill(player, settings);
            }
            break;
            case LAYER_FILL: {
                handleLayerFill(player, settings);
            }
            break;
        }
    }

    private void handleSolidFill(EntityPlayer player, ConstructionSettings settings) {
        if (settings.pos1 != null && settings.pos2 != null && settings.block != null) {
            BlockPosition min = BlockTools.getMin(settings.pos1, settings.pos2);
            BlockPosition max = BlockTools.getMax(settings.pos1, settings.pos2);
            for (int x = min.x; x <= max.x; x++) {
                for (int z = min.z; z <= max.z; z++) {
                    for (int y = min.y; y <= max.y; y++) {
                        player.worldObj.setBlock(x, y, z, settings.block, settings.meta, 3);
                    }
                }
            }
        }
    }

    private void handleBoxFill(EntityPlayer player, ConstructionSettings settings) {
        if (settings.pos1 != null && settings.pos2 != null && settings.block != null) {
            BlockPosition min = BlockTools.getMin(settings.pos1, settings.pos2);
            BlockPosition max = BlockTools.getMax(settings.pos1, settings.pos2);

            for (int x = min.x; x <= max.x; x++) {
                for (int z = min.z; z <= max.z; z++) {
                    player.worldObj.setBlock(x, max.y, z, settings.block, settings.meta, 3);
                    player.worldObj.setBlock(x, min.y, z, settings.block, settings.meta, 3);
                }
            }
            for (int x = min.x; x <= max.x; x++) {
                for (int y = min.y; y <= max.y; y++) {
                    player.worldObj.setBlock(x, y, min.z, settings.block, settings.meta, 3);
                    player.worldObj.setBlock(x, y, max.z, settings.block, settings.meta, 3);
                }
            }
            for (int z = min.z; z <= max.z; z++) {
                for (int y = min.y; y <= max.y; y++) {
                    player.worldObj.setBlock(min.x, y, z, settings.block, settings.meta, 3);
                    player.worldObj.setBlock(max.x, y, z, settings.block, settings.meta, 3);
                }
            }
        }
    }

    private void handleLakeFill(EntityPlayer player, ConstructionSettings settings) {
        if (settings.block != null) {
            BlockPosition pos = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
            if (pos != null) {
                Block block = player.worldObj.getBlock(pos.x, pos.y, pos.z);
                int meta = player.worldObj.getBlockMetadata(pos.x, pos.y, pos.z);
                Set<BlockPosition> toFill = new FloodFillPathfinder(player.worldObj, pos.x, pos.y, pos.z, block, meta, false, true).doFloodFill();
                for (BlockPosition p1 : toFill) {
                    player.worldObj.setBlock(p1.x, p1.y, p1.z, settings.block, settings.meta, 3);
                }
            }
        }
    }

    private void handleLayerFill(EntityPlayer player, ConstructionSettings settings) {
        if (settings.block != null) {
            BlockPosition pos = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
            if (pos != null) {
                Block block = player.worldObj.getBlock(pos.x, pos.y, pos.z);
                int meta = player.worldObj.getBlockMetadata(pos.x, pos.y, pos.z);
                Set<BlockPosition> toFill = new FloodFillPathfinder(player.worldObj, pos.x, pos.y, pos.z, block, meta, false, false).doFloodFill();
                for (BlockPosition p1 : toFill) {
                    player.worldObj.setBlock(p1.x, p1.y, p1.z, settings.block, settings.meta, 3);
                }
            }
        }
    }

    @Override
    public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key) {
        return true;
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        switch (key) {
            case KEY_0://toggle mode
            {
                ConstructionSettings settings = getSettings(stack);
                settings.type = settings.type.next();
                writeConstructionSettings(stack, settings);
            }
            break;
            case KEY_1://source block
            {
                BlockPosition pos = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
                if (pos != null) {
                    ConstructionSettings settings = getSettings(stack);
                    settings.block = player.worldObj.getBlock(pos.x, pos.y, pos.z);
                    settings.meta = player.worldObj.getBlockMetadata(pos.x, pos.y, pos.z);
                    writeConstructionSettings(stack, settings);
                }
            }
            break;
            case KEY_2://pos1
            {
                BlockPosition pos = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
                if (pos != null) {
                    ConstructionSettings settings = getSettings(stack);
                    settings.pos1 = pos;
                    writeConstructionSettings(stack, settings);
                }
            }
            break;
            case KEY_3://pos2
            {
                BlockPosition pos = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
                if (pos != null) {
                    ConstructionSettings settings = getSettings(stack);
                    settings.pos2 = pos;
                    writeConstructionSettings(stack, settings);
                }
            }
            break;
            case KEY_4://clear pos
            {
                ConstructionSettings settings = getSettings(stack);
                settings.pos1 = null;
                settings.pos2 = null;
                writeConstructionSettings(stack, settings);
            }
            break;
        }
    }

    public static ConstructionSettings getSettings(ItemStack item) {
        if (item.getItem() instanceof ItemConstructionTool) {
            ConstructionSettings settings = new ConstructionSettings();
            if (item.hasTagCompound() && item.getTagCompound().hasKey("constructionSettings")) {
                settings.readFromNBT(item.getTagCompound().getCompoundTag("constructionSettings"));
            }
            return settings;
        }
        return null;
    }

    public static void writeConstructionSettings(ItemStack item, ConstructionSettings settings) {
        if (item.getItem() instanceof ItemConstructionTool) {
            item.setTagInfo("constructionSettings", settings.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public void renderBox(EntityPlayer player, ItemStack stack, float delta) {
        ConstructionSettings settings = getSettings(stack);
        BlockPosition p1, p2;
        BlockPosition p3 = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
        p1 = settings.hasPos1() ? settings.pos1() : p3;
        p2 = settings.hasPos2() ? settings.pos2() : p3;
        if (p1 != null && p2 != null) {
            Util.renderBoundingBox(player, BlockTools.getMin(p1, p2), BlockTools.getMax(p1, p2), delta);
        }
        if (p3 != null) {
            Util.renderBoundingBox(player, p3, p3, delta, 1, 0, 0);
        }
    }

    public static final class ConstructionSettings {
        Block block;
        int meta;
        BlockPosition pos1;
        BlockPosition pos2;
        ConstructionType type = ConstructionType.SOLID_FILL;

        protected void readFromNBT(NBTTagCompound tag) {
            if (tag.hasKey("pos1")) {
                pos1 = new BlockPosition(tag.getCompoundTag("pos1"));
            }
            if (tag.hasKey("pos2")) {
                pos2 = new BlockPosition(tag.getCompoundTag("pos2"));
            }
            if (tag.hasKey("block")) {
                block = Block.getBlockFromName(tag.getString("block"));
            }
            if (tag.hasKey("meta")) {
                meta = tag.getInteger("meta");
            }
            if (tag.hasKey("type")) {
                type = ConstructionType.get(tag.getInteger("type"));
            }
        }

        protected NBTTagCompound writeToNBT(NBTTagCompound tag) {
            if (block != null) {
                tag.setString("block", Block.blockRegistry.getNameForObject(block));
            }
            tag.setInteger("meta", meta);
            if (pos1 != null) {
                tag.setTag("pos1", pos1.writeToNBT(new NBTTagCompound()));
            }
            if (pos2 != null) {
                tag.setTag("pos2", pos2.writeToNBT(new NBTTagCompound()));
            }
            tag.setInteger("type", type.ordinal());
            return tag;
        }

        public boolean hasPos1() {
            return pos1 != null;
        }

        public boolean hasPos2() {
            return pos2 != null;
        }

        public BlockPosition pos1() {
            return pos1;
        }

        public BlockPosition pos2() {
            return pos2;
        }

    }

    public static enum ConstructionType {

        /**
         * Fills current layer and downwards with chosen block
         */
        LAKE_FILL,

        /**
         * fills current layer only with chosen block
         */
        LAYER_FILL,

        /**
         * fills entire bounding box with chosen block
         */
        SOLID_FILL,

        /**
         * creates a box around chosen area with chosen block
         */
        BOX_FILL;

        public ConstructionType next() {
            int ordinal = ordinal();
            ordinal++;
            if (ordinal >= values().length) {
                ordinal = 0;
            }
            return values()[ordinal];
        }

        public static ConstructionType get(int index) {
            if (index >= 0 && index < values().length) {
                return values()[index];
            }
            return SOLID_FILL;
        }
    }

}
