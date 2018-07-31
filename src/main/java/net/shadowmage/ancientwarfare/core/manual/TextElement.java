package net.shadowmage.ancientwarfare.core.manual;

public class TextElement implements IContentElement {
	private String text;

	public TextElement(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
