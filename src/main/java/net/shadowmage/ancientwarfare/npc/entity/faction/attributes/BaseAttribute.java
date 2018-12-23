package net.shadowmage.ancientwarfare.npc.entity.faction.attributes;

public abstract class BaseAttribute<T> implements IAdditionalAttribute<T> {
	private String name;

	public BaseAttribute(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
