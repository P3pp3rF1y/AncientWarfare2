package net.shadowmage.ancientwarfare.npc.entity.faction.attributes;

import java.util.Optional;

public class BoolAttribute extends BaseAttribute<Boolean> {
	public BoolAttribute(String name) {
		super(name);
	}

	@Override
	public Class<Boolean> getValueClass() {
		return Boolean.class;
	}

	@Override
	public Optional<Boolean> parseValue(String value) {
		return !"true".equals(value) && !"false".equals(value) ? Optional.empty() : Optional.of(Boolean.valueOf(value));
	}
}
