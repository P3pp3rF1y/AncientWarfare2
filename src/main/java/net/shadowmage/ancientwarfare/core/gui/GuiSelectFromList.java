package net.shadowmage.ancientwarfare.core.gui;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;

import javax.annotation.Nullable;
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
	private boolean showFilter;

	public GuiSelectFromList(GuiContainerBase parent, @Nullable T entry, Function<T, String> getDisplayName, Supplier<List<T>> getList, Consumer<T> setEntry) {
		this(parent, entry, getDisplayName, getList, setEntry, true);
	}

	public GuiSelectFromList(GuiContainerBase parent,
			@Nullable T entry, Function<T, String> getDisplayName, Supplier<List<T>> getList, Consumer<T> setEntry, boolean showFilter) {
		this(parent, entry, getDisplayName, getList, setEntry, showFilter, parent.getContainer());
	}

	public GuiSelectFromList(GuiContainerBase parent, @Nullable
			T entry, Function<T, String> getDisplayName, Supplier<List<T>> getList, Consumer<T> setEntry, boolean showFilter, ContainerBase container) {
		super(container);
		this.parent = parent;
		this.entry = entry;
		this.getDisplayName = getDisplayName;
		this.getList = getList;
		this.setEntry = setEntry;
		this.showFilter = showFilter;
	}

	@Override
	public void initElements() {
		if (showFilter) {
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

				@Override
				public void onTextUpdated(String oldText, String newText) {
					refreshGui();
				}
			};
			addGuiElement(selectionLabel);
		}

		area = new CompositeScrolled(this, 0, 40, 256, 200);
		addGuiElement(area);
	}

	@Override
	public void setupElements() {
		area.clearElements();
		int totalHeight = 8;
		Button button;
		for (T listEntry : getFilteredList()) {
			button = new Button(8, totalHeight, 256 - 8 - 16, 12, getDisplayName.apply(listEntry)) {
				@Override
				protected void onPressed() {
					setEntry.accept(listEntry);
					Minecraft.getMinecraft().displayGuiScreen(parent);
					parent.refreshGui();
				}
			};
			area.addGuiElement(button);
			totalHeight += 12;
		}
		area.setAreaSize(totalHeight);
	}

	private List<T> getFilteredList() {
		if (!showFilter) {
			return getList.get();
		}

		return getList.get().stream().filter(input -> input.toString().contains(selectionLabel.getText())).collect(Collectors.toList());
	}

	@Override
	protected boolean onGuiCloseRequested() {
		Minecraft.getMinecraft().displayGuiScreen(parent);
		return false;
	}
}
