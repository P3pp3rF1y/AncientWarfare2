package net.shadowmage.ancientwarfare.core.util;

import java.util.ArrayList;
import java.util.Iterator;

/*
 * Cover for objects in an ordered list
 * Replicates common methods from the underlying ArrayList, adding fail-safe range checks.
 * Adds moving objects up/down in the list.
 */
public class OrderingList<T> implements Iterable<T> {
	protected final ArrayList<T> points = new ArrayList<>();

	public final void increment(int index) {
		if (index >= 1 && index < points.size()) {
			T entry = points.remove(index);
			points.add(index - 1, entry);
		}
	}

	public final void decrement(int index) {
		if (index >= 0 && index < points.size() - 1) {
			T entry = points.remove(index);
			points.add(index + 1, entry);
		}
	}

	public final void add(T point) {
		if (point != null)
			points.add(point);
	}

	public final T get(int index) {
		return points.get(index);
	}

	public final void remove(int index) {
		if (index < 0 || index >= points.size()) {
			return;
		}
		points.remove(index);
	}

	public final void clear() {
		points.clear();
	}

	public final int size() {
		return points.size();
	}

	public final boolean isEmpty() {
		return points.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return points.iterator();
	}
}
