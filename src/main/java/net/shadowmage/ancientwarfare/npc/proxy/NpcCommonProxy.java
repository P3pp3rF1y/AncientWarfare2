package net.shadowmage.ancientwarfare.npc.proxy;

import com.google.common.base.Supplier;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase;

import java.io.InputStream;
import java.util.HashSet;

public class NpcCommonProxy extends CommonProxyBase {
	private HashSet<GameProfile> profileCache = new HashSet<>();

	public void loadSkins() {
	}

	public ResourceLocation loadSkinPackImage(String packName, String imageName, InputStream is) {
		return null;
	}

	public GameProfile getProfile(final String name) {
		return profileCache.stream().filter(input -> input.getName().equals(name)).findFirst().orElseGet((Supplier<GameProfile>) () -> null);
	}

	public void cacheProfile(GameProfile gameprofile) {
		profileCache.add(gameprofile);
	}

	public NBTTagCompound cacheProfile(WorldServer worldServer, final String name) {
		GameProfile gameprofile = getProfile(name);
		if (gameprofile == null) {
			gameprofile = worldServer.getMinecraftServer().getPlayerProfileCache().getGameProfileForUsername(name);
			if (gameprofile != null) {
				Property property = gameprofile.getProperties().get("textures").stream().findFirst().orElse(null);
				if (property == null) {
					gameprofile = worldServer.getMinecraftServer().getMinecraftSessionService().fillProfileProperties(gameprofile, true);
				}
				cacheProfile(gameprofile);
			}
		}
		if (gameprofile != null) {
			NBTTagCompound tagCompound = new NBTTagCompound();
			try {
				NBTUtil.writeGameProfile(tagCompound, gameprofile);
			}
			catch (Throwable handled) {
				return null;
			}
			return tagCompound;
		}
		return null;
	}

	public ResourceLocation getPlayerSkin(String name) {
		return null;
	}
}
