package net.shadowmage.ancientwarfare.npc.entity.faction.attributes;

import java.util.Optional;

public class StringAttribute extends BaseAttribute<String> {
	public StringAttribute(String name) {
		super(name);
	}

	@Override
	public Class<String> getValueClass() {
		return String.class;
	}

	@Override
	public Optional<String> parseValue(String value) {
		return Optional.of(value);
	}
}
