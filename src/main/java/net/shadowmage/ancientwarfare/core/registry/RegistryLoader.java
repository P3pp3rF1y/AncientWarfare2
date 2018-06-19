package net.shadowmage.ancientwarfare.core.registry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Optional;

public class RegistryLoader {
	private RegistryLoader() {}

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static final Map<String, IRegistryDataParser> parsers = new HashMap<>();

	public static void registerParser(IRegistryDataParser parser) {
		parsers.put(parser.getName(), parser);
	}

	private static final List<ResourceLocation> loadedRegistries = new ArrayList<>();

	public static void load() {
		//noinspection ConstantConditions
		ModContainer awModContainer = Loader.instance().activeModContainer();

		Path registryOverridesFolder = new File(AWCoreStatics.configPathForFiles + "registry").toPath();
		if (registryOverridesFolder.toFile().exists()) {
			//noinspection ConstantConditions
			loadRegistries(awModContainer, registryOverridesFolder);
		}
		loadRegistries(awModContainer, awModContainer.getSource(), "assets/" + awModContainer.getModId() + "/registry");
	}

	private static void loadRegistries(ModContainer mod, File source, String base) {
		if (!(source.isDirectory() || source.isFile())) {
			return;
		}

		FileSystem fs = null;
		try {
			Path root;
			if (source.isFile()) {
				fs = FileSystems.newFileSystem(source.toPath(), null);
				root = fs.getPath("/" + base);
			} else {
				root = source.toPath().resolve(base);
			}

			loadRegistries(mod, root);
		}
		catch (IOException e) {
			AncientWarfareCore.log.error("Error loading FileSystem from jar: ", e);
		}
		finally {
			IOUtils.closeQuietly(fs);
		}
	}

	private static void loadRegistries(ModContainer mod, Path root) {
		if (!Files.exists(root)) {
			return;
		}

		Iterator<Path> itr;
		try {
			itr = Files.walk(root).iterator();
		}
		catch (IOException e) {
			AncientWarfareCore.log.error("Error iterating filesystem for: {}", root, e);
			return;
		}
		while (itr != null && itr.hasNext()) {
			loadFile(mod, root, itr.next());
		}
	}

	private static void loadFile(ModContainer mod, Path root, Path file) {
		Loader.instance().setActiveModContainer(mod);

		String relative = root.relativize(file).toString();
		if (!"json".equals(FilenameUtils.getExtension(file.toString())))
			return;

		String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
		ResourceLocation key = new ResourceLocation(mod.getModId(), name);

		if (loadedRegistries.contains(key)) {
			AncientWarfareCore.log.info("Registry {} has already been loaded in overrides, skipping...", key.toString());
			return;
		}
		loadedRegistries.add(key);

		String shortName = name.substring(name.lastIndexOf('/') + 1);

		BufferedReader reader = null;
		try {
			reader = Files.newBufferedReader(file);
			JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
			Optional<IRegistryDataParser> parser = getParser(shortName, json);

			if (!parser.isPresent()) {
				AncientWarfareCore.log.error("No parser defined for file name {}", shortName);
				return;
			}

			if (json == null || isDisabled(json) || !isModLoaded(json)) {
				return;
			}

			parser.get().parse(json);
		}
		catch (JsonParseException e) {
			AncientWarfareCore.log.error("Parsing error loading registry {}", key, e);
		}
		catch (MissingResourceException e) {
			AncientWarfareCore.log.error(e.getMessage());
		}
		catch (IOException e) {
			AncientWarfareCore.log.error("Couldn't read registry {} from {}", key, file, e);
		}
		finally {
			IOUtils.closeQuietly(reader);
		}
	}

	private static boolean isModLoaded(JsonObject json) {
		return !JsonUtils.hasField(json, "mod") || Loader.isModLoaded(JsonUtils.getString(json, "mod"));
	}

	private static boolean isDisabled(JsonObject json) {
		return json.has("disabled") && JsonUtils.getBoolean(json, "disabled");
	}

	private static Optional<IRegistryDataParser> getParser(String fileName, JsonObject json) {
		String parserName = fileName;
		if (json.has("type")) {
			parserName = JsonUtils.getString(json, "type");
		}
		return parsers.containsKey(parserName) ? Optional.of(parsers.get(parserName)) : Optional.empty();
	}
}
