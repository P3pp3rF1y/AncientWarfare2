package net.shadowmage.ancientwarfare.core.gui.manual;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.manual.HeadingElement;
import net.shadowmage.ancientwarfare.core.manual.IContentElement;
import net.shadowmage.ancientwarfare.core.manual.TextElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class ElementWrapperFactory {
	private ElementWrapperFactory() {}

	private static final Set<ElementWrapperMapping> MAPPINGS = ImmutableSet.of(
			new ElementWrapperMapping<>(TextElement.class, new TextElementWrapper.Creator()),
			new ElementWrapperMapping<>(HeadingElement.class, new HeadingElementWrapper.Creator())
	);

	public static <T extends IContentElement> List<BaseElementWrapper<T>> create(int topLeftY, int width, int remainingPageHeight, int emptyPageHeight, T element) {
		//noinspection unchecked
		return MAPPINGS.stream().filter(m -> m.matches(element)).findFirst()
				.map(m -> m.construct(topLeftY, width, remainingPageHeight, emptyPageHeight, element))
				.orElse(Collections.emptyList());
	}

	public static List<List<BaseElementWrapper>> getPagedWrappedContent(List<IContentElement> elements, int width, int pageHeight) {
		List<List<BaseElementWrapper>> pagedWrappers = new ArrayList<>();
		int currentY = 0;
		List<BaseElementWrapper> currentPageWrappers = addNewPage(pagedWrappers);
		List<Tuple<IContentElement, List<BaseElementWrapper<IContentElement>>>> keepTogether = new ArrayList<>();
		for (IContentElement element : elements) {
			List<BaseElementWrapper<IContentElement>> pageElements = create(currentY, width, pageHeight - currentY, pageHeight, element);

			if (!pageElements.isEmpty() && pageElements.get(0).shouldKeepWithNext()) {
				keepTogether.add(new Tuple<>(element, pageElements));
				currentY += pageElements.get(0).getHeight();
				continue;
			} else if (!keepTogether.isEmpty()) {
				pageElements = addFirstToKeepAndRemoveFromCurrent(keepTogether, element, pageElements);
				currentY = addKeepToCurrentElements(width, pageHeight, keepTogether, pageElements);
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

	private static int addKeepToCurrentElements(int width, int pageHeight, List<Tuple<IContentElement, List<BaseElementWrapper<IContentElement>>>> keepTogether, List<BaseElementWrapper<IContentElement>> pageElements) {
		int currentY;
		boolean recalculateY = !areOnTheSamePage(keepTogether);
		if (recalculateY) {
			currentY = recalculateYPositions(width, pageHeight, keepTogether, pageElements);
		} else {
			ArrayList<BaseElementWrapper<IContentElement>> lastElements = new ArrayList<>(pageElements);
			pageElements.clear();
			keepTogether.stream().map(Tuple::getSecond).forEach(pageElements::addAll);
			pageElements.addAll(lastElements);
			currentY = pageElements.get(0).getTopLeftY(); //reset so that usual logic can again add up to this base value
		}
		return currentY;
	}

	private static List<BaseElementWrapper<IContentElement>> addFirstToKeepAndRemoveFromCurrent(List<Tuple<IContentElement, List<BaseElementWrapper<IContentElement>>>> keepTogether, IContentElement element, List<BaseElementWrapper<IContentElement>> pageElements) {
		if (!pageElements.isEmpty()) {
			keepTogether.add(new Tuple<>(element, ImmutableList.of(pageElements.get(0))));
			return pageElements.stream().skip(1).collect(Collectors.toList());
		}
		return pageElements;
	}

	private static int recalculateYPositions(int width, int pageHeight, List<Tuple<IContentElement, List<BaseElementWrapper<IContentElement>>>> keepTogether,
			List<BaseElementWrapper<IContentElement>> pageElements) {
		int updatedY = 0;
		List<BaseElementWrapper<IContentElement>> lastElements = new ArrayList<>(pageElements);
		pageElements.clear();
		for (Tuple<IContentElement, List<BaseElementWrapper<IContentElement>>> el : keepTogether) {
			List<BaseElementWrapper<IContentElement>> els = create(updatedY, width, pageHeight - updatedY, pageHeight, el.getFirst());
			if (!els.isEmpty()) {
				updatedY += els.get(els.size() - 1).getHeight();
			}
			pageElements.addAll(els);
		}
		pageElements.addAll(lastElements);

		return updatedY;
	}

	private static boolean areOnTheSamePage(List<Tuple<IContentElement, List<BaseElementWrapper<IContentElement>>>> keepTogether) {
		int currentY = 0;
		for (Tuple<IContentElement, List<BaseElementWrapper<IContentElement>>> element : keepTogether) {
			List<BaseElementWrapper<IContentElement>> wrappers = element.getSecond();

			if (wrappers.size() > 1 || (!wrappers.isEmpty() && wrappers.get(0).getTopLeftY() < currentY)) {
				return false;
			} else if (!wrappers.isEmpty()) {
				currentY = wrappers.get(0).getTopLeftY();
			}
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

		public List<BaseElementWrapper<T>> construct(int topLeftY, int width, int remainingPageHeight, int emptyPageHeight, T element) {
			return creator.construct(topLeftY, width, remainingPageHeight, emptyPageHeight, element);
		}
	}
}
