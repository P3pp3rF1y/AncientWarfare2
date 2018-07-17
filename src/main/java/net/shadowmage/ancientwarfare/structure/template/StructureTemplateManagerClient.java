package net.shadowmage.ancientwarfare.structure.template;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.TextureImageBased;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@SideOnly(Side.CLIENT)
public class StructureTemplateManagerClient {

	private HashMap<String, ResourceLocation> clientTemplateImages = new HashMap<>();
	private HashMap<String, StructureTemplateClient> clientTemplates = new HashMap<>();

	private StructureTemplateManagerClient() {
	}

	private static final StructureTemplateManagerClient instance = new StructureTemplateManagerClient();

	public static StructureTemplateManagerClient instance() {
		return instance;
	}

	public void onTemplateData(NBTTagCompound tag) {
		if (tag.hasKey("singleStructure")) {
			NBTTagCompound structureTag = tag.getCompoundTag("singleStructure");
			readClientStructure(structureTag);
		} else {
			clientTemplateImages.clear();
			clientTemplates.clear();
			NBTTagList list = tag.getTagList("structureList", Constants.NBT.TAG_COMPOUND);
			NBTTagCompound structureTag;
			for (int i = 0; i < list.tagCount(); i++) {
				structureTag = list.getCompoundTagAt(i);
				readClientStructure(structureTag);
			}
		}
	}

	private void readClientStructure(NBTTagCompound tag) {
		StructureTemplateClient template = StructureTemplateClient.readFromNBT(tag);
		addTemplate(template);
	}

	public void removeTemplate(String name) {
		this.clientTemplates.remove(name);
		this.clientTemplateImages.remove(name);
	}

	public Collection<StructureTemplateClient> getClientStructures() {
		return clientTemplates.values();
	}

	public List<StructureTemplateClient> getSurvivalStructures() {
		List<StructureTemplateClient> clientStructures = new ArrayList<>();
		for (StructureTemplateClient t : this.clientTemplates.values()) {
			if (t.survival) {
				clientStructures.add(t);
			}
		}
		return clientStructures;
	}

	public boolean templateExists(String name) {
		return clientTemplates.containsKey(name);
	}

	public StructureTemplateClient getClientTemplate(String name) {
		return clientTemplates.get(name);
	}

	public void addTemplate(StructureTemplateClient template) {
		clientTemplates.put(template.name, template);
		loadTemplateImage(template.name, template.name + ".jpg");
	}

	public ResourceLocation getImageFor(String templateName) {
		return clientTemplateImages.get(templateName + ".jpg");
	}

	private void loadTemplateImage(String templateName, String imageName) {
		String pathBase = AWCoreStatics.configPathForFiles + "structures/image_cache/";
		File file = new File(pathBase + imageName);
		ResourceLocation loc = new ResourceLocation("ancientwarfare", pathBase + imageName);

		if (!file.exists()) {
			BufferedImage image = StructureTemplateManager.INSTANCE.getTemplateImage(templateName);
			if (image != null) {
				Minecraft.getMinecraft().renderEngine.loadTexture(loc, new TextureImageBased(loc, image));
				clientTemplateImages.put(imageName, loc);
			}
		} else {
			try {
				BufferedImage image = ImageIO.read(file);
				if (image.getWidth() == AWStructureStatics.structureImageWidth && image.getHeight() == AWStructureStatics.structureImageHeight) {
					Minecraft.getMinecraft().renderEngine.loadTexture(loc, new TextureImageBased(loc, image));
					clientTemplateImages.put(imageName, loc);
				} else {
					AWLog.logError("Error parsing image: " + file.getName() + " image was not of correct size. Found: " + image.getWidth() + "x" + image.getHeight() + "  Needed: " + AWStructureStatics.structureImageWidth + "x" + AWStructureStatics.structureImageHeight);
				}
			}
			catch (IOException e) {
				//noop
			}
		}
	}
}
