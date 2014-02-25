package shadowmage.meim.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import shadowmage.meim.common.config.MEIMConfig;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ItemLoader
{

public static CreativeTabs modelerTab = new CreativeTabs("Ancient Modeler");
public static final Item guiOpener = new ItemGuiOpener(MEIMConfig.getItemID("guiOpener", 9001));

/**
 * called from core during init phase to load item names/etc
 */
public static void load()
  {  
  LanguageRegistry.addName(guiOpener, "MEIM Gui");
  }
}
