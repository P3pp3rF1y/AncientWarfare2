package net.shadowmage.ancientwarfare.npc.skin;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcSkinManager
{

public static final NpcSkinManager INSTANCE = new NpcSkinManager();

/**
 * server-side skins
 */
private static final String skinMainPath = "config/AWConfig/npc/skins/";

/**
 * client-side skins
 */
private static final String skinCachePath = "config/AWConfig/npc/client_skin_cache/";

public NpcSkinManager()
  {
  
  }

public void loadSkinPacks()
  {
  // scan through skins directory, loading any .zip files that are skin-packs 
  // (they will contain a meta-data file along with the skin images)
  }

public ResourceLocation getTextureFor(NpcBase npc)
  {
  return null;
  }

}
