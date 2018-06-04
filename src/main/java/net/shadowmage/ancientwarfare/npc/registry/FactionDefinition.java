package net.shadowmage.ancientwarfare.npc.registry;

public class FactionDefinition {
	private String name;
	private int color;

	public FactionDefinition(String name, int color) {
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public int getColor() {
		return color;
	}
}