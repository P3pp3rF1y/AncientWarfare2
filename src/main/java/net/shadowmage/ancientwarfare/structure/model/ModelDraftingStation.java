package net.shadowmage.ancientwarfare.structure.model;

import net.minecraft.client.model.ModelRenderer;
import net.shadowmage.ancientwarfare.core.model.crafting_table.ModelCraftingBase;

public class ModelDraftingStation extends ModelCraftingBase{

    public ModelDraftingStation(){
        ModelRenderer paperLarge = new ModelRenderer(this, "paperLarge");
        paperLarge.setTextureOffset(65, 0);
        paperLarge.setRotationPoint(0, -12.01f, 0);
        setRotation(paperLarge, 0, 0.087266594f, 0);
        paperLarge.addBox(-6, 0, -6, 12, 0, 12);
        ModelRenderer b1 = new ModelRenderer(this, "b1");
        b1.setTextureOffset(36, 18);
        b1.setRotationPoint(-4.5f, -2.5f, 4.5f);
        setPieceRotation(b1);

        b1.addBox(-0.5f, -0.5f, -0.5f, 10, 1, 1); //DONE

        paperLarge.addChild(b1);
        ModelRenderer b2 = new ModelRenderer(this, "b2");
        b2.setTextureOffset(36, 20);
        b2.setRotationPoint(-4.5f, -1.5f, -4.5f);
        setPieceRotation(b2);

        b2.addBox(-0.5f, -1.5f, -0.5f, 5, 1, 1);//DONE

        paperLarge.addChild(b2);
        ModelRenderer b3 = new ModelRenderer(this, "b3");
        b3.setTextureOffset(0, 32);
        b3.setRotationPoint(-4.5f, -2.5f, -3.5f);
        setPieceRotation(b3);

        b3.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 8); //DONE

        paperLarge.addChild(b3);
        ModelRenderer b4 = new ModelRenderer(this, "b4");
        b4.setTextureOffset(19, 32);
        b4.setRotationPoint(4.5f, -2.5f, -3.5f);
        setPieceRotation(b4);

