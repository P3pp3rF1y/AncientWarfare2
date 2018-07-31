package net.shadowmage.ancientwarfare.core.gui.manual;

import net.shadowmage.ancientwarfare.core.manual.IContentElement;

import java.util.List;

interface IElementWrapperCreator<T extends IContentElement> {
	List<BaseElementWrapper<T>> construct(int topLeftY, int width, int remainingPageHeight, int emptyPageHeight, T element);
}
