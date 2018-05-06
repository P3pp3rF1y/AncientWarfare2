package net.shadowmage.ancientwarfare.core.util;

public interface TriFunction<K, V, S, R> {
	R apply(K k, V v, S s);
}
