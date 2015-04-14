package net.shadowmage.ancientwarfare.structure.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.event.IBoxRenderer;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateClient;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManagerClient;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilderTicked;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureBuilder;

import java.util.ArrayList;
import java.util.List;

public class ItemBlockStructureBuilder extends ItemBlock implements IBoxRenderer {

    List<ItemStack> displayCache = null;

    public ItemBlockStructureBuilder(Block p_i45328_1_) {
        super(p_i45328_1_);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        String name = "corrupt_item";
        if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("structureName")) {
            name = par1ItemStack.getTagCompound().getString("structureName");
        }
        par3List.add(StatCollector.translateToLocal("guistrings.structure.structure_name") + ": " + name);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List p_150895_3_) {
        if (displayCache == null) {
            displayCache = new ArrayList<ItemStack>();
            List<StructureTemplateClient> templates = StructureTemplateManagerClient.instance().getSurvivalStructures();
            ItemStack item;
            for (StructureTemplateClient t : templates) {
                item = new ItemStack(this);
                item.setTagInfo("structureName", new NBTTagString(t.name));
                displayCache.add(item);
            }
        }
        p_150895_3_.addAll(displayCache);
//  super.getSubItems(p_150895_1_, p_150895_2_, p_150895_3_);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("structureName")) {
//    stack.setTagInfo("structureName", new NBTTagString("no_selection")); 
            return false;
        }
        boolean val = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
        if (!world.isRemote && val) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileStructureBuilder) {
                TileStructureBuilder tb = (TileStructureBuilder) te;
                tb.setOwnerName(player.getCommandSenderName());
                String name = stack.getTagCompound().getString("structureName");
                StructureTemplate t = StructureTemplateManager.INSTANCE.getTemplate(name);
                if (t != null) {
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

    @Override
    public void renderBox(EntityPlayer player, ItemStack stack, float delta) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("structureName")) {
            return;
        }
        String name = stack.getTagCompound().getString("structureName");
        StructureTemplateClient t = StructureTemplateManagerClient.instance().getClientTemplate(name);
        if (t == null) {
            return;
        }
        BlockPosition hit = BlockTools.getBlockClickedOn(player, player.worldObj, true);
        if (hit == null) {
            return;
        }
        int face = BlockTools.getPlayerFacingFromYaw(player.rotationYaw);
        BlockPosition p1 = new BlockPosition(hit.x, hit.y, hit.z);
        BlockPosition p2 = p1.copy();
        Util.renderBoundingBox(player, p1, p2, delta);
        p2.moveForward(face, t.zSize - 1 - t.zOffset + 1);
        StructureBB bb = new StructureBB(p2.x, p2.y, p2.z, face, t.xSize, t.ySize, t.zSize, t.xOffset, t.yOffset, t.zOffset);
        p2.reassign(bb.max.x, bb.max.y, bb.max.z);
        Util.renderBoundingBox(player, bb.min, p2, delta);
    }
}
