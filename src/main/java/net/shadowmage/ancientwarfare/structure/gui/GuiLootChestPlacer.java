package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiSelectFromList;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.Composite;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemToggleButton;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.structure.container.ContainerLootChestPlacer;
import net.shadowmage.ancientwarfare.structure.item.ItemLootChestPlacer;
import net.shadowmage.ancientwarfare.structure.tile.LootSettings;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GuiLootChestPlacer extends GuiContainerBase<ContainerLootChestPlacer> {
	private static final int FORM_WIDTH = 300;
	private static final int FORM_HEIGHT = 300;

	private Button selection;
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
		int x = 28;
		addGuiElement(new Button(x, totalHeight, 250, 12, getLootSetting(LootSettings::getEntity).map(ResourceLocation::toString).orElse("")) {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiSelectFromList<>(GuiLootChestPlacer.this, text, s -> s,
						this::getEntityNames, s -> {
					setText(s);
					setLootSettings(settings -> settings.setEntity(new ResourceLocation(s)));
				}));
				refreshGui();
			}

			private List<String> getEntityNames() {
				return ForgeRegistries.ENTITIES.getKeys().stream().map(ResourceLocation::toString).sorted(Comparator.naturalOrder()).collect(Collectors.toList());
			}
		});
		totalHeight += 16;
		addGuiElement(new Label(x, totalHeight + 2, "guistring.loot_placer.entity_nbt"));
		addGuiElement(new Text(x + 60, totalHeight, 190, getLootSetting(s -> s.getEntityNBT().toString()).orElse(""), this) {
			@Override
			public void onTextUpdated(String oldText, String newText) {
				try {
					NBTTagCompound entityNBT = JsonToNBT.getTagFromJson(newText);
					setLootSettings(s -> s.setEntityNBT(entityNBT));
				}
				catch (NBTException e) {
					//noop
				}
			}
		});

		return totalHeight;
	}

	private int addSetLootTableElements(int totalHeight) {
		int x = 28;

		selection = new Button(x, totalHeight, 250, 12, getLootSetting(s -> s.getLootTableName().map(ResourceLocation::toString).orElse("")).orElse("")) {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiSelectFromList<>(GuiLootChestPlacer.this, text, s -> s,
						() -> getContainer().getLootTableNames().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList()), s -> {
					setText(s);
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
		int x = 28;
		addGuiElement(new Label(x, totalHeight, "guistrings.loot_placer.potion"));
		addGuiElement(new Label(x + 130, totalHeight, "guistrings.loot_placer.duration"));
		addGuiElement(new Label(x + 200, totalHeight, "guistrings.loot_placer.strength"));

		totalHeight += 16;

		Optional<List<PotionEffect>> potionEffects = getLootSetting(LootSettings::getEffects);

		if (potionEffects.isPresent()) {
			List<PotionEffect> effects = potionEffects.get();
			for (int i = 0; i < effects.size(); i++) {
				final int effectIndex = i;
				addGuiElement(new PotionEffectElement(x, totalHeight, effects.get(i), this, effect -> {
					effects.set(effectIndex, effect);
					setLootSettings(s -> s.setEffects(effects));
				}));
				addGuiElement(new Button(x + 230, totalHeight, 20, 12, "-") {
					@Override
					protected void onPressed() {
						effects.remove(effectIndex);
						refreshGui();
						setLootSettings(s -> s.setEffects(effects));
					}
				});
				totalHeight += 16;
				setLootSettings(s -> s.setEffects(effects));
			}
			addGuiElement(new Button(x, totalHeight, 20, 12, "+") {
				@Override
				protected void onPressed() {
					effects.add(new PotionEffect(MobEffects.POISON, 200, 1));
					refreshGui();
					setLootSettings(s -> s.setEffects(effects));
				}
			});
			totalHeight += 16;
		}
		return totalHeight;
	}

	private static class PotionEffectElement extends Composite {
		private PotionEffect potionEffect;

		public PotionEffectElement(int topLeftX, int topLeftY, PotionEffect effect, GuiContainerBase parent, Consumer<PotionEffect> onEffectUpdated) {
			super(parent, topLeftX, topLeftY, 230, 12);
			potionEffect = effect;

			Button selectEffect = new Button(0, 0, 160, 12, potionEffect.getPotion().getRegistryName().toString()) {
				@Override
				protected void onPressed() {
					Minecraft.getMinecraft().displayGuiScreen(new GuiSelectFromList<>(parent, text, s -> s,
							() -> getEffectNames(), s -> {
						setText(s);
						potionEffect = new PotionEffect(Potion.REGISTRY.getObject(new ResourceLocation(s)), potionEffect.getDuration(), potionEffect.getAmplifier());
						onEffectUpdated.accept(potionEffect);
						parent.refreshGui();
					}));
				}
			};
			addGuiElement(selectEffect);

			NumberInput duration = new NumberInput(170, 0, 22, potionEffect.getDuration() / 20, parent) {
				@Override
				public void onValueUpdated(float value) {
					potionEffect = new PotionEffect(potionEffect.getPotion(), (int) value * 20, potionEffect.getAmplifier());
					onEffectUpdated.accept(potionEffect);
				}
			};
			duration.setIntegerValue();
			addGuiElement(duration);

			NumberInput amplifier = new NumberInput(200, 0, 15, potionEffect.getAmplifier(), parent) {
				@Override
				public void onValueUpdated(float value) {
					potionEffect = new PotionEffect(potionEffect.getPotion(), potionEffect.getDuration(), (int) value);
					onEffectUpdated.accept(potionEffect);
				}
			};
			amplifier.setIntegerValue();
			addGuiElement(amplifier);
		}

		@Override
		protected int getPaddingY() {
			return 0;
		}

		@Override
		protected int getPaddingX() {
			return 0;
		}

		@Override
		protected boolean drawBackground() {
			return false;
		}

		private List<String> getEffectNames() {
			return Potion.REGISTRY.getKeys().stream().map(ResourceLocation::toString).sorted(Comparator.naturalOrder()).collect(Collectors.toList());
		}
	}

	private int addPlaceBlockElements(int totalHeight) {
		int x = 8;
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

		return totalHeight + 30;
	}

	@Override
	public void setupElements() {
		clearElements();
		int totalHeight = 8;
		addGuiElement(new Label(8, totalHeight, "guistrings.loot_placer.place_block"));
		totalHeight += 10;
		totalHeight = addPlaceBlockElements(totalHeight);

		setLootTable = new Checkbox(8, totalHeight, 16, 16, "guistrings.loot_placer.set_loot_table") {
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

		splashPotion = new Checkbox(8, totalHeight, 16, 16, "guistrings.loot_placer.splash_potion") {
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

		Checkbox spawnEntity = new Checkbox(8, totalHeight, 16, 16, "guistrings.loot_placer.spawn_entity") {
			@Override
			public void onToggled() {
				setLootSettings(settings -> settings.setSpawnEntity(checked()));
				refreshGui();
			}
		};
		spawnEntity.setChecked(getLootSetting(LootSettings::getSpawnEntity).orElse(false));
		addGuiElement(spawnEntity);
		totalHeight += 20;

		if (spawnEntity.checked()) {
			totalHeight = addSpawnEntityElements(totalHeight);
		}
	}
}
