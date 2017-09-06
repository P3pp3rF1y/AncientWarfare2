package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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


    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        String name = "corrupt_item";
        if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("structureName")) {
            name = par1ItemStack.getTagCompound().getString("structureName");
        }
        par3List.add(I18n.format("guistrings.structure.structure_name") + ": " + name);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List p_150895_3_) {
        if (displayCache == null) {
            displayCache = new ArrayList<>();
            List<StructureTemplateClient> templates = StructureTemplateManagerClient.instance().getSurvivalStructures();
            @Nonnull ItemStack item;
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
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileStructureBuilder) {
                TileStructureBuilder tb = (TileStructureBuilder) te;
                tb.setOwner(player);
                String name = stack.getTagCompound().getString("structureName");
                StructureTemplate t = StructureTemplateManager.INSTANCE.getTemplate(name);
                if (t != null) {
                    int face = BlockTools.getPlayerFacingFromYaw(player.rotationYaw);
                    BlockPos p = new BlockPos(x, y, z).moveForward(face, t.zSize - 1 - t.zOffset + 1);
                    tb.setBuilder(new StructureBuilderTicked(world, t, face, p.x, p.y, p.z));
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
        BlockPos hit = BlockTools.getBlockClickedOn(player, player.world, true);
        if (hit == null) {
            return;
        }
        Util.renderBoundingBox(player, hit, hit, delta);
        int face = BlockTools.getPlayerFacingFromYaw(player.rotationYaw);
        BlockPos p2 = hit.moveForward(face, t.zSize - 1 - t.zOffset + 1);
        StructureBB bb = new StructureBB(p2.x, p2.y, p2.z, face, t.xSize, t.ySize, t.zSize, t.xOffset, t.yOffset, t.zOffset);
        Util.renderBoundingBox(player, bb.min, bb.max, delta);
    }
}
