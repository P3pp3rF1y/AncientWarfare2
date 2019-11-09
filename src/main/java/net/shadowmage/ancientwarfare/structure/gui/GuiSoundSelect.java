package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.core.util.SongPlayData;

import java.util.Comparator;
import java.util.stream.Collectors;

public class GuiSoundSelect extends GuiContainerBase {
	private final GuiContainerBase parent;
	private final SongPlayData.SongEntry songEntry;
	private CompositeScrolled area;
	private Text selectionLabel;

	protected GuiSoundSelect(GuiContainerBase parent, SongPlayData.SongEntry entry) {
		super(parent.getContainer());
		this.parent = parent;
		this.songEntry = entry;
	}

	@Override
	public void initElements() {
		Button button = new Button(256 - 8 - 55, 8, 55, 12, "guistrings.done") {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(parent);
				parent.refreshGui();
			}
		};
		addGuiElement(button);
		selectionLabel = new Text(8, 30, 240, songEntry.name(), this) {
			@Override
			protected void handleKeyInput(int keyCode, char ch) {
				String old = getText();
				super.handleKeyInput(keyCode, ch);
				String text = getText();
				if (!text.equals(old)) {
					refreshGui();
				}
			}

			@Override
			public void onTextUpdated(String oldText, String newText) {
				refreshGui();
			}
		};
		addGuiElement(selectionLabel);
		area = new CompositeScrolled(this, 0, 40, 256, 200);
		addGuiElement(area);
	}

	private String getShortenedName(String name) {
		return name.replace("ancientwarfarestructure:auto_load/", "auto:");
	}

	@Override
	public void setupElements() {
		area.clearElements();
		int totalHeight = 8;
		Button button;
		for (ResourceLocation registryName : ForgeRegistries.SOUND_EVENTS.getKeys().stream()
				.filter(input -> input.toString().contains(selectionLabel.getText()))
				.sorted(Comparator.naturalOrder()).collect(Collectors.toList())) {
			String name = registryName.toString();
			button = new Button(8, totalHeight, 256 - 8 - 16, 12, getShortenedName(name)) {
				@Override
				protected void onPressed() {
					songEntry.setSound(ForgeRegistries.SOUND_EVENTS.getValue(registryName));
					selectionLabel.setText(name);
					refreshGui();
				}
			};
			area.addGuiElement(button);
			totalHeight += 12;
		}
		area.setAreaSize(totalHeight);
	}

	@Override
	protected boolean onGuiCloseRequested() {
		Minecraft.getMinecraft().displayGuiScreen(parent);
		return false;
	}
}
