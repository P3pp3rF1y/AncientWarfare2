package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.util.HashMap;

/*
 * TODO remove once rendering is moved to JSONs / baked models?
 *
 * @author Shadowmage
 */
public class AWTextureManager {

    public static AWTextureManager instance() {
        return instance;
    }

    private static AWTextureManager instance = new AWTextureManager();

    private AWTextureManager() {
    }

    private HashMap<String, ResourceLocation> locationTextures = new HashMap<>();
    private HashMap<String, TextureImageBased> imageBasedTextures = new HashMap<>();

    public void bindLocationTexture(String name) {
        if (!locationTextures.containsKey(name)) {
            locationTextures.put(name, new ResourceLocation(name));
        }
        Minecraft.getMinecraft().renderEngine.bindTexture(locationTextures.get(name));
    }

    public ResourceLocation loadImageBasedTexture(String refName, BufferedImage image) {
        ResourceLocation loc = new ResourceLocation(refName);
        TextureImageBased tex = new TextureImageBased(loc, image);
        Minecraft.getMinecraft().renderEngine.loadTexture(loc, tex);
        imageBasedTextures.put(refName, tex);
        locationTextures.put(refName, loc);
        return loc;
    }

    public void updateImageBasedTexture(String refName, BufferedImage image) {
        TextureImageBased tex = imageBasedTextures.get(refName);
        if (tex != null) {
            tex.image = image;
            tex.reUploadImage();
        }
    }

}
