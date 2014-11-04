package net.shadowmage.ancientwarfare.structure.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.world_gen.StructureMap;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldStructureGenerator;

public class ItemStructureBuilderWorldGen extends Item implements IItemKeyInterface, IItemClickable
{

ItemStructureSettings buildSettings = new ItemStructureSettings();

ItemStructureSettings viewSettings = new ItemStructureSettings();

/**
 * @param itemID
 */
public ItemStructureBuilderWorldGen(String itemName)
  {
  this.setUnlocalizedName(itemName);
  this.setCreativeTab(AWStructuresItemLoader.structureTab);
  this.setMaxStackSize(1);  
  this.setTextureName("ancientwarfare:structure/"+itemName);
  }

@Override
public boolean cancelRightClick(EntityPlayer player, ItemStack stack)
  {
  return true;
  }

@Override
public boolean cancelLeftClick(EntityPlayer player, ItemStack stack)
  {
  return false;
  }

@SuppressWarnings({ "unchecked", "rawtypes" })
@Override
public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
  {
  String structure = "guistrings.no_selection";
  ItemStructureSettings.getSettingsFor(stack, viewSettings);
  if(viewSettings.hasName())
    {
    structure = viewSettings.name;
    }  
  list.add(StatCollector.translateToLocal("guistrings.current_structure")+" "+StatCollector.translateToLocal(structure));
  }

@Override
public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player)
  {
  return false;
  }

@Override
public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key)
  {
  return key==ItemKey.KEY_0;
  }

@Override
public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key)
  {
  if(player==null || player.worldObj.isRemote)
    {
    return;
    }
  ItemStructureSettings.getSettingsFor(stack, buildSettings);
  if(buildSettings.hasName())
    {
    StructureTemplate template = StructureTemplateManager.instance().getTemplate(buildSettings.name);
    if(template==null)
      {
      /**
       * TODO add chat message
       */
      return;
      }
    BlockPosition bpHit = BlockTools.getBlockClickedOn(player, player.worldObj, true);   
    StructureMap map = AWGameData.INSTANCE.getData("AWStructureMap", player.worldObj, StructureMap.class);
    WorldStructureGenerator.instance().attemptStructureGenerationAt(player.worldObj, bpHit.x, bpHit.y, bpHit.z, BlockTools.getPlayerFacingFromYaw(player.rotationYaw), template, map);
    
//    StructureBuilder builder = new StructureBuilder(player.worldObj, template, BlockTools.getPlayerFacingFromYaw(player.rotationYaw), bpHit.x, bpHit.y, bpHit.z);
//    builder.instantConstruction();
    }  
  else
    {
    /**
     * TODO add chat message
     */
    }  
  }

@Override
public boolean onRightClickClient(EntityPlayer player, ItemStack stack)
  {
  return true;
  }

@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  if(!player.worldObj.isRemote && !player.isSneaking())
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_BUILDER, 0, 0, 0);   
    }        
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
