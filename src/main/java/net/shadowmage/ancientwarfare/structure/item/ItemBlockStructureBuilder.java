package net.shadowmage.ancientwarfare.structure.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateClient;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManagerClient;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilderTicked;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureBuilder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockStructureBuilder extends ItemBlock
{

List<ItemStack> displayCache = null;

public ItemBlockStructureBuilder(Block p_i45328_1_)
  {
  super(p_i45328_1_);
  }

@Override
@SideOnly(Side.CLIENT)
public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
  {
  String name = "corrupt_item";
  if(par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("structureName"))
    {
    name = par1ItemStack.getTagCompound().getString("structureName");
    }
  par3List.add("DEBUG- struct name: "+name);  
  }

@Override
@SideOnly(Side.CLIENT)
public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List p_150895_3_)
  {
  if(displayCache==null)
    {
    displayCache = new ArrayList<ItemStack>();
    List<StructureTemplateClient> templates = StructureTemplateManagerClient.instance().getSurvivalStructures();
    ItemStack item;    
    for(StructureTemplateClient t : templates)
      {
      item = new ItemStack(this);
      item.setTagInfo("structureName", new NBTTagString(t.name));
      displayCache.add(item);
      }
    }
  p_150895_3_.addAll(displayCache);
//  super.getSubItems(p_150895_1_, p_150895_2_, p_150895_3_);
  }

@Override
public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
  {
  if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("structureName"))    
    {
    AWLog.logDebug("no tag exists for structure item... item is corrupt");
//    stack.setTagInfo("structureName", new NBTTagString("no_selection")); 
    return false;
    }
  boolean val = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
  if(!world.isRemote && val)
    {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileStructureBuilder)
      {
      TileStructureBuilder tb = (TileStructureBuilder)te;
      tb.setOwnerName(player.getCommandSenderName());      
      String name = stack.getTagCompound().getString("structureName");
      StructureTemplate t = StructureTemplateManager.instance().getTemplate(name);
      if(t!=null)
        {
        AWLog.logDebug("setting tile builder template to: "+t);
        int face = BlockTools.getPlayerFacingFromYaw(player.rotationYaw);
        BlockPosition p = new BlockPosition(x, y, z);
        p.moveForward(face, t.zSize - 1 - t.zOffset + 1);
        StructureBuilderTicked ticked = new StructureBuilderTicked(world, t, BlockTools.getPlayerFacingFromYaw(player.rotationYaw), p.x, p.y, p.z);        
        tb.setBuilder(ticked);
        }
      }    
    }
  return val;  
  }

}
