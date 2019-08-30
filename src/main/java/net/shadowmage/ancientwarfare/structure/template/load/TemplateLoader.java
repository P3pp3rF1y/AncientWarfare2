package net.shadowmage.ancientwarfare.structure.template.load;

import net.minecraftforge.fml.common.Loader;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.util.FileUtils;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;
import net.shadowmage.ancientwarfare.structure.template.save.TemplateExporter;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate;
import net.shadowmage.ancientwarfare.structure.town.TownTemplateManager;
import net.shadowmage.ancientwarfare.structure.town.TownTemplateParser;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
			existTest.mkdirs();
		}

		existTest = new File(INCLUDE_DIRECTORY);
		if (!existTest.exists()) {
			existTest.mkdirs();
		}
	}

	public void loadTemplates() {
		int loadedCount = 0;
		if (AWStructureStatics.loadDefaultPack) {
			//noinspection ConstantConditions
			loadedCount += loadTemplatesFromSource(Loader.instance().activeModContainer().getSource(), DEFAULT_TEMPLATE_DIRECTORY, false);
		}
		loadedCount += loadTemplatesFromSource(new File(INCLUDE_DIRECTORY), "", true);

		AncientWarfareStructure.LOG.info("Loaded {} structure(s)", loadedCount);

		validateTownTemplates();
	}

	public void reloadAll() {
		StructureTemplateManager.removeAll();
		TownTemplateManager.INSTANCE.removeAll();

		loadTemplates();
	}

	private int loadTemplatesFromSource(File source, String base, boolean saveFixedTemplate) {
		AtomicInteger loaded = new AtomicInteger(0);
		FileUtils.findFiles(source, base, (root, file) -> {
			String relative = root.relativize(file).toString();

			String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");

			String extension = FilenameUtils.getExtension(file.toString());

			if (extension.equals(AWStructureStatics.townTemplateExtension) || extension.equals(AWStructureStatics.templateExtension)) {
				List<String> lines;
				try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.ISO_8859_1)) {
					lines = reader.lines().filter(l -> !l.startsWith("#")).collect(Collectors.toList());
					if (extension.equals(AWStructureStatics.townTemplateExtension)) {
						loadTownTemplate(lines);
					} else {
						loaded.addAndGet(loadTemplate(file, lines, saveFixedTemplate));
					}
				}
				catch (IOException e) {
					AncientWarfareStructure.LOG.error("Couldn't read template data {} from {}", name, file, e);
				}
				catch (UncheckedIOException e) {
					AncientWarfareStructure.LOG.error("Couldn't read template data {} from {} - most probable cause is incorrect encoding, should be ISO-8859-1", name, file, e);
				}
			}
		});
		return loaded.get();
	}

	private int loadTemplate(Path fileName, List<String> lines, boolean saveFixedTemplate) {
		Optional<FixResult<StructureTemplate>> result = TemplateParser.INSTANCE.parseTemplate(fileName.toString(), lines);
		if (!result.isPresent()) {
			return 0;
		}

		FixResult<StructureTemplate> loadedTemplate = result.get();
		StructureTemplate template = loadedTemplate.getData();

		if (loadedTemplate.isModified()) {
			AncientWarfareStructure.LOG.info("Template {} had following fixes applied: {}", () -> fileName,
					() -> String.join(", ", loadedTemplate.getFixesApplied()));
		}

		if (saveFixedTemplate && loadedTemplate.isModified()) {
			TemplateExporter.exportTo(template, fileName.getParent().toFile());
			AncientWarfareStructure.LOG.info("Changes saved to {}", fileName);
		}

		if (template != null) {
			AncientWarfareStructure.LOG.info("Loaded Structure Template: [{}] WorldGen: {}  Survival: {}", template.name, template.getValidationSettings().isWorldGenEnabled(), template.getValidationSettings().isSurvival());
			StructureTemplateManager.addTemplate(template);
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
			AncientWarfareStructure.LOG.info("Loading Town Templates: ");
			for (TownTemplate t : this.parsedTownTemplates) {
				AncientWarfareStructure.LOG.info("Loading town template: " + t.getTownTypeName());
				t.validateStructureEntries();
				TownTemplateManager.INSTANCE.loadTemplate(t);
			}
			AncientWarfareStructure.LOG.info("Loaded : {} Town Templates.", parsedTownTemplates.size());
		}
	}
}