        b4.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 8);//DONE

        paperLarge.addChild(b4);
        ModelRenderer b5 = new ModelRenderer(this, "b5");
        b5.setTextureOffset(36, 23);
        b5.setRotationPoint(1.5f, -2.5f, -4.5f);
        setPieceRotation(b5);

        b5.addBox(-0.5f, -0.5f, -0.5f, 4, 1, 1);//DONE

        paperLarge.addChild(b5);
        ModelRenderer b6 = new ModelRenderer(this, "b6");
        b6.setTextureOffset(0, 42);
        b6.setRotationPoint(-4.5f, -0.5f, -3.5f);
        setPieceRotation(b6);
        b6.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 8);//DONE
        paperLarge.addChild(b6);
        ModelRenderer b7 = new ModelRenderer(this, "b7");
        b7.setTextureOffset(49, 20);
        b7.setRotationPoint(-4.5f, -1.5f, -4.5f);
        setPieceRotation(b7);
        b7.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 2);//DONE
        paperLarge.addChild(b7);
        ModelRenderer b8 = new ModelRenderer(this, "b8");
        b8.setTextureOffset(59, 18);
        b8.setRotationPoint(-4.5f, -1.5f, 3.5f);
        setPieceRotation(b8);
        b8.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        paperLarge.addChild(b8);
        ModelRenderer b9 = new ModelRenderer(this, "b9");
        b9.setTextureOffset(56, 21);
        b9.setRotationPoint(-4.5f, -1.5f, 0.5f);
        setPieceRotation(b9);
        b9.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        paperLarge.addChild(b9);
        ModelRenderer b10 = new ModelRenderer(this, "b10");
        b10.setTextureOffset(47, 24);
        b10.setRotationPoint(-4.5f, -1.5f, -0.5f);
        setPieceRotation(b10);
        b10.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        paperLarge.addChild(b10);
        ModelRenderer b11 = new ModelRenderer(this, "b11");
        b11.setTextureOffset(36, 27);
        b11.setRotationPoint(-4.5f, 0.5f, -4.5f);
        setPieceRotation(b11);
        b11.addBox(-0.5f, -1.5f, -0.5f, 5, 1, 1);
        paperLarge.addChild(b11);
        ModelRenderer b12 = new ModelRenderer(this, "b12");
        b12.setTextureOffset(52, 24);
        b12.setRotationPoint(-0.5f, -1.5f, -4.5f);
        setPieceRotation(b12);
        b12.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        paperLarge.addChild(b12);
        ModelRenderer b13 = new ModelRenderer(this, "b13");
        b13.setTextureOffset(57, 24);
        b13.setRotationPoint(-3.5f, -1.5f, -4.5f);
        setPieceRotation(b13);
        b13.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        paperLarge.addChild(b13);
        ModelRenderer b14 = new ModelRenderer(this, "b14");
        b14.setTextureOffset(49, 27);
        b14.setRotationPoint(1.5f, 0.5f, -4.5f);
        setPieceRotation(b14);
        b14.addBox(-0.5f, -1.5f, -0.5f, 4, 1, 1);
        paperLarge.addChild(b14);
        ModelRenderer b15 = new ModelRenderer(this, "b15");
        b15.setTextureOffset(60, 27);
        b15.setRotationPoint(1.5f, -1.5f, -4.5f);
        setPieceRotation(b15);
        b15.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        paperLarge.addChild(b15);
        ModelRenderer b16 = new ModelRenderer(this, "b16");
        b16.setTextureOffset(47, 37);
        b16.setRotationPoint(4.5f, -1.5f, -4.5f);
        setPieceRotation(b16);
        b16.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 2);
        paperLarge.addChild(b16);
        ModelRenderer b17 = new ModelRenderer(this, "b17");
        b17.setTextureOffset(19, 42);
        b17.setRotationPoint(4.5f, -0.5f, -3.5f);
        setPieceRotation(b17);
        b17.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 8);//DONE
        paperLarge.addChild(b17);
        ModelRenderer b18 = new ModelRenderer(this, "b18");
        b18.setTextureOffset(38, 30);
        b18.setRotationPoint(4.5f, -1.5f, -0.5f);
        setPieceRotation(b18);
        b18.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 2);//DONE
        paperLarge.addChild(b18);
        ModelRenderer b19 = new ModelRenderer(this, "b19");
        b19.setTextureOffset(45, 30);
        b19.setRotationPoint(4.5f, -1.5f, 3.5f);
        setPieceRotation(b19);
        b19.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        paperLarge.addChild(b19);
        ModelRenderer b20 = new ModelRenderer(this, "b20");
        b20.setTextureOffset(38, 34);
        b20.setRotationPoint(-4.5f, -0.5f, 4.5f);
        setPieceRotation(b20);
        b20.addBox(-0.5f, -0.5f, -0.5f, 10, 1, 1);
        paperLarge.addChild(b20);
        ModelRenderer b21 = new ModelRenderer(this, "b21");
        b21.setTextureOffset(50, 30);
        b21.setRotationPoint(3.5f, -1.5f, -4.5f);
        setPieceRotation(b21);
        b21.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        paperLarge.addChild(b21);
        ModelRenderer b22 = new ModelRenderer(this, "b22");
        b22.setTextureOffset(55, 30);
        b22.setRotationPoint(-4.5f, -1.5f, 4.5f);
        setPieceRotation(b22);
        b22.addBox(-0.5f, -0.5f, -0.5f, 3, 1, 1);
        paperLarge.addChild(b22);
        ModelRenderer b23 = new ModelRenderer(this, "b23");
        b23.setTextureOffset(38, 37);
        b23.setRotationPoint(2.5f, -1.5f, 4.5f);
        setPieceRotation(b23);
        b23.addBox(-0.5f, -0.5f, -0.5f, 3, 1, 1);
        paperLarge.addChild(b23);
        ModelRenderer b25 = new ModelRenderer(this, "b25");
        b25.setTextureOffset(38, 42);
        b25.setRotationPoint(-3.5f, -3.5f, -3.5f);
        setPieceRotation(b25);
        b25.addBox(-0.5f, -0.5f, -0.5f, 8, 1, 8);
        paperLarge.addChild(b25);
        ModelRenderer b27 = new ModelRenderer(this, "b27");
        b27.setTextureOffset(60, 27);
        b27.setRotationPoint(0.5f, -2.5f, -4.5f);
        setPieceRotation(b27);
        b27.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        paperLarge.addChild(b27);

        addPiece(paperLarge);
    }

    @Override
    protected void addLegs(int height) {
        table().cubeList.clear();
        table().addBox(-8, -12, -8, 16, 1, 16);
        super.addLegs(11);
    }

    private void setPieceRotation(ModelRenderer piece){
        setRotation(piece, 1.0402973E-9f, 1.0402973E-9f, 0);
    }
}
