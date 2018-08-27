package net.shadowmage.ancientwarfare.structure.template.datafixes;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FixResult<T> {
	private final T data;
	private final boolean modified;
	private final Set<String> fixesApplied;

	private FixResult(T data, boolean modified, Set<String> fixesApplied) {
		this.data = data;
		this.modified = modified;
		this.fixesApplied = fixesApplied;
	}

	public Set<String> getFixesApplied() {
		return fixesApplied;
	}

	public static class Modified<U> extends FixResult<U> {
		public Modified(U data, String fixName) {
			super(data, true, Collections.singleton(fixName));
		}
	}

	public static class NotModified<U> extends FixResult<U> {
		public NotModified(U data) {
			super(data, false, Collections.emptySet());
		}
	}

	public boolean isModified() {
		return modified;
	}

	public T getData() {
		return data;
	}

	public static class Builder<T> {
		private boolean modified = false;
		private Set<String> fixesApplied = new HashSet<>();

		public <U> U updateAndGetData(FixResult<U> otherResult) {
			modified |= otherResult.isModified();
			fixesApplied.addAll(otherResult.getFixesApplied());
			return otherResult.getData();
		}

		public FixResult<T> build(T data) {
			return new FixResult<>(data, modified, fixesApplied);
		}
	}
}
