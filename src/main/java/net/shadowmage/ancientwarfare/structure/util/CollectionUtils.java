package net.shadowmage.ancientwarfare.structure.util;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.function.ToIntFunction;

public class CollectionUtils {
	private CollectionUtils() {}

	public static <T> Optional<T> getWeightedRandomElement(Random rng, Collection<T> collection, ToIntFunction<T> getElementWeight) {
		int totalWeight = 0;
		for (T t : collection) {
			totalWeight += getElementWeight.applyAsInt(t);
		}
		int rnd = rng.nextInt(totalWeight + 1);
		T toReturn = null;
		for (T t : collection) {
			rnd -= getElementWeight.applyAsInt(t);
			if (rnd <= 0) {
				toReturn = t;
				break;
			}
		}
		return Optional.ofNullable(toReturn);
	}
}
