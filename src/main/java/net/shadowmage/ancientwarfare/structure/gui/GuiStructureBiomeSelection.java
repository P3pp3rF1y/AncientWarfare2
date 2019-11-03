package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.biome.Biome;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.structure.registry.BiomeGroupRegistry;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public class GuiStructureBiomeSelection extends GuiContainerBase {

	private final GuiStructureScanner parent;

	private Checkbox whiteList;
	private Text biomeSearchBox;
	private CompositeScrolled biomeArea;
	private CompositeScrolled biomeGroupArea;
	private Listener listener;
	private Text biomeGroupSearchBox;

	public GuiStructureBiomeSelection(GuiStructureScanner parent) {
		super(new ContainerBase(parent.getContainer().player));
		this.parent = parent;
		this.shouldCloseOnVanillaKeys = false;
	}

	@Override
	public void initElements() {

		addGuiElement(new Label(8, 22, I18n.format("guistrings.scanner.biome_groups") + ":"));

		biomeGroupSearchBox = new Text(80, 22, 170, "", this) {
			@Override
			public void onTextUpdated(String oldText, String newText) {
				super.onTextUpdated(oldText, newText);
				if (!oldText.equals(newText)) {
					refreshGui();
				}
			}
		};
		addGuiElement(biomeGroupSearchBox);

		biomeGroupArea = new CompositeScrolled(this, 0, 36, 256, 80);
		addGuiElement(biomeGroupArea);

		whiteList = new Checkbox(8, 118, 16, 16, "guistrings.biome_whitelist") {
			@Override
			public void onToggled() {
				parent.getContainer().updateValidator(v -> v.setBiomeWhiteList(checked()));
			}
		};

		whiteList.setChecked(parent.getContainer().getValidator().isBiomeWhiteList());
		addGuiElement(whiteList);

		addGuiElement(new Label(8, 138, I18n.format("guistrings.scanner.biomes") + ":"));

		biomeSearchBox = new Text(80, 138, 170, "", this) {
			@Override
			public void onTextUpdated(String oldText, String newText) {
				super.onTextUpdated(oldText, newText);
				if (!oldText.equals(newText)) {
					refreshGui();
				}
			}
		};
		addGuiElement(biomeSearchBox);

		biomeArea = new CompositeScrolled(this, 0, 152, 256, 100);
		addGuiElement(biomeArea);

		Button button = new Button(256 - 8 - 55, 8, 55, 12, "guistrings.done") {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(parent);
			}
		};
		addGuiElement(button);

		listener = new Listener(Listener.MOUSE_UP) {
			@Override
			public boolean onEvent(GuiElement widget, ActivationEvent evt) {
				if (widget.isMouseOverElement(evt.mx, evt.my)) {
					Set<String> biomeNames = parent.getContainer().getValidator().getBiomeList();
					if (((BiomeCheck) widget).checked()) {
						biomeNames.add(((BiomeCheck) widget).name);
					} else {
						biomeNames.remove(((BiomeCheck) widget).name);
					}
					parent.getContainer().updateValidator(v -> v.setBiomeList(biomeNames));
				}
				return true;
			}
		};
		refreshBiomeGroupList();
		refreshBiomeList();
	}

	private void refreshBiomeList() {
		Set<String> biomeNames = parent.getContainer().getValidator().getBiomeList();
		biomeArea.clearElements();
		int totalHeight = 3;
		for (Biome biome : Biome.REGISTRY) {
			//noinspection ConstantConditions
			String name = biome.getRegistryName().toString();
			if (name.contains(biomeSearchBox.getText()) || biome.getBiomeName().contains(biomeSearchBox.getText())) {
				BiomeCheck box = new BiomeCheck(totalHeight, biome);
				biomeArea.addGuiElement(box);
				totalHeight += 16;
				if (biomeNames.contains(name)) {
					box.setChecked(true);
				}
				box.addNewListener(listener);
			}
		}
		biomeArea.setAreaSize(totalHeight);
	}

	private void refreshBiomeGroupList() {
		int totalHeight = 3;
		Set<String> biomeGroupNames = parent.getContainer().getValidator().getBiomeGroupList();
		biomeGroupArea.clearElements();
		for (String biomeGroup : BiomeGroupRegistry.getBiomeGroups().stream().filter(bg -> bg.contains(biomeGroupSearchBox.getText()))
				.sorted(Comparator.naturalOrder()).collect(Collectors.toList())) {
			Checkbox box = new Checkbox(8, totalHeight, 16, 16, biomeGroup) {
				@Override
				public void onToggled() {
					Set<String> biomeGroups = parent.getContainer().getValidator().getBiomeGroupList();
					if (checked()) {
						biomeGroups.add(label);
					} else {
						biomeGroups.remove(label);
					}
					parent.getContainer().updateValidator(v -> v.setBiomeGroupList(biomeGroups));
				}
			};
			biomeGroupArea.addGuiElement(box);
			totalHeight += 16;
			if (biomeGroupNames.contains(biomeGroup)) {
				box.setChecked(true);
			}
		}
		biomeGroupArea.setAreaSize(totalHeight);
	}

	@Override
	public void setupElements() {
		whiteList.setChecked(parent.getContainer().getValidator().isBiomeWhiteList());
		refreshBiomeGroupList();
		refreshBiomeList();
	}

	private class BiomeCheck extends Checkbox {

		private final String name;

		/*
		 * @param topLeftY height of display
		 * @param label text displayed
		 */
		public BiomeCheck(int topLeftY, Biome biome) {
			//noinspection ConstantConditions
			super(8, topLeftY, 16, 16, String.format("%s (%s)", biome.getBiomeName(), biome.getRegistryName().toString()));
			//noinspection ConstantConditions
			this.name = biome.getRegistryName().toString();
		}
	}
}
