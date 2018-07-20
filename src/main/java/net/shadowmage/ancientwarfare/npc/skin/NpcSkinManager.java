package net.shadowmage.ancientwarfare.npc.skin;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.util.FileUtils;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class NpcSkinManager {

	public static final NpcSkinManager INSTANCE = new NpcSkinManager();

	private final HashMap<String, List<ResourceLocation>> npcSkins = new HashMap<>();

	private final Random rng = new Random();

	private static final String SKINS_CONFIG_PATH = AWCoreStatics.configPathForFiles + "npc/skins/";
	private static final String DEFAULT_SKINS = "assets/ancientwarfare/skin_pack";

	public Optional<ResourceLocation> getTextureFor(NpcBase npc) {
		long id = npc.getIDForSkin();
		if (!npc.getCustomTex().isEmpty()) {
			Optional<ResourceLocation> texture = getNpcTexture(npc.getCustomTex(), id);
			if (texture.isPresent()) {
				return texture;
			}
		}
		return getNpcTexture(npc.getNpcFullType(), id);
	}

	private Optional<ResourceLocation> getNpcTexture(String type, long idlsb) {
		rng.setSeed(idlsb);
		return npcSkins.containsKey(type) ? Optional.of(npcSkins.get(type).get(rng.nextInt(npcSkins.get(type).size()))) : Optional.empty();
	}

	public void loadSkins() {
		//noinspection ConstantConditions
		loadSkinsFromSource(Loader.instance().activeModContainer().getSource(), DEFAULT_SKINS);
		loadSkinsFromSource(new File(SKINS_CONFIG_PATH), "");
	}

	private void loadSkinsFromSource(File source, String base) {
		HashMap<String, Set<String>> imageMap = new HashMap<>();

		FileUtils.findFiles(source, base, root -> {
			Path fPath = root.resolve("skin_pack.meta");
			if (fPath != null && fPath.toFile().exists()) {
				try (BufferedReader reader = Files.newBufferedReader(fPath)) {
					readImageMap(reader, imageMap);
				}
				catch (IOException e) {
					AncientWarfareCore.log.error("Error loading skin_pack.meta: ", e);
					return false;
				}
			}
			return true;
		}, (root, file) -> {
			String relative = root.relativize(file).toString();

			String extension = FilenameUtils.getExtension(file.toString());

			if (extension.equals("png")) {
				try (InputStream stream = Files.newInputStream(file)) {
					imageMap.entrySet().stream().filter(e -> e.getValue().contains(relative)).findFirst()
							.ifPresent(e -> addNpcSkin(e.getKey(), loadSkinImage(relative, stream)));
				}
				catch (IOException e) {
					AncientWarfareCore.log.error("Error loading image {}: ", relative, e);
				}
			}
		});
	}

	private void readImageMap(BufferedReader br, HashMap<String, Set<String>> imageMap) throws IOException {
		while (br.ready()) {
			String line = br.readLine();
			String[] lineBits = line.split("=");
			if (lineBits.length > 1) {
				if (!imageMap.containsKey(lineBits[0])) {
					imageMap.put(lineBits[0], new HashSet<>());
				}
				imageMap.get(lineBits[0]).add(lineBits[1]);
			}
		}
	}

	private void addNpcSkin(String npcType, ResourceLocation texture) {
		if (!npcSkins.containsKey(npcType)) {
			npcSkins.put(npcType, new ArrayList<>());
		}
		npcSkins.get(npcType).add(texture);
	}

	private ResourceLocation loadSkinImage(String imageName, InputStream is) {
		return AncientWarfareNPC.proxy.loadSkinPackImage(imageName, is).orElse(TextureMap.LOCATION_MISSING_TEXTURE);
	}
}
