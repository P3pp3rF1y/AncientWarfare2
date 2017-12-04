package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.item.ItemBlockBase;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.event.IBoxRenderer;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateClient;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManagerClient;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilderTicked;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureBuilder;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockStructureBuilder extends ItemBlockBase implements IBoxRenderer {

    public ItemBlockStructureBuilder(Block block) {
        super(block);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
        String name = "corrupt_item";
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("structureName")) {
            name = stack.getTagCompound().getString("structureName");
        }
        tooltip.add(I18n.format("guistrings.structure.structure_name") + ": " + name);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("structureName")) {
            return false;
        }
        boolean val = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (!world.isRemote && val) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileStructureBuilder) {
                TileStructureBuilder tb = (TileStructureBuilder) te;
                tb.setOwner(player);
                String name = stack.getTagCompound().getString("structureName");
                StructureTemplate t = StructureTemplateManager.INSTANCE.getTemplate(name);
                if (t != null) {
                    EnumFacing face = player.getHorizontalFacing();
                    BlockPos p = pos.offset(face, t.zSize - 1 - t.zOffset + 1);
                    tb.setBuilder(new StructureBuilderTicked(world, t, face, p));
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
        EnumFacing face = player.getHorizontalFacing();
        BlockPos p2 = hit.offset(face, t.zSize - 1 - t.zOffset + 1);
        StructureBB bb = new StructureBB(p2, face, t.xSize, t.ySize, t.zSize, t.xOffset, t.yOffset, t.zOffset);
        Util.renderBoundingBox(player, bb.min, bb.max, delta);
    }
}
