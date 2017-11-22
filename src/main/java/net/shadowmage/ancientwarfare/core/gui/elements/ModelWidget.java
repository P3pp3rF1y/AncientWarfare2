package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;
import net.shadowmage.ancientwarfare.core.model.Primitive;
import net.shadowmage.ancientwarfare.core.util.AWTextureManager;
import net.shadowmage.ancientwarfare.core.util.Trig;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/*
 * renders a model into its display area
 * has optional model-view input for rotation/movement/zoom
 * has optional piece selection mode, with callback
 * <p/>
 * contains all methods necessary to:<br>
 * load model from disk<br>
 * save model to disk<br>
 * load model texture from disk<br>
 * add / remove / manipulate pieces<br>
 * add / remove / manipulate primitives<br>
 *
 * @author Shadowmage
 */
public class ModelWidget extends GuiElement {

    ModelLoader loader = new ModelLoader();
    private ModelBaseAW model;
    private ModelPiece selectedPiece = null;
    private Primitive selectedPrimitive = null;

    private int downX;
    private int downY;

    boolean dragging = false;
    boolean dragLeft = true;
    private int lastX;
    private int lastY;

    private boolean selectable = false;
    private boolean doSelection = false;
    private int selectionX;
    private int selectionY;

    int gridDisplayList = -1;

    float yaw;
    float pitch;
    float viewDistance = 5.f;

    /*
     * stored/calc'd values
     */
    float viewPosX, viewPosY, viewPosZ, viewTargetX, viewTargetY, viewTargetZ;

    ResourceLocation texture;

    public ModelWidget(int topLeftX, int topLeftY, int width, int height) {
        super(topLeftX, topLeftY, width, height);
        this.addNewListener(new Listener(Listener.MOUSE_UP) {
            public boolean onEvent(GuiElement widget, ActivationEvent evt) {
                if (isMouseOverElement(evt.mx, evt.my)) {
                    if (selectable && downX == evt.mx && downY == evt.my) {
                        doSelection = true;
                        selectionX = Mouse.getX();
                        selectionY = Mouse.getY();
                    }
                    dragging = false;
                }
                return true;
            }

            ;
        });
        this.addNewListener(new Listener(Listener.MOUSE_DOWN) {
            @Override
            public boolean onEvent(GuiElement widget, ActivationEvent evt) {
                if (isMouseOverElement(evt.mx, evt.my)) {
                    dragging = true;
                    downX = evt.mx;
                    downY = evt.my;
                    lastX = evt.mx;
                    lastY = evt.my;
                    dragLeft = evt.mButton == 0;
                }
                return true;
            }
        });
        this.addNewListener(new Listener(Listener.MOUSE_MOVED) {
            @Override
            public boolean onEvent(GuiElement widget, ActivationEvent evt) {
                if (dragging && isMouseOverElement(evt.mx, evt.my)) {
                    handleMouseDragged(evt.mx, evt.my);
                } else {
                    dragging = false;
                    lastX = evt.mx;
                    lastY = evt.my;
                }
                return true;
            }
        });
        this.addNewListener(new Listener(Listener.MOUSE_WHEEL) {
            @Override
            public boolean onEvent(GuiElement widget, ActivationEvent evt) {
                if (isMouseOverElement(evt.mx, evt.my)) {
                    handleMouseWheel(evt.mw);
                }
                return true;
            }
        });

        viewPosZ = 5;
        viewPosY = 5;
    }

    public void setTexture(ResourceLocation loc) {
        this.texture = loc;
    }

