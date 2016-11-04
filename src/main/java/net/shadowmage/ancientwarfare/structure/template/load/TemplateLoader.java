/**
 Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
 This software is distributed under the terms of the GNU General Public License.
 Please see COPYING for precise license information.

 This file is part of Ancient Warfare.

 Ancient Warfare is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Ancient Warfare is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.shadowmage.ancientwarfare.structure.template.load;

import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate;
import net.shadowmage.ancientwarfare.structure.town.TownTemplateManager;
import net.shadowmage.ancientwarfare.structure.town.TownTemplateParser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TemplateLoader {

    public static final String defaultTemplateDirectory = "/assets/ancientwarfare/templates/";
    public static String outputDirectory = null;
    public static String includeDirectory = null;
    private final String defaultTemplatePackLocation = "/assets/ancientwarfare/template/default_structure_pack.zip";

    private List<File> probableTownFiles = new ArrayList<File>();
    private List<File> probableStructureFiles = new ArrayList<File>();
    private List<File> probableZipFiles = new ArrayList<File>();

    private List<TownTemplate> parsedTownTemplates = new ArrayList<TownTemplate>();

    private Set<String> loadedStructureNames = new HashSet<String>();

    private HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();

    public static final TemplateLoader INSTANCE = new TemplateLoader();

    private TemplateLoader() {
    }

    public void initializeAndExportDefaults(String path) {
        outputDirectory = AWCoreStatics.configPathForFiles + "structures/export/";
        includeDirectory = AWCoreStatics.configPathForFiles + "structures/included/";

        /**
         * create default dirs if they don't exist...
         */
        File existTest = new File(outputDirectory);
        if (!existTest.exists()) {
            AWLog.log("Creating default Export Directory");
            existTest.mkdirs();
        }

        existTest = new File(includeDirectory);
        if (!existTest.exists()) {
            AWLog.log("Creating default Include Directory");
            existTest.mkdirs();
        }
    }

    public void loadTemplates() {
        int loadedCount = this.loadDefaultPack();//load default structure pack

        this.locateStructureFiles();
        StructureTemplate template;
        for (File f : this.probableStructureFiles) {
            template = loadTemplateFromFile(f);
            if (template != null) {
                AWLog.log("Loaded Structure Template: [" + template.name + "] WorldGen: " + template.getValidationSettings().isWorldGenEnabled() + "  Survival: " + template.getValidationSettings().isSurvival());
                StructureTemplateManager.INSTANCE.addTemplate(template);
                loadedStructureNames.add(template.name);
                loadedCount++;
            }
        }
        loadedCount += this.loadTemplatesFromZips();
        AWLog.log("Loaded " + loadedCount + " structure(s)");

        this.validateAndLoadImages();
        this.probableStructureFiles.clear();
        this.probableZipFiles.clear();
        this.loadedStructureNames.clear();
        this.images.clear();
        loadTownTemplates();
    }

    private void loadTownTemplates() {
        TownTemplate townTemplate;
        for (File f : this.probableTownFiles) {
            townTemplate = loadTownTemplateFromFile(f);
            if (townTemplate != null) {
                parsedTownTemplates.add(townTemplate);
            }
        }

        this.probableTownFiles.clear();

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

    private void validateAndLoadImages() {
        String name;
        Iterator<String> it = images.keySet().iterator();
        while (it.hasNext() && (name = it.next()) != null) {
            if (!loadedStructureNames.contains(name.substring(0, name.length() - 4))) {
                it.remove();
                continue;
            }
            StructureTemplateManager.INSTANCE.addTemplateImage(name, images.get(name));
        }
    }

    private StructureTemplate loadTemplateFromFile(File file) {
        FileReader reader = null;
        Scanner scan = null;
        List<String> templateLines = new ArrayList<String>();
        try {
            reader = new FileReader(file);
            scan = new Scanner(reader);
            while (scan.hasNext()) {
                templateLines.add(scan.nextLine());
            }
            return TemplateParser.INSTANCE.parseTemplate(file.getName(), templateLines);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (scan != null) {
                scan.close();
            }
        }
    }

    private TownTemplate loadTownTemplateFromFile(File file) {
        FileReader reader = null;
        Scanner scan = null;
        List<String> templateLines = new ArrayList<String>();
        String line;
        try {
            reader = new FileReader(file);
            scan = new Scanner(reader);
            while (scan.hasNext()) {
                line = scan.nextLine();
                if (line.startsWith("#")) {
                    continue;
                }
                templateLines.add(line);
            }
            return TownTemplateParser.parseTemplate(templateLines);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (scan != null) {
                scan.close();
            }
        }
    }

    private void loadStructureImage(String imageName, InputStream is) {
        while (imageName.contains("/")) {
            imageName = imageName.substring(imageName.indexOf("/") + 1, imageName.length());
        }
        try {
            BufferedImage image = ImageIO.read(is);
            if (image != null && image.getWidth() == AWStructureStatics.structureImageWidth && image.getHeight() == AWStructureStatics.structureImageHeight) {
                images.put(imageName, image);
                AWLog.logDebug("loaded structure image of: " + imageName);
            } else {
                AWLog.logError("Attempted to load improper sized template image: " + imageName + " with dimensions of: " + image.getWidth() + "x" + image.getHeight() + ".  Specified width/height is: " + AWStructureStatics.structureImageWidth + "x" + AWStructureStatics.structureImageHeight);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int loadTemplatesFromZipStream(ZipInputStream zis) {
        int parsed = 0;
        StructureTemplate template;
        ZipEntry entry = null;
        try {
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }//TODO how to handle subfolders in a zip-file?
                AWLog.logDebug("parsing entry: " + entry.getName());
                if (entry.getName().toLowerCase(Locale.ENGLISH).endsWith(".png")) {
                    loadStructureImage(entry.getName(), zis);
                } else if (entry.getName().toLowerCase(Locale.ENGLISH).endsWith("." + AWStructureStatics.townTemplateExtension)) {
                    loadTownTemplateFromZip(entry, zis);
                } else if (entry.getName().toLowerCase(Locale.ENGLISH).endsWith("." + AWStructureStatics.templateExtension)) {
                    template = loadTemplateFromZip(entry, zis);
                    if (template != null) {
                        AWLog.log("Loaded Structure Template: [" + template.name + "] WorldGen: " + template.getValidationSettings().isWorldGenEnabled() + "  Survival: " + template.getValidationSettings().isSurvival());
                        StructureTemplateManager.INSTANCE.addTemplate(template);
                        loadedStructureNames.add(template.name);
                        parsed++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parsed;
    }

    private int loadDefaultPack() {
        if (!AWStructureStatics.loadDefaultPack) {
            return 0;
        }
        InputStream is = getClass().getResourceAsStream(defaultTemplatePackLocation);
        ZipInputStream zis = new ZipInputStream(is);
        int loaded = loadTemplatesFromZipStream(zis);
        try {
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loaded;
    }

    private int loadTemplatesFromZips() {
        ZipInputStream zis = null;
        FileInputStream fis = null;

        int parsed = 0;
        int totalParsed = 0;
        for (File f : this.probableZipFiles) {
            parsed = 0;
            AWLog.log("Loading templates from zip file: " + f.getName());
            try {
                fis = new FileInputStream(f);
                zis = new ZipInputStream(fis);
                parsed = loadTemplatesFromZipStream(zis);
                try {
                    zis.close();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            AWLog.log("Loaded a total of " + parsed + " template(s) from zip file: " + f.getName());
            totalParsed += parsed;
        }
        return totalParsed;
    }

    private TownTemplate loadTownTemplateFromZip(ZipEntry entry, InputStream is) {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isr);
        List<String> lines = new ArrayList<String>();
        String line;
        TownTemplate template = null;
        try {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            template = TownTemplateParser.parseTemplate(lines);
            if (template != null) {
                parsedTownTemplates.add(template);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            template = null;
        }
        return template;
    }

    private StructureTemplate loadTemplateFromZip(ZipEntry entry, InputStream is) {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isr);
        List<String> lines = new ArrayList<String>();
        String line;
        StructureTemplate template = null;
        try {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            template = TemplateParser.INSTANCE.parseTemplate(entry.getName(), lines);
        } catch (IOException e1) {
            e1.printStackTrace();
            template = null;
        }
        return template;
    }

    private void locateStructureFiles() {
        this.recursiveScan(new File(includeDirectory), probableStructureFiles, probableZipFiles, AWStructureStatics.templateExtension);
        this.recursiveScan(new File(includeDirectory), probableTownFiles, probableZipFiles, AWStructureStatics.townTemplateExtension);
    }

    private void recursiveScan(File directory, List<File> fileList, List<File> zipFileList, String extension) {
        if (directory == null) {
            AWLog.logError("Could not locate " + directory + " directory to load structures!");
            return;
        }
        File[] allFiles = directory.listFiles();
        if (allFiles == null) {
            AWLog.logError("Could not locate " + directory + " directory to load structures!--no files in directory file list!");
            return;
        }
        File currentFile;
        for (int i = 0; i < allFiles.length; i++) {
            currentFile = allFiles[i];
            if (currentFile.isDirectory()) {
                recursiveScan(currentFile, fileList, zipFileList, extension);
            } else if (isProbableFile(currentFile, extension) && !fileList.contains(currentFile)) {
                fileList.add(currentFile);
            } else if (isProbableZip(currentFile) && !zipFileList.contains(currentFile)) {
                zipFileList.add(currentFile);
            } else if (isProbableImage(currentFile)) {
                try {
                    FileInputStream fis = new FileInputStream(currentFile);
                    loadStructureImage(currentFile.getName(), fis);
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isProbableFile(File file, String extension) {
        return file.getName().toLowerCase(Locale.ENGLISH).endsWith(extension);
    }

    private boolean isProbableZip(File file) {
        return isProbableFile(file, ".zip");
    }

    private boolean isProbableImage(File file) {
        return isProbableFile(file, ".png");
    }

}
