package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStockViewer;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStockViewer.WarehouseStockFilter;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.List;

public class RenderTileWarehouseStockViewer extends TileEntitySpecialRenderer {

    private static RenderItem render = new RenderItem();

    public RenderTileWarehouseStockViewer() {

    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
        TileWarehouseStockViewer tile = (TileWarehouseStockViewer) te;
        EnumFacing d = EnumFacing.getOrientation(te.getBlockMetadata()).getOpposite();
        float r = getRotationFromDirection(d);

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderTools.setFullColorLightmap();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glTranslatef(0.5f, 1.f, 0.5f);//translate the point to the top-center of the block
        GL11.glRotatef(r, 0, 1, 0);//rotate for rotation
        GL11.glTranslatef(0.5f, 0, 0.5f);//translate to top-left corner
        GL11.glTranslatef(0, -0.125f, -0.127f);//move out and down for front-face of sign
        renderSignContents(tile);

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    float getRotationFromDirection(EnumFacing d) {
        switch (d) {
            case NORTH:
                return 180.f;
            case SOUTH:
                return 0.f;
            case EAST:
                return 90.f;
            case WEST:
                return 270.f;
            default:
                return 0.f;
        }
    }

    /**
     * matrix should be setup so that 0,0 is upper-left-hand corner of the sign-board, with a
     * transformation of 1 being 1 BLOCK
     */
    private void renderSignContents(TileWarehouseStockViewer tile) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, -0.002f);//move out from block face slightly, for depth-buffer/z-fighting
        GL11.glScalef(-1, -1, -1);//rescale for gui rendering axis flip
        GL11.glScalef(0.0050f, 0.0050f, 0.0050f);//this scale puts it at 200 units(pixels) per block
        GL11.glScalef(1f, 1f, 0.0001f);//squash Z axis for 'flat' rendering of 3d blocks/items..LOLS
        FontRenderer fr = func_147498_b();
        GL11.glDisable(GL11.GL_LIGHTING);
        ItemStack filterItem;
        WarehouseStockFilter filter;
        String name = "";
        List<WarehouseStockFilter> filters = tile.getFilters();
        int max = filters.size();
        if(10 < max){
            max = 10;
        }
        for (int i = 0; i < max; i++) {
            filter = filters.get(i);
            filterItem = filter.getFilterItem();
            if (filterItem != null) {
                render.renderItemAndEffectIntoGUI(fr, Minecraft.getMinecraft().getTextureManager(), filter.getFilterItem(), 0 + 12, i * 18 + 10);
            }
            name = filterItem == null ? "Empty Filter" : filterItem.getDisplayName();
            fr.drawString(name, 20 + 12, i * 18 + 4 + 10, 0xffffffff);
            name = String.valueOf(filter.getQuantity());
            fr.drawString(name, 200 - 13 - fr.getStringWidth(name), i * 18 + 4 + 10, 0xffffffff);
        }
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

}