    private void handleMouseDragged(int mx, int my) {
        int dx = mx - lastX;
        int dy = my - lastY;
        if (dragLeft) {
            float xChange = dy * MathHelper.sin(pitch) * MathHelper.sin(yaw);
            float zChange = dy * MathHelper.sin(pitch) * MathHelper.cos(yaw);

            xChange += MathHelper.cos(yaw) * dx;
            zChange -= MathHelper.sin(yaw) * dx;

            float yChange = dy * MathHelper.cos(pitch);

            viewPosX -= xChange * 0.1f;
            viewPosY += yChange * 0.1f;
            viewPosZ -= zChange * 0.1f;

            viewTargetX -= xChange * 0.1f;
            viewTargetY += yChange * 0.1f;
            viewTargetZ -= zChange * 0.1f;
        } else {
            yaw -= dx * Trig.TORADIANS;
            pitch += dy * Trig.TORADIANS;
            if (pitch * Trig.TODEGREES >= 89.f) {
                pitch = 89.f * Trig.TORADIANS;
            }
            if (pitch * Trig.TODEGREES <= -89.f) {
                pitch = -89.f * Trig.TORADIANS;
            }
            viewPosX = viewTargetX + viewDistance * MathHelper.sin(yaw) * MathHelper.cos(pitch);
            viewPosZ = viewTargetZ + viewDistance * MathHelper.cos(yaw) * MathHelper.cos(pitch);
            viewPosY = viewTargetY + viewDistance * MathHelper.sin(pitch);
        }
        lastX = mx;
        lastY = my;
    }

    private void handleMouseWheel(int wheel) {
        if (wheel < 0) {
            viewDistance += 0.25f;
        } else {
            viewDistance -= 0.25f;
        }
        viewPosX = viewTargetX + viewDistance * MathHelper.sin(yaw) * MathHelper.cos(pitch);
        viewPosZ = viewTargetZ + viewDistance * MathHelper.cos(yaw) * MathHelper.cos(pitch);
        viewPosY = viewTargetY + viewDistance * MathHelper.sin(pitch);
    }

    /*
     * if true, will enable mouse-picking of model pieces/primitives
     */
    public void setSelectable(boolean val) {
        this.selectable = val;
    }

    public void setModel(ModelBaseAW model) {
        this.model = model;
        selectedPiece = null;
        selectedPrimitive = null;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        GlStateManager.enableDepth();
        setViewport();
        if (model != null) {
            if (doSelection) {
                GlStateManager.clearColor(0.2f, 0.2f, 0.2f, 0.2f);
                GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                doSelection();
                doSelection = false;
            }
            GlStateManager.clearColor(0.2f, 0.2f, 0.2f, 0.2f);
            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            renderGrid();

            enableModelLighting();
            GlStateManager.color(1.f, 1.f, 1.f, 1.f);

            Minecraft.getMinecraft().renderEngine.bindTexture(texture);
            calculateHighlightedPieces();
            model.renderForEditor(selectedPiece, selectedPrimitive, parents);
            parents.clear();
        }

        resetViewport();
        GlStateManager.disableDepth();//re-disable for rendering of the rest of widgets
        GlStateManager.disableLighting();
    }

    List<ModelPiece> parents = new ArrayList<>();

    private void calculateHighlightedPieces() {
        parents.clear();
        if (selectedPiece != null) {
            selectedPiece.getPieces(parents);
        }
    }

    private void enableModelLighting() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        int bright = player.world.getCombinedLight(player.getPosition(), 0);

