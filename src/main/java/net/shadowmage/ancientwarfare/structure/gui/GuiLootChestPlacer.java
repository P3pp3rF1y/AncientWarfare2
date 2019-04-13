package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiSelectFromList;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemToggleButton;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.structure.container.ContainerLootChestPlacer;
import net.shadowmage.ancientwarfare.structure.item.ItemLootChestPlacer;
import net.shadowmage.ancientwarfare.structure.tile.LootSettings;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GuiLootChestPlacer extends GuiContainerBase<ContainerLootChestPlacer> {
	private static final int FORM_WIDTH = 300;
	private static final int FORM_HEIGHT = 200;

	private Button selection;
	private String lootTable;
	private Checkbox setLootTable;
	private Checkbox splashPotion;

	public GuiLootChestPlacer(ContainerBase container) {
		super(container, FORM_WIDTH, FORM_HEIGHT);
	}

	@Override
	public void initElements() {
	}

	private void setLootSettings(Consumer<LootSettings> setLootSetting) {
		LootSettings lootSettings = getContainer().getLootSettings().orElse(new LootSettings());
		setLootSetting.accept(lootSettings);
		getContainer().setLootSettings(lootSettings);
	}

	private <T> Optional<T> getLootSetting(Function<LootSettings, T> getSetting) {
		return getContainer().getLootSettings().map(getSetting);
	}

	private int addSpawnEntityElements(int totalHeight) {
		return totalHeight;
	}

	private int addSetLootTableElements(int totalHeight) {
		int x = 28;

		lootTable = getLootSetting(s -> s.getLootTableName().map(ResourceLocation::toString).orElse("")).orElse("");
		selection = new Button(x, totalHeight, 250, 12, lootTable) {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiSelectFromList<>(GuiLootChestPlacer.this, lootTable, s -> s,
						() -> getContainer().getLootTableNames().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList()), s -> {
					lootTable = s;
					selection.setText(s);
					setLootSettings(settings -> settings.setLootTableName(new ResourceLocation(s)));
				}));
				refreshGui();
			}
		};
		addGuiElement(selection);

		totalHeight += 18;

		addGuiElement(new Label(x, totalHeight, "guistrings.loot_rolls"));
		NumberInput lootRolls = new NumberInput(100, totalHeight - 2, 15, getLootSetting(LootSettings::getLootRolls).orElse(1), this) {
			@Override
			public void onValueUpdated(float value) {
				setLootSettings(settings -> settings.setLootRolls((int) value));
			}
		};
		lootRolls.setIntegerValue();
		addGuiElement(lootRolls);

		return totalHeight + 16;
	}

	private int addSplashPotionElements(int totalHeight) {
		return totalHeight;
	}

	private int addPlaceBlockElements(int totalHeight) {
		int x = 28;
		Set<ItemToggleButton> stackToggles = new LinkedHashSet<>();

		ItemStack selectedStack = getContainer().getBlockStack();
		for (ItemStack container : ItemLootChestPlacer.getLootContainers()) {
			ItemToggleButton button = new ItemToggleButton(x, totalHeight, container.copy(), false) {
				@Override
				protected void onPressed(int mButton) {
					if (isToggled()) {
						getContainer().setBlockStack(container.copy());
						for (ItemToggleButton btn : stackToggles) {
							if (btn != this) {
								btn.setToggled(false);
							}
						}
					}
				}
			};
			button.setToggled(ItemStack.areItemStacksEqual(container, selectedStack));
			stackToggles.add(button);
			addGuiElement(button);
			x += 24;
		}
		if (selectedStack.isEmpty() && !stackToggles.isEmpty()) {
			stackToggles.iterator().next().setToggled(true);
		}

		return totalHeight + 26;
	}

	@Override
	public void setupElements() {
		clearElements();
		int totalHeight = 8;
		addGuiElement(new Label(8, totalHeight, "guistrings.spawner_placer.place_block"));
		totalHeight += 20;
		totalHeight = addPlaceBlockElements(totalHeight);

		setLootTable = new Checkbox(8, totalHeight, 16, 16, "guistrings.spawner_placer.set_loot_table") {
			@Override
			public void onToggled() {
				setLootSettings(settings -> settings.setHasLoot(setLootTable.checked()));
				refreshGui();
			}
		};
		setLootTable.setChecked(getLootSetting(LootSettings::getHasLoot).orElse(false));
		addGuiElement(setLootTable);
		totalHeight += 20;

		if (setLootTable.checked()) {
			totalHeight = addSetLootTableElements(totalHeight);
		}

		splashPotion = new Checkbox(8, totalHeight, 16, 16, "guistrings.spawner_placer.splash_potion") {
			@Override
			public void onToggled() {
				setLootSettings(settings -> settings.setSplashPotion(splashPotion.checked()));
				refreshGui();
			}
		};
		splashPotion.setChecked(getLootSetting(LootSettings::getSplashPotion).orElse(false));
		addGuiElement(splashPotion);
		totalHeight += 20;

		if (splashPotion.checked()) {
			totalHeight = addSplashPotionElements(totalHeight);
		}

		Checkbox spawnEntity = new Checkbox(8, totalHeight, 16, 16, "guistrings.spawner_placer.spawn_entity") {
			@Override
			public void onToggled() {
				//TODO add setting properties
				refreshGui();
			}
		};
		spawnEntity.setChecked(true); //TODO pass real data
		addGuiElement(spawnEntity);
		totalHeight += 20;

		if (spawnEntity.checked()) {
			totalHeight = addSpawnEntityElements(totalHeight);
		}
	}
}
