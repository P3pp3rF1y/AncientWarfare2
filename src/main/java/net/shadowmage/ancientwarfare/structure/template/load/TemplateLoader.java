package net.shadowmage.ancientwarfare.structure.template.load;

import net.minecraftforge.fml.common.Loader;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.FileUtils;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructures;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate;
import net.shadowmage.ancientwarfare.structure.town.TownTemplateManager;
import net.shadowmage.ancientwarfare.structure.town.TownTemplateParser;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TemplateLoader {

	private static final String DEFAULT_TEMPLATE_DIRECTORY = "assets/ancientwarfare/template";
	public static final String OUTPUT_DIRECTORY = AWCoreStatics.configPathForFiles + "structures/export/";
	public static final String INCLUDE_DIRECTORY = AWCoreStatics.configPathForFiles + "structures/included/";

	private List<TownTemplate> parsedTownTemplates = new ArrayList<>();

	public static final TemplateLoader INSTANCE = new TemplateLoader();

	private TemplateLoader() {
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void initializeAndExportDefaults() {
		/*
		 * create default dirs if they don't exist...
         */
		File existTest = new File(OUTPUT_DIRECTORY);
		if (!existTest.exists()) {
			AWLog.log("Creating default Export Directory");
			existTest.mkdirs();
		}

		existTest = new File(INCLUDE_DIRECTORY);
		if (!existTest.exists()) {
			AWLog.log("Creating default Include Directory");
			existTest.mkdirs();
		}
	}

	public void loadTemplates() {
		int loadedCount = 0;
		if (AWStructureStatics.loadDefaultPack) {
			//noinspection ConstantConditions
			loadedCount += loadTemplatesFromSource(Loader.instance().activeModContainer().getSource(), DEFAULT_TEMPLATE_DIRECTORY);
		}
		loadedCount += loadTemplatesFromSource(new File(INCLUDE_DIRECTORY), "");

		AWLog.log("Loaded " + loadedCount + " structure(s)");

		validateTownTemplates();
	}

	public void reloadAll() {
		StructureTemplateManager.INSTANCE.removeAll();
		TownTemplateManager.INSTANCE.removeAll();

		loadTemplates();
	}

	private void loadStructureImage(String imageName, InputStream is) {
		String img = FilenameUtils.getName(imageName);
		try {
			BufferedImage image = ImageIO.read(is);
			if (image != null && image.getWidth() == AWStructureStatics.structureImageWidth && image.getHeight() == AWStructureStatics.structureImageHeight) {
				StructureTemplateManager.INSTANCE.addTemplateImage(img, image);
				AWLog.logDebug("loaded structure image of: " + img);
			} else {
				if (image == null) {
					AncientWarfareStructures.LOG.error("Error loading image {}", img);
				} else {
					AWLog.logError("Attempted to load improper sized template image: " + img + " with dimensions of: " + image.getWidth() + "x" + image.getHeight() + ".  Specified width/height is: " + AWStructureStatics.structureImageWidth + "x" + AWStructureStatics.structureImageHeight);
				}
			}
		}
		catch (IOException e) {
			AncientWarfareStructures.LOG.error("Error loading image {}", img);
		}
	}

	private int loadTemplatesFromSource(File source, String base) {
		AtomicInteger loaded = new AtomicInteger(0);
		FileUtils.findFiles(source, base, (root, file) -> {
			String relative = root.relativize(file).toString();

			String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");

			String extension = FilenameUtils.getExtension(file.toString());

			if (extension.equals("png") || extension.equals("jpg")) {
				try (InputStream stream = Files.newInputStream(file)) {
					loadStructureImage(name, stream);
				}
				catch (IOException e) {
					AncientWarfareCore.log.error("Couldn't read image data {} from {}", name, file, e);
				}
			} else if (extension.equals(AWStructureStatics.townTemplateExtension) || extension.equals(AWStructureStatics.templateExtension)) {
				List<String> lines;
				try (BufferedReader reader = Files.newBufferedReader(file)) {
					lines = reader.lines().filter(l -> !l.startsWith("#")).collect(Collectors.toList());
					if (extension.equals(AWStructureStatics.townTemplateExtension)) {
						loadTownTemplate(lines);
					} else {
						loaded.addAndGet(loadTemplate(file.toString(), lines));
					}
				}
				catch (IOException e) {
					AncientWarfareCore.log.error("Couldn't read template data {} from {}", name, file, e);
				}
				catch (UncheckedIOException e) {
					AncientWarfareCore.log.error("Couldn't read template data {} from {} - most probable cause is incorrect encoding, should be ISO-8859-1", name, file, e);
				}
			}
		});
		return loaded.get();
	}

	private int loadTemplate(String fileName, List<String> lines) {
		StructureTemplate template = TemplateParser.INSTANCE.parseTemplate(fileName, lines);
		if (template != null) {
			AWLog.log("Loaded Structure Template: [" + template.name + "] WorldGen: " + template.getValidationSettings().isWorldGenEnabled() + "  Survival: " + template.getValidationSettings().isSurvival());
			StructureTemplateManager.INSTANCE.addTemplate(template);
			return 1;
		}
		return 0;
	}

	private void loadTownTemplate(List<String> lines) {
		TownTemplate template = TownTemplateParser.parseTemplate(lines);
		if (template != null) {
			parsedTownTemplates.add(template);
		}
	}

	private void validateTownTemplates() {
		if (!this.parsedTownTemplates.isEmpty()) {
			AWLog.log("Loading Town Templates: ");
			for (TownTemplate t : this.parsedTownTemplates) {
				AWLog.log("Loading town template: " + t.getTownTypeName());
				t.validateStructureEntries();
				TownTemplateManager.INSTANCE.loadTemplate(t);
			}
			AWLog.log("Loaded : " + this.parsedTownTemplates.size() + " Town Templates.");
		}
	}
}
