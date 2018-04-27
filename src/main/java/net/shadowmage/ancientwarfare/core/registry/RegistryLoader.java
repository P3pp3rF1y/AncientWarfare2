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

public class RegistryLoader {
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
		Loader.instance().getActiveModList().forEach(m -> loadRegistries(m, m.getSource(), "assets/" + m.getModId() + "/registry"));

		Loader.instance().setActiveModContainer(awModContainer);
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
		if (!parsers.containsKey(shortName)) {
			AncientWarfareCore.log.error("No parser defined for file name ", shortName);
		}

		IRegistryDataParser parser = parsers.get(shortName);
		BufferedReader reader = null;
		try {
			reader = Files.newBufferedReader(file);
			JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
			if (json != null && (!JsonUtils.hasField(json, "mod") || Loader.isModLoaded(JsonUtils.getString(json, "mod")))) {
				parser.parse(json);
			}
		}
		catch (JsonParseException e) {
			AncientWarfareCore.log.error("Parsing error loading registry {}", key, e);
		}
		catch (IOException e) {
			AncientWarfareCore.log.error("Couldn't read registry {} from {}", key, file, e);
		}
		finally {
			IOUtils.closeQuietly(reader);
		}
	}
}
