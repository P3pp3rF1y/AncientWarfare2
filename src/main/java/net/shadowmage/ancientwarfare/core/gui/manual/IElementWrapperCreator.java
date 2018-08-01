package net.shadowmage.ancientwarfare.core.gui.manual;

import net.shadowmage.ancientwarfare.core.gui.manual.elements.BaseElementWrapper;
import net.shadowmage.ancientwarfare.core.manual.IContentElement;

import java.util.List;

public interface IElementWrapperCreator<T extends IContentElement> {
	List<BaseElementWrapper<T>> construct(GuiManual gui, int topLeftY, int width, int remainingPageHeight, int emptyPageHeight, T element);
}
