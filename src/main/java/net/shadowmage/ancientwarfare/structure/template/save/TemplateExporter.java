package net.shadowmage.ancientwarfare.structure.template.save;

import net.minecraft.util.math.Vec3i;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.api.TemplateRule;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class TemplateExporter {
	private TemplateExporter() {}

	public static boolean exportTo(StructureTemplate template, File directory) {
		File exportFile = new File(directory, template.name + "." + AWStructureStatics.templateExtension);
		if (!exportFile.exists()) {
			try {
				if (!exportFile.createNewFile()) {
					return false;
				}
			}
			catch (IOException e) {
				AncientWarfareStructure.LOG.error("Could not export template..could not create file : " + exportFile.getAbsolutePath());
				return false;
			}
		}
		try (FileWriter fileWriter = new FileWriter(exportFile); BufferedWriter writer = new BufferedWriter(fileWriter)) {

			writeHeader(template, writer);
			writeValidationSettings(template.getValidationSettings(), writer);
			writeLayers(template, writer);

			writer.write("#### RULES ####");
			writer.newLine();
			TemplateRule[] templateRules = template.getTemplateRules();
			for (TemplateRule rule : templateRules) {
				if (rule != null) { //TODO replace templateRules and entityRules with List/Set structures instead of relying and copying to arrays
					StructurePluginManager.writeRuleLines(rule, writer, "rule");
				}
			}
			writer.write("#### ENTITIES ####");
			writer.newLine();
			templateRules = template.getEntityRules();
			for (TemplateRule rule : templateRules) {
				StructurePluginManager.writeRuleLines(rule, writer, "entity");
			}
		}
		catch (IOException e) {
			AncientWarfareStructure.LOG.error("Could not export template..could not create file : " + exportFile.getAbsolutePath());
			return false;
		}
		return true;
	}

	private static void writeValidationSettings(StructureValidator settings, BufferedWriter writer) throws IOException {
		writer.write("#### VALIDATION ####");
		writer.newLine();
		writer.write("validation:");
		writer.newLine();
		StructureValidator.writeValidator(writer, settings);
		writer.write(":endvalidation");
		writer.newLine();
		writer.newLine();
	}

	private static void writeHeader(StructureTemplate template, BufferedWriter writer) throws IOException {
		Calendar cal = Calendar.getInstance();
		writer.write("# Ancient Warfare Structure Template File");
		writer.newLine();
		writer.write("# Auto-generated structure template file. created on: " + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.YEAR) + " at: " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));
		writer.newLine();
		writer.write("# Lines beginning with # denote comments");
		writer.newLine();
		writer.newLine();
		writer.write("header:");
		writer.newLine();
		writer.write("version=" + StructureTemplate.CURRENT_VERSION.getMajor() + "." + StructureTemplate.CURRENT_VERSION.getMinor());
		writer.newLine();
		writer.write("name=" + template.name);
		writer.newLine();
		writer.write("size=" + template.getSize().getX() + "," + template.getSize().getY() + "," + template.getSize().getZ());
		writer.newLine();
		writer.write("offset=" + template.getOffset().getX() + "," + template.getOffset().getY() + "," + template.getOffset().getZ());
		writer.newLine();
		writer.write(":endheader");
		writer.newLine();
		writer.newLine();
	}

	private static void writeLayers(StructureTemplate template, BufferedWriter writer) throws IOException {
		writer.write("#### LAYERS ####");
		writer.newLine();
		for (int y = 0; y < template.getSize().getY(); y++) {
			writer.write("layer: " + y);
			writer.newLine();
			for (int z = 0; z < template.getSize().getZ(); z++) {
				for (int x = 0; x < template.getSize().getX(); x++) {
					short data = template.getTemplateData()[StructureTemplate.getIndex(new Vec3i(x, y, z), template.getSize())];
					writer.write(String.valueOf(data));
					if (x < template.getSize().getX() - 1) {
						writer.write(",");
					}
				}
				writer.newLine();
			}
			writer.write(":endlayer");
			writer.newLine();
		}
		writer.newLine();
	}

}
