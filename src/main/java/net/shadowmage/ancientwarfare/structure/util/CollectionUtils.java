package net.shadowmage.ancientwarfare.structure.util;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.function.ToIntFunction;

public class CollectionUtils {
	private CollectionUtils() {}

	public static <T> Optional<T> getWeightedRandomElement(Random rng, Collection<T> collection, ToIntFunction<T> getElementWeight, WeightedRandomLogger<T> logger) {
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

		logger.log(totalWeight, toReturn);

		return Optional.ofNullable(toReturn);
	}

	public interface WeightedRandomLogger<T> {
		void log(int totalWeight, @Nullable T selected);
	}
}
