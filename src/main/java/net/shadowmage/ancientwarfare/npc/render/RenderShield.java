//package net.shadowmage.ancientwarfare.npc.render;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.ItemRenderer;
//import net.minecraft.client.renderer.OpenGlHelper;
//import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.client.renderer.texture.TextureManager;
//import net.minecraft.client.renderer.texture.TextureUtil;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.client.IItemRenderer;
//import org.lwjgl.opengl.GL11;
//import org.lwjgl.opengl.GL12;
//
//public class RenderShield implements IItemRenderer {
//
//    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
//    public RenderShield() {
//    }
//
//    @Override
//    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
//        return type == ItemRenderType.EQUIPPED;
//    }
//
//    @Override
//    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
//        return type == ItemRenderType.EQUIPPED && helper == ItemRendererHelper.EQUIPPED_BLOCK;
//    }
//
//    @Override
//    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
//        render((EntityLivingBase) data[1], item);
//    }
//
//    private void render(EntityLivingBase entity, ItemStack stack) {
//        IIcon iicon = entity.getItemIcon(stack, stack.getItemDamage());
//        if (iicon == null) {
//            return;
//        }
//        GlStateManager.pushMatrix();
//        TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
//        texturemanager.bindTexture(texturemanager.getResourceLocation(stack.getItemSpriteNumber()));
//        TextureUtil.func_152777_a(false, false, 1.0F);
//        GlStateManager.enableRescaleNormal();
//
//        GlStateManager.translate(-0.0F, -0.3F, 0.0F);
//        float f6 = 1.5F;
//        GlStateManager.scale(f6, f6, f6);
//        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
//        GlStateManager.translate(0.0625f, 0.125F, 0.6875f - 3.f * 0.0625f);
//        GlStateManager.rotate(90, 0, 1, 0);
//        GlStateManager.translate(-8.f * 0.0625f, 10.f * 0.0625f, 0);
//        GlStateManager.rotate(-80.f, 1, 0, 0);
//        GlStateManager.translate(0, -3.f * 0.0625f, 0);
//        Tessellator tessellator = Tessellator.getInstance();
//        float f = iicon.getMinU();
//        float f1 = iicon.getMaxU();
//        float f2 = iicon.getMinV();
//        float f3 = iicon.getMaxV();
//        ItemRenderer.renderItemIn2D(tessellator, f1, f2, f, f3, iicon.getIconWidth(), iicon.getIconHeight(), 0.0625F);
//
//        if (stack.hasEffect(stack.getItemDamage())){
//            GlStateManager.depthFunc(GL11.GL_EQUAL);
//            GlStateManager.disableLighting();
//            texturemanager.bindTexture(RES_ITEM_GLINT);
//            GlStateManager.enableBlend();
//            OpenGlHelper.glBlendFunc(768, 1, 1, 0);
//            float f7 = 0.76F;
//            GlStateManager.color(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
//            GlStateManager.matrixMode(GL11.GL_TEXTURE);
//            GlStateManager.pushMatrix();
//            float f8 = 0.125F;
//            GlStateManager.scale(f8, f8, f8);
//            float f9 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
//            GlStateManager.translate(f9, 0.0F, 0.0F);
//            GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
//            ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
//            GlStateManager.popMatrix();
//            GlStateManager.pushMatrix();
//            GlStateManager.scale(f8, f8, f8);
//            f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
//            GlStateManager.translate(-f9, 0.0F, 0.0F);
//            GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
//            ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
//            GlStateManager.popMatrix();
//            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
//            GlStateManager.disableBlend();
//            GlStateManager.enableLighting();
//            GlStateManager.depthFunc(GL11.GL_LEQUAL);
//        }
//
//        GlStateManager.disableRescaleNormal();
//        texturemanager.bindTexture(texturemanager.getResourceLocation(stack.getItemSpriteNumber()));
//        TextureUtil.func_147945_b();
//        GlStateManager.popMatrix();
//    }
//
//
//}
