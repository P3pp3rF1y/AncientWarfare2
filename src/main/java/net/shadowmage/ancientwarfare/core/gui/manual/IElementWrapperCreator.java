package net.shadowmage.ancientwarfare.core.gui.manual;

import net.shadowmage.ancientwarfare.core.manual.IContentElement;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

interface IElementWrapperCreator<T extends IContentElement> {
	PageElements<T> construct(int topLeftY, int width, int remainingPageHeight, int emptyPageHeight, T element);

	class PageElements<T extends IContentElement> {
		@Nullable
		private final BaseElementWrapper<T> currentPageElement;
		@Nullable
		private final List<BaseElementWrapper<T>> nextPageElement;

		public PageElements(@Nullable BaseElementWrapper<T> currentPageElement, @Nullable List<BaseElementWrapper<T>> nextPageElement) {

			this.currentPageElement = currentPageElement;
			this.nextPageElement = nextPageElement;
		}

		public Optional<BaseElementWrapper<T>> getCurrent() {
			return Optional.ofNullable(currentPageElement);
		}

		public Optional<List<BaseElementWrapper<T>>> getNext() {
			return Optional.ofNullable(nextPageElement);
		}
	}
}
