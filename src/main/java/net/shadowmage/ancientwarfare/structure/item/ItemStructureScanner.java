package net.shadowmage.ancientwarfare.structure.item;

import java.io.File;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidationType;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;
import net.shadowmage.ancientwarfare.structure.template.load.TemplateLoader;
import net.shadowmage.ancientwarfare.structure.template.save.TemplateExporter;
import net.shadowmage.ancientwarfare.structure.template.scan.TemplateScanner;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;

public class ItemStructureScanner extends Item implements IItemKeyInterface, IItemClickable
{

public ItemStructureScanner(String localizationKey)
  {
  this.setUnlocalizedName(localizationKey); 
  this.setCreativeTab(AWStructuresItemLoader.structureTab);
  this.setMaxStackSize(1);
  this.setTextureName("ancientwarfare:structure/"+localizationKey);
  }

ItemStructureSettings viewSettings = new ItemStructureSettings();
@SuppressWarnings({ "unchecked", "rawtypes" })
@Override
public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List list, boolean par4)
  { 
  if(par1ItemStack!=null)
    {
    ItemStructureSettings.getSettingsFor(par1ItemStack, viewSettings);
    /**
     * TODO add info to tooltip from nbt-tag
     */
    NBTTagCompound tag;
    if(par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("structData"))
      {
      tag = par1ItemStack.getTagCompound().getCompoundTag("structData");
      }
    else
      {
      tag = new NBTTagCompound();
      }
    if(viewSettings.hasPos1() && viewSettings.hasPos2() && viewSettings.hasBuildKey())
      {
      list.add("Right Click: Scan and Process (4/4)");
      list.add("(Shift)Right Click: Cancel/clear");
      }        
    else if(!viewSettings.hasPos1())
      {
      list.add("Left Click: Set first bound (1/4)");
      list.add("Hold shift to offset for side hit");
      list.add("(Shift)Right Click: Cancel/clear");
      }
    else if(!viewSettings.hasPos2())
      {
      list.add("Left Click: Set second bound (2/4)");
      list.add("Hold shift to offset for side hit");
      list.add("(Shift)Right Click: Cancel/clear");
      }
    else if(!viewSettings.hasBuildKey())
      {
      list.add("Left Click: Set build key and");
      list.add("    direction (3/4)");
      list.add("Hold shift to offset for side hit");
      list.add("(Shift)Right Click: Cancel/clear");
      }    
    }  
  }

ItemStructureSettings scanSettings = new ItemStructureSettings();
@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  ItemStructureSettings.getSettingsFor(stack, scanSettings);
  if(player.isSneaking())
    {
    AWLog.logDebug("clearing item");
    scanSettings.clearSettings();
    ItemStructureSettings.setSettingsFor(stack, scanSettings);
    }
  else if(scanSettings.hasPos1() && scanSettings.hasPos2() && scanSettings.hasBuildKey())
    {
    BlockPosition key = scanSettings.key;
    if(player.getDistance(key.x+0.5d, key.y, key.z+0.5d) > 10)
      {
      AWLog.logDebug("too far from scan build key");
//      player.addChatMessage("You are too far away to scan that building, move closer to chosen build-key position");
      return;
      }
//    player.addChatMessage("Initiating Scan (4/4)");
    AWLog.logDebug("should open scan GUI==true");
    FMLNetworkHandler.openGui(player, AncientWarfareCore.instance, NetworkHandler.GUI_SCANNER, player.worldObj, 0, 0, 0);
//    GUIHandler.instance().openGUI(Statics.guiStructureScannerCreative, player, 0,0,0);   
    } 
  }

public static boolean scanStructure(World world, BlockPosition pos1, BlockPosition pos2, BlockPosition key, int face, String name, boolean include, NBTTagCompound tag)
  {
  BlockPosition min = BlockTools.getMin(pos1, pos2);
  BlockPosition max = BlockTools.getMax(pos1, pos2);
  TemplateScanner scanner = new TemplateScanner();
  int turns = face==0 ? 2 : face==1 ? 1 : face==2 ? 0 : face==3 ? 3 : 0; //because for some reason my mod math was off?  
  StructureTemplate template = scanner.scan(world, min, max, key, turns, name);

  String validationType = tag.getString("validationType");
  StructureValidationType type = StructureValidationType.getTypeFromName(validationType);
  StructureValidator validator = type.getValidator();
  validator.readFromNBT(tag);  
  template.setValidationSettings(validator);
  if(include)
    {
    StructureTemplateManager.instance().addTemplate(template);    
    }
  TemplateExporter.exportTo(template, new File(include ? TemplateLoader.includeDirectory : TemplateLoader.outputDirectory));
  return true;
  }

@Override
public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, int keyIndex)
  {
  return true;
  }

@Override
public void onKeyAction(EntityPlayer player, ItemStack stack, int keyIndex)
  {  
  if(!MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(player.getCommandSenderName()))
    {
    return;
    }
  BlockPosition hit = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
  ItemStructureSettings.getSettingsFor(stack, scanSettings);
  if(scanSettings.hasPos1() && scanSettings.hasPos2() && scanSettings.hasBuildKey())
    {
    AWLog.logDebug("right click to process");
//    player.addChatMessage("Right Click to Process");
    }
  else if(!scanSettings.hasPos1())
    {
    AWLog.logDebug("setting pos1");
    scanSettings.setPos1(hit.x, hit.y, hit.z);
//    player.addChatMessage("Setting Scan Position 1 (Step 1/4)");
    }
  else if(!scanSettings.hasPos2())
    {
    AWLog.logDebug("setting pos2");
    scanSettings.setPos2(hit.x, hit.y, hit.z);
//    player.addChatMessage("Setting Scan Position 2 (Step 2/4)");
    }
  else if(!scanSettings.hasBuildKey())
    {
    AWLog.logDebug("setting build key");
    scanSettings.setBuildKey(hit.x, hit.y, hit.z, BlockTools.getPlayerFacingFromYaw(player.rotationYaw));
//    player.addChatMessage("Setting Scan Build Position and Facing (Step 3/4)");
    }
  ItemStructureSettings.setSettingsFor(stack, scanSettings);
  }

@Override
public boolean onRightClickClient(EntityPlayer player, ItemStack stack)
  {
  return true;
  }

@Override
public boolean onLeftClickClient(EntityPlayer player, ItemStack stack)
  {
  return false;
  }

@Override
public void onLeftClick(EntityPlayer player, ItemStack stack)
  {
  
  }


}
