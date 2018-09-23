package net.shadowmage.ancientwarfare.core.gui.manual;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.gui.manual.elements.BaseElementWrapper;
import net.shadowmage.ancientwarfare.core.gui.manual.elements.HeadingElementWrapper;
import net.shadowmage.ancientwarfare.core.gui.manual.elements.ImageElementWrapper;
import net.shadowmage.ancientwarfare.core.gui.manual.elements.ItemElementWrapper;
import net.shadowmage.ancientwarfare.core.gui.manual.elements.TableOfContentsWrapper;
import net.shadowmage.ancientwarfare.core.gui.manual.elements.TextElementWrapper;
import net.shadowmage.ancientwarfare.core.manual.HeadingElement;
import net.shadowmage.ancientwarfare.core.manual.IContentElement;
import net.shadowmage.ancientwarfare.core.manual.ImageElement;
import net.shadowmage.ancientwarfare.core.manual.ItemElement;
import net.shadowmage.ancientwarfare.core.manual.TableOfContentsElement;
import net.shadowmage.ancientwarfare.core.manual.TextElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class ElementWrapperFactory {
	private ElementWrapperFactory() {}

	private static final Set<ElementWrapperMapping> MAPPINGS = ImmutableSet.of(
			new ElementWrapperMapping<>(TextElement.class, new TextElementWrapper.Creator()),
			new ElementWrapperMapping<>(HeadingElement.class, new HeadingElementWrapper.Creator()),
			new ElementWrapperMapping<>(TableOfContentsElement.class, new TableOfContentsWrapper.Creator()),
			new ElementWrapperMapping<>(ImageElement.class, new ImageElementWrapper.Creator()),
			new ElementWrapperMapping<>(ItemElement.class, new ItemElementWrapper.Creator())
	);

	public static <T extends IContentElement> List<BaseElementWrapper<T>> create(GuiManual gui, int topLeftY, int width, int remainingPageHeight, int emptyPageHeight, T element) {
		//noinspection unchecked
		return MAPPINGS.stream().filter(m -> m.matches(element)).findFirst()
				.map(m -> m.construct(gui, topLeftY, width, remainingPageHeight, emptyPageHeight, element))
				.orElse(Collections.emptyList());
	}

	public static List<List<BaseElementWrapper>> getPagedWrappedContent(GuiManual gui, List<IContentElement> elements, int width, int pageHeight) {
		List<List<BaseElementWrapper>> pagedWrappers = new ArrayList<>();
		int currentY = 0;
		List<BaseElementWrapper> currentPageWrappers = addNewPage(pagedWrappers);
		List<Tuple<IContentElement, List<BaseElementWrapper<IContentElement>>>> keepTogether = new ArrayList<>();
		for (IContentElement element : elements) {
			List<BaseElementWrapper<IContentElement>> pageElements = create(gui, currentY, width, pageHeight - currentY, pageHeight, element);

			if (!pageElements.isEmpty() && pageElements.get(0).shouldKeepWithNext()) {
				keepTogether.add(new Tuple<>(element, pageElements));
				currentY += pageElements.get(0).getHeight();
				continue;
			} else if (!keepTogether.isEmpty()) {
				pageElements = addAllToKeepAndClearCurrent(keepTogether, element, pageElements);
				currentY = addKeepToCurrentElements(gui, width, pageHeight, keepTogether, pageElements);
				keepTogether.clear();
			}

			for (BaseElementWrapper wrapper : pageElements) {
				if (!currentPageWrappers.isEmpty() && wrapper.getTopLeftY() == 0) {
					currentPageWrappers = addNewPage(pagedWrappers);
					currentY = 0;
				}
				currentPageWrappers.add(wrapper);
				currentY += wrapper.getHeight();
			}
		}
		return pagedWrappers;

	}

	private static int addKeepToCurrentElements(GuiManual gui, int width, int pageHeight, List<Tuple<IContentElement, List<BaseElementWrapper<IContentElement>>>> keepTogether, List<BaseElementWrapper<IContentElement>> pageElements) {
		int currentY;
		boolean recalculateY = !areOnTheSamePage(keepTogether);
		if (recalculateY) {
			currentY = recalculateYPositions(gui, width, pageHeight, keepTogether, pageElements);
		} else {
			keepTogether.stream().map(Tuple::getSecond).forEach(pageElements::addAll);
			currentY = pageElements.get(0).getTopLeftY(); //reset so that usual logic can again add up to this base value
		}
		return currentY;
	}

	private static List<BaseElementWrapper<IContentElement>> addAllToKeepAndClearCurrent(List<Tuple<IContentElement, List<BaseElementWrapper<IContentElement>>>> keepTogether, IContentElement element, List<BaseElementWrapper<IContentElement>> pageElements) {
		keepTogether.add(new Tuple<>(element, ImmutableList.copyOf(pageElements)));
		return new ArrayList<>();
	}

	private static int recalculateYPositions(GuiManual gui, int width, int pageHeight, List<Tuple<IContentElement, List<BaseElementWrapper<IContentElement>>>> keepTogether,
			List<BaseElementWrapper<IContentElement>> pageElements) {
		int updatedY = 0;
		for (Tuple<IContentElement, List<BaseElementWrapper<IContentElement>>> el : keepTogether) {
			List<BaseElementWrapper<IContentElement>> els = create(gui, updatedY, width, pageHeight - updatedY, pageHeight, el.getFirst());
			if (!els.isEmpty()) {
				updatedY += els.get(els.size() - 1).getHeight();
			}
			pageElements.addAll(els);
		}
		return updatedY;
	}

	private static boolean areOnTheSamePage(List<Tuple<IContentElement, List<BaseElementWrapper<IContentElement>>>> keepTogether) {
		int currentY = 0;
		for (Tuple<IContentElement, List<BaseElementWrapper<IContentElement>>> element : keepTogether) {
			List<BaseElementWrapper<IContentElement>> wrappers = element.getSecond();

			if (wrappers.isEmpty()) {
				continue;
			}

			if (wrappers.get(0).getTopLeftY() < currentY) {
				return false;
			}

			if (!wrappers.get(0).shouldKeepWithNext()) {
				return true;
			}

			currentY = wrappers.get(0).getTopLeftY();
		}

		return true;
	}

	private static List<BaseElementWrapper> addNewPage(List<List<BaseElementWrapper>> pagedWrappers) {
		List<BaseElementWrapper> currentPageWrappers = new ArrayList<>();
		pagedWrappers.add(currentPageWrappers);
		return currentPageWrappers;
	}

	private static class ElementWrapperMapping<T extends IContentElement> {
		private final Class<T> elementClass;
		private final IElementWrapperCreator<T> creator;

		@SuppressWarnings("squid:UnusedPrivateMethod")
		//actually is used above and sonar lint marks it as unused because of private modifier and no use within the class itself
		private ElementWrapperMapping(Class<T> elementClass, IElementWrapperCreator<T> creator) {
			this.elementClass = elementClass;
			this.creator = creator;
		}

		public boolean matches(IContentElement element) {
			return elementClass.isInstance(element);
		}

		public List<BaseElementWrapper<T>> construct(GuiManual gui, int topLeftY, int width, int remainingPageHeight, int emptyPageHeight, T element) {
			return creator.construct(gui, topLeftY, width, remainingPageHeight, emptyPageHeight, element);
		}
	}
}
