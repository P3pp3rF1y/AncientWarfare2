package net.shadowmage.ancientwarfare.structure.template.datafixes;

public class FixResult<T> {
	private final T data;
	private final boolean modified;

	public FixResult(T data, boolean modified) {
		this.data = data;
		this.modified = modified;
	}

	public boolean isModified() {
		return modified;
	}

	public T getData() {
		return data;
	}
}
