package net.shadowmage.ancientwarfare.npc.entity.faction.attributes;

import java.util.Optional;

public interface IAdditionalAttribute<T> {
	String getName();

	Class<T> getValueClass();

	Optional<T> parseValue(String value);
}
