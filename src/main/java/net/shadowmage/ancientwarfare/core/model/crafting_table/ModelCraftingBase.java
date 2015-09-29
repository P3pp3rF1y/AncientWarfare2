package net.shadowmage.ancientwarfare.core.model.crafting_table;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

public class ModelCraftingBase extends ModelBase {

    private final ModelRenderer tableTop;
    public ModelCraftingBase() {
        textureWidth = 128;
        textureHeight = 128;
        tableTop = new ModelRenderer(this, "tableTop");
        tableTop.addBox(-8, -14, -8, 16, 1, 16);
        addLegs(13);
    }

    protected void addLegs(int height) {
        ModelRenderer leg1 = new ModelRenderer(this, "leg1");
        leg1.setTextureOffset(0, 18);
        leg1.addBox(-7, -height, 5, 2, height, 2);
        tableTop.addChild(leg1);
        ModelRenderer leg2 = new ModelRenderer(this, "leg2");
        leg2.setTextureOffset(9, 18);
        leg2.addBox(-7, -height, -7, 2, height, 2);
        tableTop.addChild(leg2);
        ModelRenderer leg3 = new ModelRenderer(this, "leg3");
        leg3.setTextureOffset(18, 18);
        leg3.addBox(5, -height, -7, 2, height, 2);
        tableTop.addChild(leg3);
        ModelRenderer leg4 = new ModelRenderer(this, "leg4");
        leg4.setTextureOffset(27, 18);
        leg4.addBox(5, -height, 5, 2, height, 2);
        tableTop.addChild(leg4);
    }

    public final ModelRenderer table(){
        return tableTop;
    }

    @Override
    public void render(Entity entity, float f1, float f2, float f3, float f4, float f5, float f6) {
        super.render(entity, f1, f2, f3, f4, f5, f6);
        setRotationAngles(f1, f2, f3, f4, f5, f6, entity);
        tableTop.render(f6);
    }

    public void renderModel() {
        tableTop.render(0.0625f);
    }

    public void renderModel(TileEntity te) {
        renderModel();
    }

    public final void setPieceRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public final void addPiece(ModelRenderer model){
        tableTop.addChild(model);
    }

    protected final void addHammer() {
        ModelRenderer hammerHead1 = new ModelRenderer(this, "hammerHead1");
        hammerHead1.setTextureOffset(36, 18);
        hammerHead1.setRotationPoint(-5, -14.25f, 5);
        setPieceRotation(hammerHead1, 0.09599306f, 0.3665189f, 0.069813065f);
        hammerHead1.addBox(0, -2, 0, 2, 2, 1);
        ModelRenderer hammerClaw1 = new ModelRenderer(this, "hammerClaw1");
        hammerClaw1.setTextureOffset(36, 22);
        hammerClaw1.setRotationPoint(0.125f, -2.25f, 0);
        setPieceRotation(hammerClaw1, 3.120892E-9f, -0.26179937f, 0);
        hammerClaw1.addBox(-2, 0, 0, 2, 1, 1);
        hammerHead1.addChild(hammerClaw1);
        ModelRenderer hammerClaw2 = new ModelRenderer(this, "hammerClaw2");
        hammerClaw2.setTextureOffset(36, 25);
        hammerClaw2.setRotationPoint(0.125f, -0.75f, 0);
        setPieceRotation(hammerClaw2, 3.120892E-9f, -0.26179937f, 0);
        hammerClaw2.addBox(-2, 0, 0, 2, 1, 1);
        hammerHead1.addChild(hammerClaw2);
        ModelRenderer hammerHead2 = new ModelRenderer(this, "hammerHead2");
        hammerHead2.setTextureOffset(36, 28);
        hammerHead2.setRotationPoint(2, -1.5f, 0);
        hammerHead2.addBox(0, 0, 0, 1, 1, 1);
        ModelRenderer hammerHead3 = new ModelRenderer(this, "hammerHead3");
        hammerHead3.setTextureOffset(36, 31);
        hammerHead3.setRotationPoint(0.5f, -0.5f, -0.5f);
        hammerHead3.addBox(0, 0, 0, 1, 2, 2);
        hammerHead2.addChild(hammerHead3);
        hammerHead1.addChild(hammerHead2);
        ModelRenderer hammerHandle = new ModelRenderer(this, "hammerHandle");
        hammerHandle.setTextureOffset(43, 18);
        hammerHandle.setRotationPoint(0.5f, -1.5f, -1);
        hammerHandle.addBox(0, 0, -7.5f, 1, 1, 10);
        hammerHead1.addChild(hammerHandle);
        addPiece(hammerHead1);
    }
}
