package net.shadowmage.ancientwarfare.core.manual;

import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;

public class ImageElement implements IContentElement {
	private String path;
	private final int width;
	private final int height;

	private ImageElement(String path, int width, int height) {
		this.path = path;
		this.width = width;
		this.height = height;
	}

	public static ImageElement parse(JsonObject elementJson) {
		return new ImageElement(JsonUtils.getString(elementJson, "path"), JsonUtils.getInt(elementJson, "width"),
				JsonUtils.getInt(elementJson, "height"));
	}

	public String getPath() {
		return path;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
