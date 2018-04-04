package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GuiStructureBlockSelection extends GuiContainerBase {

	private final GuiStructureScanner parent;

	private final HashMap<Checkbox, Block> boxToBlock = new HashMap<>();
	private final HashMap<Block, Checkbox> blockToBox = new HashMap<>();

	public GuiStructureBlockSelection(GuiStructureScanner parent) {
		super(parent.getContainer());
		this.parent = parent;
		this.shouldCloseOnVanillaKeys = false;
	}

	@Override
	public void initElements() {
		Label label = new Label(8, 8, I18n.format("guistrings.select_blocks") + ":");
		addGuiElement(label);

		Button button = new Button(256 - 8 - 55, 8, 55, 12, "guistrings.done") {
			@Override
			protected void onPressed() {
				setBlocksToValidator();
				Minecraft.getMinecraft().displayGuiScreen(parent);
			}
		};
		addGuiElement(button);

		CompositeScrolled area = new CompositeScrolled(this, 0, 8 + 12 + 4, 256, 240 - 24);
		this.addGuiElement(area);

		int totalHeight = 3;

		button = new Button(20, totalHeight, 120, 16, "guistrings.auto_fill_biome") {
			@Override
			protected void onPressed() {
				fillFromBiomes();
			}
		};
		area.addGuiElement(button);
		totalHeight += 16;

		button = new Button(20, totalHeight, 120, 16, "guistrings.auto_fill_vanilla") {
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
			if (block == null || block == Blocks.AIR) {
				continue;
			}

			name = BlockDataManager.INSTANCE.getNameForBlock(block);
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
			block = BlockDataManager.INSTANCE.getBlockForName(blockName);
			if (block == null || block == Blocks.AIR || blockToBox.containsKey(block)) {
				continue;
			}

			name = BlockDataManager.INSTANCE.getNameForBlock(block);
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
		Set<String> targetBlocks = new HashSet<>();
		for (Checkbox box : boxToBlock.keySet()) {
			block = boxToBlock.get(box);
			if (box.checked()) {
				targetBlocks.add(BlockDataManager.INSTANCE.getNameForBlock(block));
			}
		}
		parent.validator.setTargetBlocks(targetBlocks);
	}

	/*
	 * add top and filler blocks to block list from biomes
	 */
	private void fillFromBiomes() {
		Set<String> selectedBiomes = new HashSet<>();
		selectedBiomes.addAll(parent.validator.getBiomeList());
		boolean whitelist = parent.validator.isBiomeWhiteList();

		Set<Biome> biomesToSearch = new HashSet<>();

		for (Biome biome : Biome.REGISTRY) {
			if (biome == null) {
				continue;
			}
			if (whitelist && selectedBiomes.contains(biome.getBiomeName()) || !whitelist && !selectedBiomes.contains(biome.getBiomeName())) {
				biomesToSearch.add(biome);
			}
		}

		Set<Block> targetBlocks = new HashSet<>();
		Block topBlock;
		Block fillBlock;

		for (Biome biome : biomesToSearch) {
			topBlock = biome.topBlock.getBlock();
			fillBlock = biome.fillerBlock.getBlock();
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

	/*
	 * add default blocks to target list, such as dirt, stone, grass, sand, gravel, and vanilla ores
	 */
	private void addDefaults() {
		Set<Block> targetBlocks = new HashSet<>();

		targetBlocks.add(Blocks.SAND);
		targetBlocks.add(Blocks.GRAVEL);
		targetBlocks.add(Blocks.STONE);
		targetBlocks.add(Blocks.GRASS);
		targetBlocks.add(Blocks.DIRT);
		targetBlocks.add(Blocks.CLAY);
		targetBlocks.add(Blocks.STAINED_HARDENED_CLAY);
		targetBlocks.add(Blocks.GOLD_ORE);
		targetBlocks.add(Blocks.IRON_ORE);
		targetBlocks.add(Blocks.DIAMOND_ORE);
		targetBlocks.add(Blocks.REDSTONE_ORE);
		targetBlocks.add(Blocks.LAPIS_ORE);
		targetBlocks.add(Blocks.COAL_ORE);

		Checkbox box;
		for (Block block : blockToBox.keySet()) {
			box = blockToBox.get(block);
			box.setChecked(box.checked() || targetBlocks.contains(block));
		}
	}

}
