package net.shadowmage.ancientwarfare.core.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.model.crafting_table.ModelCraftingBase;

//TODO json model for both block and item form
public class TileCraftingTableRender extends TileEntitySpecialRenderer {

    private final ModelCraftingBase model;
    private final ResourceLocation texture;

    public TileCraftingTableRender(ModelCraftingBase model, String tex) {
        this.model = model;
        texture = new ResourceLocation("ancientwarfare", tex);
    }

//    @Override
//    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
//        GlStateManager.pushMatrix();
//        GlStateManager.scale(-1, -1, 1);
//        GlStateManager.translate(-0.5f, 0.0f, 0.5f);
//        GlStateManager.rotate(270, 0, 1, 0);
//        bindTexture(texture);
//        model.renderModel();
//        GlStateManager.popMatrix();
//    }

    @Override
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        float rotation = te instanceof IRotatableTile ? getRotation(((IRotatableTile) te).getPrimaryFacing()) : 0;
        GlStateManager.pushMatrix();

        GlStateManager.enableRescaleNormal();
        GlStateManager.translate(x + 0.5d, y, z + 0.5d);
        GlStateManager.rotate(rotation, 0, 1, 0);
        GlStateManager.scale(-1, -1, 1);
        bindTexture(texture);
        model.renderModel(te);

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    private float getRotation(EnumFacing direction) {
        switch (direction) {
            case SOUTH:
                return 180.f;
            case EAST:
                return 270.f;
            case WEST:
                return 90.f;
            default:
                return 0.f;
        }
    }

}
