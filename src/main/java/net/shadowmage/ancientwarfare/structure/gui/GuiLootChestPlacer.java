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
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
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
	private static final int FORM_HEIGHT = 240;
	private static final String LOOT_TABLE_PREFIX = AncientWarfareCore.MOD_ID + ":chests/";
	private static final String COMPAT_LOOT_TABLE_PREFIX = LOOT_TABLE_PREFIX + "compat/";

	private Checkbox setLootTable;
	private Checkbox splashPotion;
	private Checkbox playerMessage;

	public GuiLootChestPlacer(ContainerBase container) {
		super(container, FORM_WIDTH, FORM_HEIGHT);
	}

	@Override
	public void initElements() {
		//nothing to add here
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

		return totalHeight + 16;
	}

	private String shortenCompatLootTables(String s) {
		if (s.startsWith(COMPAT_LOOT_TABLE_PREFIX)) {
			return LOOT_TABLE_PREFIX + s.substring(s.indexOf('/', COMPAT_LOOT_TABLE_PREFIX.length()) + 1);
		}
		return s;
	}

	private int addSetLootTableElements(int totalHeight) {
		int x = 28;

		Button selection = new Button(x, totalHeight, 250, 12, getLootSetting(s -> s.getLootTableName().map(lootTable -> this.shortenCompatLootTables(lootTable.toString())).orElse("")).orElse("")) {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiSelectFromList<>(GuiLootChestPlacer.this,
						getLootSetting(settings -> settings.getLootTableName().map(ResourceLocation::toString).orElse("")).orElse(""), GuiLootChestPlacer.this::shortenCompatLootTables,
						() -> getContainer().getLootTableNames().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList()), s -> {
					setText(shortenCompatLootTables(s));
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
					@SuppressWarnings("squid:S5413") // remove is used in a button callback so terchnically outside of the loop
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

	private int addPlayerMessageElements(int totalHeight) {
		int x = 28;
		addGuiElement(new Label(x, totalHeight + 2, "guistrings.loot_placer.message"));
		addGuiElement(new Text(x + 60, totalHeight, 190, getLootSetting(LootSettings::getPlayerMessage).orElse(""), this) {
			@Override
			public void onTextUpdated(String oldText, String newText) {
				setLootSettings(s -> s.setPlayerMessage(newText));
			}
		});

		return totalHeight;
	}

	private static class PotionEffectElement extends Composite {
		private PotionEffect potionEffect;

		private PotionEffectElement(int topLeftX, int topLeftY, PotionEffect effect, GuiContainerBase parent, Consumer<PotionEffect> onEffectUpdated) {
			super(parent, topLeftX, topLeftY, 230, 12);
			potionEffect = effect;

			//noinspection ConstantConditions
			Button selectEffect = new Button(0, 0, 160, 12, potionEffect.getPotion().getRegistryName().toString()) {
				@Override
				protected void onPressed() {
					Minecraft.getMinecraft().displayGuiScreen(new GuiSelectFromList<>(parent, text, s -> s,
							PotionEffectElement.this::getEffectNames, s -> {
						setText(s);
						//noinspection ConstantConditions
						potionEffect = new PotionEffect(Potion.REGISTRY.getObject(new ResourceLocation(s)), potionEffect.getDuration(), potionEffect.getAmplifier());
						onEffectUpdated.accept(potionEffect);
						parent.refreshGui();
					}));
				}
			};
			addGuiElement(selectEffect);

			@SuppressWarnings("squid:S2184") //really just want to floor the value so no need to cast to float
					NumberInput duration = new NumberInput(170, 0, 22, potionEffect.getDuration() / 20f, parent) {
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

		ItemLootChestPlacer.LootContainerInfo selectedContainer = getContainer().getLootContainerInfo();
		for (ItemLootChestPlacer.LootContainerInfo container : ItemLootChestPlacer.getLootContainers().values()) {
			final ItemStack containerStack = container.getStack();
			ItemToggleButton button = new ItemToggleButton(x, totalHeight, containerStack.copy(), false) {
				@Override
				protected void onPressed(int mButton) {
					if (isToggled()) {
						getContainer().setContainer(container.getName());
						for (ItemToggleButton btn : stackToggles) {
							if (btn != this) {
								btn.setToggled(false);
							}
						}
					}
				}
			};
			button.addTooltip(containerStack.getDisplayName()); // tooltip for hovering loot items
			button.setToggled(container.getName().equals(selectedContainer.getName()));
			stackToggles.add(button);
			addGuiElement(button);
			x += 24;
			if (x >= 296) { // shift loot items a row down if the line is full, 12 fits in with FORM_WIDTH = 300
				x = 8;
				totalHeight = +40;
			}
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

		playerMessage = new Checkbox(8, totalHeight, 16, 16, "guistrings.loot_placer.player_message") {
			@Override
			public void onToggled() {
				setLootSettings(settings -> settings.setHasMessage(playerMessage.checked()));
				refreshGui();
			}
		};
		playerMessage.setChecked(getLootSetting(LootSettings::hasMessage).orElse(false));
		addGuiElement(playerMessage); // add the checkbox
		totalHeight += 20;

		if (playerMessage.checked()) {
			totalHeight = addPlayerMessageElements(totalHeight); // display message elements if checkbox is checked
		}

	}
}