        int var11 = bright % 65536;
        int var12 = bright / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) var11 / 1.0F, (float) var12 / 1.0F);

        RenderHelper.enableStandardItemLighting();
    }

    private void renderGrid() {
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.glLineWidth(2.f);
        if (gridDisplayList >= 0) {
            GlStateManager.callList(gridDisplayList);
        } else {
            gridDisplayList = GlStateManager.glGenLists(1);
            GlStateManager.glNewList(gridDisplayList, GL11.GL_COMPILE_AND_EXECUTE);
            GlStateManager.color(0.f, 0.f, 1.f, 1.f);
            for (int x = -5; x <= 5; x++) {
                GlStateManager.glBegin(GL11.GL_LINE_LOOP);
                GlStateManager.glVertex3f(x, 0.f, -5.f);
                GlStateManager.glVertex3f(x, 0.f, 5.f);
                GlStateManager.glEnd();
            }
            for (int z = -5; z <= 5; z++) {
                GlStateManager.glBegin(GL11.GL_LINE_LOOP);
                GlStateManager.glVertex3f(-5.f, 0.f, z);
                GlStateManager.glVertex3f(5.f, 0.f, z);
                GlStateManager.glEnd();
            }
            GlStateManager.color(1.f, 1.f, 1.f, 1.f);
            GlStateManager.glEndList();
        }
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
    }

    private void setViewport() {
        /*
         * load a clean projection matrix
         */
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();

        /*
         * set up the base projection transformation matrix, as well as view target and position
         * (camera setup)
         */
        Minecraft mc = Minecraft.getMinecraft();
        float aspect = (float) mc.displayWidth / (float) mc.displayHeight;
        GLU.gluPerspective(60.f, aspect, 0.1f, 100.f);
        GLU.gluLookAt(viewPosX, viewPosY, viewPosZ, viewTargetX, viewTargetY, viewTargetZ, 0, 1, 0);

        /*
         * load a clean model-view matrix
         */
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();

        /*
         * and finally, clear the depth buffer
         * (we want to ignore any world/etc, as we're rendering over-top of it all anyway)
         */
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);

        /*
         * set the cropped viewport to render to
         */
        GuiContainerBase.pushViewport(renderX, renderY, width, height);
    }

    private void resetViewport() {
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();
        GuiContainerBase.popViewport();
    }

    /*
     * render for selection
     */
    private void doSelection() {
        int posX = selectionX;
        int posY = selectionY;

        GlStateManager.disableTexture2D();
        GlStateManager.clearColor(1.f, 1.f, 1.f, 1.f);
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        model.renderForSelection();

        byte[] pixelColorsb = new byte[3];
        ByteBuffer pixelColors = ByteBuffer.allocateDirect(3);
        GL11.glReadPixels(posX, posY, 1, 1, GL11.GL_RGB, GL11.GL_BYTE, pixelColors);

        for (int i = 0; i < 3; i++) {
            pixelColorsb[i] = pixelColors.get(i);
        }

        int r = pixelColorsb[0] & 255;
        int g = pixelColorsb[1] & 255;
        int b = pixelColorsb[2] & 255;

        GlStateManager.enableTexture2D();
        int color = (r << 14) | (g << 7) | b;

        GlStateManager.clearColor(.2f, .2f, .2f, 1.f);
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        Primitive p = model.getPrimitive(color);

        this.selectedPrimitive = p;

        if (p == null) {
            this.selectedPiece = null;
        } else {
            this.selectedPiece = p.parent;
        }
        this.onSelection(selectedPiece, selectedPrimitive);
    }

    public ModelPiece getSelectedPiece() {
        return selectedPiece;
    }

    public Primitive getSelectedPrimitive() {
        return selectedPrimitive;
    }

    public ModelBaseAW getModel() {
        return model;
    }

    /*
     * add a new fully defined primitive to the model
     * <p/>
     * sets the current selected primitive to the passed in primitive
     * after adding it to the model / piece
     * <p/>
     * will NOT add the primitive or select it if current selected piece==null
     * OR if the current selected piece != p.parent
     */
    public void addNewPrimitive(Primitive p) {
        if (p.parent != this.selectedPiece) {
            return;
        }
        p.parent.addPrimitive(p);
        this.selectedPiece = p.parent;
        this.selectedPrimitive = p;
        this.onSelection(selectedPiece, selectedPrimitive);
    }

    /*
     * adds a new model-piece to the model
     * parent = current piece, or null if no current piece
     * origin = parent origin, or 0,0,0 if no parent
     * sets the current selected piece to the new piece
     * sets the current selected primitive to null
     */
    public void addNewPiece(String pieceName) {
        ModelPiece pieceParent = this.selectedPiece == null ? null : this.selectedPiece;
        ModelPiece newPiece = new ModelPiece(pieceName, 0, 0, 0, 0, 0, 0, pieceParent);
        model.addPiece(newPiece);
        this.selectedPiece = newPiece;
        this.selectedPrimitive = null;
        this.onSelection(selectedPiece, selectedPrimitive);
    }

    /*
     * delete the selected piece
     */
    public void deleteSelectedPiece() {
        if (selectedPiece != null) {
            selectedPiece.setParent(null);
            model.removePiece(selectedPiece);
        }
        this.selectedPiece = null;
        this.selectedPrimitive = null;
        this.onSelection(selectedPiece, selectedPrimitive);
    }

    public void deleteSelectedPrimitive() {
        if (this.selectedPrimitive != null) {
            this.selectedPrimitive.parent.removePrimitive(selectedPrimitive);
        }
        this.selectedPrimitive = null;
        this.onSelection(selectedPiece, selectedPrimitive);
    }

    /*
     * copies the currently selected piece.
     * adds it as a child of the current selected piece's parent -- as a sibling of the current piece
     * sets current selected piece to the new copied peice
     * copied piece will have an automatically generated piece-name
     */
    public void copyPiece() {
        if (selectedPiece != null) {
            ModelPiece copy = selectedPiece.copy();
            if (copy.getParent() != null) {
                copy.getParent().addChild(copy);
            }
            model.addPiece(copy);
            selectedPiece = copy;
            selectedPrimitive = null;
        }
        this.onSelection(selectedPiece, selectedPrimitive);
    }

    public void renameCurrentPiece(String name) {
        ModelPiece piece = this.getSelectedPiece();
        piece.setName(name);
        this.model.removePiece(piece);
        this.model.addPiece(piece);
        this.onSelection(selectedPiece, selectedPrimitive);
    }

    public void copyPrimitive() {
        Primitive p = this.getSelectedPrimitive().copy();
        this.getSelectedPiece().addPrimitive(p);
        this.selectedPrimitive = p;
        this.onSelection(selectedPiece, selectedPrimitive);
    }

    /*
     * swaps the parent of the selectedPiece to the passed in ModelPiece<br>
     * pass null for a base piece
     */
    public void swapPieceParent(ModelPiece newParent) {
        this.getSelectedPiece().setParent(newParent);
    }

    /*
     * swaps the parent of the selected primitive to the passed in ModelPiece
     */
    public void swapPrimitiveParent(ModelPiece newParent) {
        Primitive p = this.getSelectedPrimitive();
        ModelPiece oldParent = p.parent;
        oldParent.removePrimitive(p);
        newParent.addPrimitive(p);
    }

    public void loadModel(File file) {
        ModelBaseAW model = loader.loadModel(file);
        if (model != null) {
            this.model = model;
            this.setSelection(null, null);
        }
    }

    public void setSelection(ModelPiece piece, Primitive p) {
        this.selectedPiece = piece;
        this.selectedPrimitive = p;
        this.onSelection(piece, p);
        calculateHighlightedPieces();
    }

    public void saveModel(File file) {
        loader.saveModel(model, file);
    }

    public void loadTexture(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            this.setTexture(AWTextureManager.instance().loadImageBasedTexture(file.getAbsolutePath(), image));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importPieces(File file) {
        ModelBaseAW model = loader.loadModel(file);
        if (model != null) {
            /*
             * TODO validate names to check for duplicates
             */
            for (ModelPiece p : model.getBasePieces()) {
                this.model.addPiece(p);
            }
        }
    }

    /*
     * implementations should override to provide a callback for piece selection
     */
    protected void onSelection(ModelPiece piece, Primitive primitive) {
        //NOOP in base widget, implementation must be provided via anonymous inner-class overrides
    }

    public void clearPieceParent() {
        if (selectedPiece != null) {
            model.removePiece(selectedPiece);
            selectedPiece.setParent(null);
            model.addPiece(selectedPiece);
        }
    }

}
