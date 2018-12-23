package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;

import java.util.HashSet;
import java.util.Set;

public class GuiModSelection extends GuiContainerBase {

	private final GuiStructureScanner parent;

	private CompositeScrolled area;

	private Text modEntry;

	private Set<String> mods = new HashSet<>();

	public GuiModSelection(GuiStructureScanner parent) {
		super(new ContainerBase(parent.getContainer().player));
		this.parent = parent;
		this.shouldCloseOnVanillaKeys = false;
		mods.addAll(parent.getContainer().getModDependencies());
	}

	@Override
	public void initElements() {
		Label label = new Label(8, 8, I18n.format("guistrings.select_mods") + ":");
		addGuiElement(label);

		area = new CompositeScrolled(this, 0, 40, 256, 200);
		this.addGuiElement(area);

		Button button = new Button(256 - 8 - 55, 8, 55, 12, "guistrings.done") {
			@Override
			protected void onPressed() {
				closeGui();
			}
		};
		addGuiElement(button);

		modEntry = new Text(8, 22, 167, "", this);
		button = new Button(140 + 35 + 4, 22, 12, 12, "+") {
			@Override
			protected void onPressed() {
				if (!modEntry.getText().isEmpty()) {
					mods.add(modEntry.getText());
					parent.getContainer().updateModDependencies(mods);
					refreshGui();
				}
			}
		};

		addGuiElement(button);
		addGuiElement(modEntry);
	}

	@Override
	public void setupElements() {
		area.clearElements();

		int totalHeight = 8;
		for (String mod : mods) {
			area.addGuiElement(new ModButton(8, totalHeight, 232, 12, mod));
			totalHeight += 12;
		}
		area.setAreaSize(totalHeight);
	}

	@Override
	protected boolean onGuiCloseRequested() {
		Minecraft.getMinecraft().displayGuiScreen(parent);
		return false;
	}

	private class ModButton extends Button {
		final String mod;

		public ModButton(int topLeftX, int topLeftY, int width, int height, String mod) {
			super(topLeftX, topLeftY, width, height, mod);
			this.mod = mod;
		}

		@Override
		protected void onPressed() {
			mods.remove(mod);
			parent.getContainer().updateModDependencies(mods);
			refreshGui();
		}
	}

}
