package net.shadowmage.ancientwarfare.npc.entity.faction.attributes;

import java.util.Optional;

public class FloatAttribute extends BaseAttribute<Float> {
	FloatAttribute(String name) {
		super(name);
	}

	@Override
	public Class<Float> getValueClass() {
		return Float.class;
	}

	@Override
	public Optional<Float> parseValue(String value) {
		try {
			return Optional.of(Float.parseFloat(value));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}
}
