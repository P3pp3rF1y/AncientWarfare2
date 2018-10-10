package net.shadowmage.ancientwarfare.npc.entity.faction.attributes;

import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

import java.util.Optional;

public class ClassAttribute extends BaseAttribute<Class> {
	private Class baseClass;

	public ClassAttribute(String name, Class baseClass) {
		super(name);
		this.baseClass = baseClass;
	}

	@Override
	public Class<Class> getValueClass() {
		return Class.class;
	}

	@Override
	public Optional<Class> parseValue(String value) {
		Class clazz;
		try {
			clazz = Class.forName(value);
		}
		catch (ClassNotFoundException e) {
			AncientWarfareNPC.LOG.error("Horse entity class was not found for: " + value);
			return Optional.empty();
		}

		if (!baseClass.isAssignableFrom(clazz)) {
			return Optional.empty();
		}

		return Optional.of(clazz);
	}
}
