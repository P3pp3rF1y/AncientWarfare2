package net.shadowmage.ancientwarfare.core.gui;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GuiSelectFromList<T> extends GuiContainerBase {
	private final GuiContainerBase parent;
	private T entry;
	private final Function<T, String> getDisplayName;
	private final Supplier<List<T>> getList;
	private Consumer<T> setEntry;
	private CompositeScrolled area;
	private Text selectionLabel;

	public GuiSelectFromList(GuiContainerBase parent, T entry, Function<T, String> getDisplayName, Supplier<List<T>> getList, Consumer<T> setEntry) {
		super(parent.getContainer());
		this.parent = parent;
		this.entry = entry;
		this.getDisplayName = getDisplayName;
		this.getList = getList;
		this.setEntry = setEntry;
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
		selectionLabel = new Text(8, 30, 240, getDisplayName.apply(entry), this) {
			@Override
			protected void handleKeyInput(int keyCode, char ch) {
				String old = getText();
				super.handleKeyInput(keyCode, ch);
				String text = getText();
				if (!text.equals(old)) {
					refreshGui();
				}
			}
		};
		Listener l = new Listener(Listener.MOUSE_UP) {
			@Override
			public boolean onEvent(GuiElement widget, GuiContainerBase.ActivationEvent evt) {
				if (evt.mButton == 1 && widget.isMouseOverElement(evt.mx, evt.my)) {
					((Text) widget).setText("");
					refreshGui();
				}

				return false;
			}
		};
		selectionLabel.addNewListener(l);
		addGuiElement(selectionLabel);
		area = new CompositeScrolled(this, 0, 40, 256, 200);
		addGuiElement(area);
	}

	@Override
	public void setupElements() {
		area.clearElements();
		int totalHeight = 8;
		Button button;
		for (T listEntry : getList.get().stream().filter(input -> input.toString().contains(selectionLabel.getText())).collect(Collectors.toList())) {
			button = new Button(8, totalHeight, 256 - 8 - 16, 12, getDisplayName.apply(listEntry)) {
				@Override
				protected void onPressed() {
					setEntry.accept(listEntry);
					selectionLabel.setText(getDisplayName.apply(listEntry));
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
