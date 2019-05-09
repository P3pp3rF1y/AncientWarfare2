package net.shadowmage.ancientwarfare.structure.template.build.validation.border.points;

public enum PointType {
	REFERENCE_POINT("R"),
	OUTER_BORDER("O"),
	SMOOTHED_BORDER("S"),
	STRUCTURE_BORDER("B"),
	STRUCTURE_INSIDE("I");

	PointType(String acronym) {
		this.acronym = acronym;
	}

	private String acronym;

	public String getAcronym() {
		return acronym;
	}
}
