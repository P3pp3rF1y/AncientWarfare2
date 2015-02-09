package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.biome.BiomeGenBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.*;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GuiStructureBlockSelection extends GuiContainerBase {

    GuiStructureScanner parent;

    CompositeScrolled area;

    public GuiStructureBlockSelection(GuiStructureScanner parent) {
        super(parent.getContainer(), 256, 240, defaultBackground);
        this.parent = parent;
        this.shouldCloseOnVanillaKeys = false;
    }

    private HashMap<Checkbox, Block> boxToBlock = new HashMap<Checkbox, Block>();
    private HashMap<Block, Checkbox> blockToBox = new HashMap<Block, Checkbox>();


    @Override
    public void initElements() {
        Label label = new Label(8, 8, StatCollector.translateToLocal("guistrings.select_blocks") + ":");
        addGuiElement(label);

        Button button = new Button(256 - 8 - 55, 8, 55, 12, StatCollector.translateToLocal("guistrings.done")) {
            @Override
            protected void onPressed() {
                setBlocksToValidator();
                Minecraft.getMinecraft().displayGuiScreen(parent);
            }
        };
        addGuiElement(button);

        area = new CompositeScrolled(this, 0, 8 + 12 + 4, 256, 240 - 24);
        this.addGuiElement(area);

        int totalHeight = 3;

        button = new Button(20, totalHeight, 120, 16, "Auto Fill From Biomes") {
            @Override
            protected void onPressed() {
                fillFromBiomes();
            }
        };
        area.addGuiElement(button);
        totalHeight += 16;

        button = new Button(20, totalHeight, 120, 16, "Auto Fill Vanilla Blocks") {
            @Override
            protected void onPressed() {
                addDefaults();
            }
        };
        area.addGuiElement(button);
        totalHeight += 16;


        Set<String> blockNames = parent.validator.getTargetBlocks();

        Block block;
        Checkbox box;
        ItemSlot slot;
        String name;
        for (int i = 0; i < 256; i++) {
            block = Block.getBlockById(i);
            if (block == null || block == Blocks.air) {
                continue;
            }

            name = BlockDataManager.instance().getNameForBlock(block);
            box = new Checkbox(8 + 18, totalHeight + 1, 16, 16, name);
            area.addGuiElement(box);
            if (blockNames.contains(name)) {
                box.setChecked(true);
            }

            slot = new ItemSlot(8, totalHeight, new ItemStack(block), this);
            area.addGuiElement(slot);

            totalHeight += 18;
            boxToBlock.put(box, block);
            blockToBox.put(block, box);
        }

        for (String blockName : AWStructureStatics.getUserDefinedTargetBlocks()) {
            block = BlockDataManager.instance().getBlockForName(blockName);
            if (block == null || block == Blocks.air || blockToBox.containsKey(block)) {
                continue;
            }

            name = BlockDataManager.instance().getNameForBlock(block);
            box = new Checkbox(8 + 18, totalHeight + 1, 16, 16, name);
            area.addGuiElement(box);
            if (blockNames.contains(name)) {
                box.setChecked(true);
            }

            slot = new ItemSlot(8, totalHeight, new ItemStack(block), this);
            area.addGuiElement(slot);

            totalHeight += 18;
            boxToBlock.put(box, block);
            blockToBox.put(block, box);
        }

        area.setAreaSize(totalHeight);
    }

    @Override
    public void setupElements() {

    }

    private void setBlocksToValidator() {
        Block block;
        Set<String> targetBlocks = new HashSet<String>();
        for (Checkbox box : boxToBlock.keySet()) {
            block = boxToBlock.get(box);
            if (box.checked()) {
                targetBlocks.add(BlockDataManager.instance().getNameForBlock(block));
            }
        }
        parent.validator.setTargetBlocks(targetBlocks);
    }

    /**
     * add top and filler blocks to block list from biomes
     */
    private void fillFromBiomes() {
        Set<String> selectedBiomes = new HashSet<String>();
        selectedBiomes.addAll(parent.validator.getBiomeList());
        boolean whitelist = parent.validator.isBiomeWhiteList();

        Set<BiomeGenBase> biomesToSearch = new HashSet<BiomeGenBase>();

        for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
            if (biome == null) {
                continue;
            }
            if (whitelist && selectedBiomes.contains(biome.biomeName) || !whitelist && !selectedBiomes.contains(biome.biomeName)) {
                biomesToSearch.add(biome);
            }
        }

        Set<Block> targetBlocks = new HashSet<Block>();
        Block topBlock;
        Block fillBlock;

        for (BiomeGenBase biome : biomesToSearch) {
            topBlock = biome.topBlock;
            fillBlock = biome.fillerBlock;
            if (topBlock != null) {
                targetBlocks.add(topBlock);
            }
            if (fillBlock != null) {
                targetBlocks.add(fillBlock);
            }
        }
        Checkbox box;
        for (Block block : blockToBox.keySet()) {
            box = blockToBox.get(block);
            box.setChecked(box.checked() || targetBlocks.contains(block));
        }
    }

    /**
     * add default blocks to target list, such as dirt, stone, grass, sand, gravel, and vanilla ores
     */
    private void addDefaults() {
        Set<Block> targetBlocks = new HashSet<Block>();

        targetBlocks.add(Blocks.sand);
        targetBlocks.add(Blocks.gravel);
        targetBlocks.add(Blocks.stone);
        targetBlocks.add(Blocks.grass);
        targetBlocks.add(Blocks.dirt);
        targetBlocks.add(Blocks.clay);
        targetBlocks.add(Blocks.stained_hardened_clay);
        targetBlocks.add(Blocks.gold_ore);
        targetBlocks.add(Blocks.iron_ore);
        targetBlocks.add(Blocks.diamond_ore);
        targetBlocks.add(Blocks.redstone_ore);
        targetBlocks.add(Blocks.lapis_ore);
        targetBlocks.add(Blocks.coal_ore);

        Checkbox box;
        for (Block block : blockToBox.keySet()) {
            box = blockToBox.get(block);
            box.setChecked(box.checked() || targetBlocks.contains(block));
        }
    }

}
