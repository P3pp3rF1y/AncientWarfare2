package net.shadowmage.ancientwarfare.npc.skin;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class NpcSkinManager {

    public static final NpcSkinManager INSTANCE = new NpcSkinManager();

    private final HashMap<String, SkinGroup> skinGroups = new HashMap<String, SkinGroup>();

    private final Random rng = new Random();

    private final String skinMainPath = AWCoreStatics.configPathForFiles + "npc/skins/";
    private static final String defaultSkinPack = "/assets/ancientwarfare/skin_pack/default_skin_pack.zip";

    public ResourceLocation getTextureFor(NpcBase npc) {
        ResourceLocation loc = null;
        long id = npc.getIDForSkin();
        if (!npc.getCustomTex().isEmpty()) {
            loc = getNpcTexture(npc.getCustomTex(), id);
        }
        if (loc == null) {
            loc = getNpcTexture(npc.getNpcFullType(), id);
        }
        return loc;
    }

    private ResourceLocation getNpcTexture(String type, long idlsb) {
        SkinGroup group = skinGroups.get(type);
        if (group != null && !group.textures.isEmpty()) {
            rng.setSeed(idlsb);
            int tex = rng.nextInt(group.textures.size());
            return group.getTexture(tex);
        }
        return null;
    }

    public void loadSkinPacks() {
        if (AWNPCStatics.loadDefaultSkinPack) {
            loadDefaultSkinPack();
        }
        String path = skinMainPath;
        File file = new File(path);
        file.mkdirs();
        List<File> probableZipFiles = new ArrayList<File>();
        recursiveScan(new File(path), probableZipFiles);
        parseZipFiles(probableZipFiles);
    }

    private void loadDefaultSkinPack() {
        InputStream is = getClass().getResourceAsStream(defaultSkinPack);
        ZipInputStream zis = new ZipInputStream(is);
        SkinPack pack = loadPackFromZip("default_skin_pack.zip", zis);
        if (pack != null) {
            unpackSkinPack(pack);
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load a skin pack from a zip-input stream.<br>
     * Closes the zip input stream when done.
     */
    private SkinPack loadPackFromZip(String fileName, ZipInputStream zis) {
        HashMap<String, ResourceLocation> parsedImages = new HashMap<String, ResourceLocation>();
        ResourceLocation loc;
        SkinMeta metaFile = null;
        SkinPack pack = null;
        ZipEntry entry;
        try {
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                } else if (entry.getName().toLowerCase().equals("skin_pack.meta")) {
                    metaFile = new SkinMeta(zis);
                } else if (entry.getName().toLowerCase().endsWith(".png")) {
                    loc = loadSkinPackImage(fileName, entry.getName(), zis);
                    if (loc != null) {
                        parsedImages.put(entry.getName(), loc);
                    }
                }
            }
            if (metaFile != null) {
                pack = new SkinPack(metaFile, parsedImages);
                AWLog.log("Loaded Skin pack: " + fileName + " containing: " + parsedImages.size() + " skins.");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pack;
    }

    private void recursiveScan(File directory, List<File> zipFileList) {
        if (directory == null) {
            AWLog.logError("Could not locate directory to load structures!");
            return;
        }
        File[] allFiles = directory.listFiles();
        if (allFiles == null) {
            AWLog.logError("Could not locate " + directory + " directory to load skin packs!--no files in directory file list!");
            return;
        }
        for (File currentFile : allFiles) {
            if (currentFile.isDirectory()) {
                recursiveScan(currentFile, zipFileList);
            } else if (isProbableZip(currentFile)) {
                zipFileList.add(currentFile);
            }
        }
    }

    private void parseZipFiles(List<File> probableZipFiles) {
        List<SkinPack> skinPacks = new ArrayList<SkinPack>();
        SkinPack pack;
        ZipInputStream zis = null;
        FileInputStream fis = null;

        for (File f : probableZipFiles) {
            try {
                fis = new FileInputStream(f);
                zis = new ZipInputStream(fis);
                pack = loadPackFromZip(f.getName(), zis);
                if (pack != null) {
                    skinPacks.add(pack);
                }
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        }
        unpackSkinPacks(skinPacks);
    }

    private void unpackSkinPacks(List<SkinPack> packs) {
        for (SkinPack pack : packs) {
            unpackSkinPack(pack);
        }
    }

    private void unpackSkinPack(SkinPack pack) {
        SkinMeta meta = pack.meta;
        for (String key : meta.imageMap.keySet()) {
            for (String img : meta.imageMap.get(key)) {
                if (pack.textures.containsKey(img)) {
                    loadSkinImage(key, pack.textures.get(img));
                }
            }
        }
    }

    private void loadSkinImage(String npcType, ResourceLocation texture) {
        getOrCreateSkinGroup(npcType).addTexture(texture);
    }

    private SkinGroup getOrCreateSkinGroup(String npcType) {
        if (!skinGroups.containsKey(npcType)) {
            SkinGroup group = new SkinGroup();
            skinGroups.put(npcType, group);
            return group;
        } else {
            return skinGroups.get(npcType);
        }
    }

    private ResourceLocation loadSkinPackImage(String packName, String imageName, InputStream is) {
        return AncientWarfareNPC.proxy.loadSkinPackImage(packName, imageName, is);
    }

    private boolean isProbableZip(File file) {
        return file.getName().toLowerCase(Locale.ENGLISH).endsWith(".zip");
    }

    private class SkinGroup {
        List<ResourceLocation> textures = new ArrayList<ResourceLocation>();

        public void addTexture(ResourceLocation loc) {
            this.textures.add(loc);
        }

        public ResourceLocation getTexture(int index) {
            return textures.get(index);
        }
    }

    private class SkinPack {
        SkinMeta meta;
        HashMap<String, ResourceLocation> textures = new HashMap<String, ResourceLocation>();

        public SkinPack(SkinMeta meta, Map<String, ResourceLocation> textureMap) {
            this.meta = meta;
            this.textures.putAll(textureMap);
        }
    }

    private class SkinMeta {
        //map of NpcType = imageName
        HashMap<String, Set<String>> imageMap = new HashMap<String, Set<String>>();

        public SkinMeta(InputStream is) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            String[] lineBits;
            while (br.ready()) {
                line = br.readLine();
                lineBits = line.split("=");
                if (lineBits.length > 1) {
                    if (!imageMap.containsKey(lineBits[0])) {
                        imageMap.put(lineBits[0], new HashSet<String>());
                    }
                    imageMap.get(lineBits[0]).add(lineBits[1]);
                }
            }
        }

        @Override
        public String toString() {
            return "SkinPackMeta -- imageMap: [" + imageMap + "]";
        }

    }

}
