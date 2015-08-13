package net.shadowmage.ancientwarfare.npc.proxy;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase;

import java.io.InputStream;
import java.util.HashSet;

public class NpcCommonProxy extends CommonProxyBase {
    private HashSet<GameProfile> profileCache = new HashSet<GameProfile>();

    public void loadSkins() {
    }

    public ResourceLocation loadSkinPackImage(String packName, String imageName, InputStream is) {
        return null;
    }

    public GameProfile getProfile(final String name) {
        return Iterables.getFirst(Iterables.filter(profileCache, new Predicate<GameProfile>() {
            @Override
            public boolean apply(GameProfile input) {
                return input.getName().equals(name);
            }
        }), null);
    }

    public void cacheProfile(GameProfile gameprofile) {
        profileCache.add(gameprofile);
    }

    public NBTTagCompound cacheProfile(final String name) {
        GameProfile gameprofile = getProfile(name);
        if (gameprofile == null) {
            gameprofile = MinecraftServer.getServer().func_152358_ax().func_152655_a(name);
            if (gameprofile != null) {
                Property property = Iterables.getFirst(gameprofile.getProperties().get("textures"), null);
                if (property == null) {
                    gameprofile = MinecraftServer.getServer().func_147130_as().fillProfileProperties(gameprofile, true);
                }
                cacheProfile(gameprofile);
            }
        }
        if(gameprofile!=null) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            try {
                NBTUtil.func_152460_a(tagCompound, gameprofile);
            }catch (Throwable handled){
                return null;
            }
            return tagCompound;
        }
        return null;
    }

    public ResourceLocation getPlayerSkin(String name){
        return null;
    }
}
