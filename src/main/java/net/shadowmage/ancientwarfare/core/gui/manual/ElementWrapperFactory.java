package net.shadowmage.ancientwarfare.core.gui.manual;

import com.google.common.collect.ImmutableSet;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.gui.manual.IElementWrapperCreator.PageElements;
import net.shadowmage.ancientwarfare.core.manual.IContentElement;
import net.shadowmage.ancientwarfare.core.manual.TextElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class ElementWrapperFactory {
	private ElementWrapperFactory() {}

	private static final Set<ElementWrapperMapping> MAPPINGS = ImmutableSet.of(
			new ElementWrapperMapping<>(TextElement.class, new TextElementWrapper.Creator())
	);

	public static <T extends IContentElement> PageElements<T> create(int topLeftY, int width, int remainingPageHeight, int emptyPageHeight, T element) {
		//noinspection unchecked
		return MAPPINGS.stream().filter(m -> m.matches(element)).findFirst()
				.map(m -> m.construct(topLeftY, width, remainingPageHeight, emptyPageHeight, element))
				.orElse(new PageElements(null, null));
	}

	public static List<List<BaseElementWrapper>> getPagedWrappedContent(List<IContentElement> elements, int width, int pageHeight) {
		List<List<BaseElementWrapper>> pagedWrappers = new ArrayList<>();
		int currentY = 0;
		List<BaseElementWrapper> currentPageWrappers = addNewPage(pagedWrappers);
		for (IContentElement element : elements) {
			PageElements<IContentElement> pageElements = create(currentY, width, pageHeight - currentY, pageHeight, element);

			Optional<BaseElementWrapper<IContentElement>> current = pageElements.getCurrent();
			if (current.isPresent()) {
				currentPageWrappers.add(current.get());
				currentY += current.get().getHeight();
			}

			Optional<List<BaseElementWrapper<IContentElement>>> next = pageElements.getNext();
			if (next.isPresent()) {
				for (BaseElementWrapper wrapper : next.get()) {
					currentPageWrappers = addNewPage(pagedWrappers);

					currentPageWrappers.add(wrapper);
					currentY = wrapper.getHeight();
				}
			}
		}

		return pagedWrappers;
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

		public PageElements<T> construct(int topLeftY, int width, int remainingPageHeight, int emptyPageHeight, T element) {
			return creator.construct(topLeftY, width, remainingPageHeight, emptyPageHeight, element);
		}
	}
}
