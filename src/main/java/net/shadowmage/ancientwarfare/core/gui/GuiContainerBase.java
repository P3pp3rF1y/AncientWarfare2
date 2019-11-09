package net.shadowmage.ancientwarfare.core.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Tooltip;
import net.shadowmage.ancientwarfare.core.interfaces.IContainerGuiCallback;
import net.shadowmage.ancientwarfare.core.interfaces.ITooltipRenderer;
import net.shadowmage.ancientwarfare.core.interfaces.IWidgetSelection;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class GuiContainerBase<T extends ContainerBase> extends GuiContainer implements IContainerGuiCallback, ITooltipRenderer, IWidgetSelection {

	private static LinkedList<Viewport> viewportStack = new LinkedList<>();

	private GuiElement selectedWidget = null;
	protected boolean shouldCloseOnVanillaKeys = true;
	private float partialRenderTick = 0.f;
	private boolean initDone = false;
	private boolean shouldUpdate = false;
	private List<GuiElement> elements = new ArrayList<>();

	private Tooltip elementTooltip;
	private int elementTooltipX;
	private int elementTooltipY;

	@Nonnull
	private ItemStack tooltipStack = ItemStack.EMPTY;
	private int tooltipX;
	private int tooltipY;

	private ResourceLocation backgroundTexture;

	protected EntityPlayer player;

	protected GuiContainerBase(ContainerBase container, int xSize, int ySize) {
		super(container);
		container.setGui(this);
		this.xSize = xSize;
		this.ySize = ySize;
		this.player = container.player;
		this.backgroundTexture = GuiElement.backgroundTextureLocation;
	}

	public GuiContainerBase(ContainerBase container) {
		this(container, 256, 240);
	}

	public final void setTexture(String name) {
		this.backgroundTexture = new ResourceLocation(name);
	}

	public final T getContainer() {
		return (T) this.inventorySlots;
	}

	@Override
	public void handleItemStackTooltipRender(ItemStack itemStack, int mouseX, int mouseY) {
		this.tooltipStack = itemStack;
		this.tooltipX = mouseX;
		this.tooltipY = mouseY;
	}

	@Override
	public void handleElementTooltipRender(Tooltip tooltip, int mouseX, int mouseY) {
		this.elementTooltip = tooltip;
		elementTooltipX = mouseX;
		elementTooltipY = mouseY;
	}

	protected void clearElements() {
		if (this.selectedWidget != null && this.elements.contains(selectedWidget)) {
			this.selectedWidget = null;
		}
		this.elements.clear();
	}

	protected void addGuiElement(GuiElement element) {
		this.elements.add(element);
	}

	protected void removeGuiElement(GuiElement element) {
		this.elements.remove(element);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		int button = Mouse.getEventButton();
		boolean state = Mouse.getEventButtonState();
		int wheel = Mouse.getEventDWheel();

		int type = button >= 0 ? (state ? Listener.MOUSE_DOWN : Listener.MOUSE_UP) : wheel != 0 ? Listener.MOUSE_WHEEL : Listener.MOUSE_MOVED;
		ActivationEvent evt = new ActivationEvent(type, button, state, x, y, wheel);
		for (GuiElement element : this.elements) {
			element.handleMouseInput(evt);
		}
	}

	@Override
	public void handleKeyboardInput() throws IOException {
		int key = Keyboard.getEventKey();
		boolean state = Keyboard.getEventKeyState();
		char ch = Keyboard.getEventCharacter();

		ActivationEvent evt = new ActivationEvent(state ? Listener.KEY_DOWN : Listener.KEY_UP, key, ch, state);
		for (GuiElement element : this.elements) {
			element.handleKeyboardInput(evt);
		}

		if (selectedWidget == null) {
			super.handleKeyboardInput();
		}
	}

	@Override
	protected void keyTyped(char ch, int key) throws IOException {
		if (selectedWidget == null) {
			boolean isExitCommand = key == 1 || key == this.mc.gameSettings.keyBindInventory.getKeyCode();
			if (isExitCommand) {
				if (shouldCloseOnVanillaKeys) {
					closeGui();
				}
			} else {
				super.keyTyped(ch, key);
			}
		}
	}

	protected final void closeGui() {
		if (onGuiCloseRequested()) {
			Minecraft.getMinecraft().player.closeScreen();
		}
	}

	/*
	 * over-rideable method for onGuiClosed w/ proper functionality -- (is called before the fucking container disappears)
	 * derived classes can override this to handle custom onClosed functionality (e.g. call container to send data to server/
	 * finalize settings)
	 *
	 * @return true if the GUI should closed -- false if another GUI has been opened/the GUI should not closed
	 */
	protected boolean onGuiCloseRequested() {
		return true;
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		if (!initDone) {
			initElements();
			initDone = true;
		}
		this.setupElements();
		for (GuiElement element : this.elements) {
			element.updateGuiPosition(guiLeft, guiTop);
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void updateScreen() {
		if (this.shouldUpdate) {
			((ContainerBase) inventorySlots).setGui(this);
			this.initGui();
			this.shouldUpdate = false;
		}
		super.updateScreen();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		if (backgroundTexture != null) {
			Minecraft.getMinecraft().renderEngine.bindTexture(backgroundTexture);
			RenderTools.renderQuarteredTexture(256, 256, 0, 0, 256, 240, width / 2 - xSize / 2, (height / 2) - (ySize / 2), xSize, ySize);
		}
		Minecraft.getMinecraft().renderEngine.bindTexture(GuiElement.widgetTexture1);
		for (Slot slot : this.inventorySlots.inventorySlots) {
			this.drawTexturedModalRect(slot.xPos - 1 + guiLeft, slot.yPos - 1 + guiTop, 152, 120, 18, 18);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		partialRenderTick = partialTicks;
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
		if (!tooltipStack.isEmpty()) {
			super.renderToolTip(tooltipStack, tooltipX, tooltipY);
			tooltipStack = ItemStack.EMPTY;
		}
		if (elementTooltip != null) {
			elementTooltip.renderTooltip(elementTooltipX, elementTooltipY, partialRenderTick);
			elementTooltip = null;
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(-guiLeft, -guiTop, 0);
		long time = System.currentTimeMillis();
		GlStateManager.color(1.f, 1.f, 1.f, 1.f);
		for (GuiElement element : elements) {
			element.render(par1, par2, partialRenderTick);
			element.postRender(par1, par2, partialRenderTick, time, this);
		}
		GlStateManager.popMatrix();
	}

	/*
	 * call this method to enforce a re-initialization of gui at the start of next game tick (not render tick)<br>
	 * setupElements() will then be called<br>
	 * and all elements will have their position updated
	 */
	@Override
	public void refreshGui() {
		this.shouldUpdate = true;
	}

	/*
	 * Sub-classes should implement this method to add initial gui elements.<br>
	 * Only called a single time, shortly after construction,
	 * but before any rendering or other update methods are called
	 */
	public abstract void initElements();

	/*
	 * sub-classes should implement this method to setup/change any elements that need adjusting when the gui is initialized<br>
	 * any elements that are positioned outside of the gui-window space will need their positions updated by calling element.setPosition(xPos, yPos)
	 * as they reference internal position relative to the guiLeft / guiTop values from this gui (which are passed in and updated directly after setupElements() is called)<br>
	 * Always called at least once, directly after {@link #initElements()}
	 */
	public abstract void setupElements();

	/*
	 * sub-classes should override this method to handle any expected packet data
	 */
	@Override
	public void handlePacketData(NBTTagCompound data) {

	}

	/*
	 * deferred to allow proper render-order, and draw the tooltip on top of everything else
	 */
	@Override
	protected void renderToolTip(ItemStack stack, int x, int y) {
		tooltipStack = stack;
		tooltipX = x;
		tooltipY = y;
	}

	@Override
	public void onWidgetSelected(GuiElement element) {
		this.selectedWidget = element;
		element.setSelected(true);
	}

	@Override
	public void onWidgetDeselected(GuiElement element) {
		if (selectedWidget == element) {
			selectedWidget = null;
		}
		element.setSelected(false);
	}

	public void onCompositeCleared(List<GuiElement> compositeElements) {
		if (compositeElements.contains(selectedWidget)) {
			selectedWidget = null;
		}
	}

	/*
	 * Action event for gui-widgets.  A single event is sent to all element / listeners for each input event (key up/down, mouse up/down/move/wheel)
	 *
	 * @author Shadowmage
	 */
	public static class ActivationEvent {
		/*
		 * the type of event:<br>
		 * 0=Key up <br>
		 * 1=Key down <br>
		 * 2=Mouse up <br>
		 * 4=Mouse Down <br>
		 * 8=Mouse Wheel
		 */
		public final int type;

		/*
		 * what key was pressed?
		 */
		public int key;

		/*
		 * the mouse button number.
		 * -1 = none
		 * 0 = LMB
		 * 1 = RMB
		 * 2+= ?
		 */
		public int mButton;
		/*
		 * the state of the button or key.  true for pressed, false for released
		 */
		public boolean state;
		/*
		 * the input char, for keyboard events
		 */
		public char ch;
		/*
		 * mouse x position
		 */
		public int mx;
		/*
		 * mouse y position
		 */
		public int my;
		/*
		 * mouse-wheel delta movement
		 */
		public int mw;//mousewheel delta movement

		private ActivationEvent(int type, int button, boolean state, int mx, int my, int mw) {
			this.type = type;
			this.mButton = button;
			this.state = state;
			this.mx = mx;
			this.my = my;
			this.mw = mw;
		}

		private ActivationEvent(int type, int key, char character, boolean state) {
			this.type = type;
			this.key = key;
			this.ch = character;
			this.state = state;
		}
	}

	/*
	 * Push a new scissors-test viewport onto the stack.<br>
	 * If this viewport would extend outside of the currently-set viewport, it
	 * will be truncated to fit inside of the existing viewport
	 */
	public static void pushViewport(int x, int y, int w, int h) {
		int tlx, tly, brx, bry;
		tlx = x;
		tly = y;
		brx = x + w;
		bry = y + h;

		Viewport p = viewportStack.peek();
		if (p != null) {
			if (tlx < p.x) {
				tlx = p.x;
			}
			if (brx > p.x + p.w) {
				brx = p.x + p.w;
			}
			if (tly < p.y) {
				tly = p.y;
			}
			if (bry > p.y + p.h) {
				bry = p.y + p.h;
			}
		}
		x = tlx;
		y = tly;
		w = brx - tlx;
		h = bry - tly;

		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution scaledRes = new ScaledResolution(mc);
		int guiScale = scaledRes.getScaleFactor();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);

		GL11.glScissor(x * guiScale, mc.displayHeight - y * guiScale - h * guiScale, w * guiScale, h * guiScale);

		viewportStack.push(new Viewport(x, y, w, h));
	}

	/*
	 * pop a scissors-test viewport off of the stack
	 */
	public static void popViewport() {
		Viewport p = viewportStack.poll();
		if (p == null) {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}
		p = viewportStack.peek();
		if (p != null) {
			Minecraft mc = Minecraft.getMinecraft();
			ScaledResolution scaledRes = new ScaledResolution(mc);
			int guiScale = scaledRes.getScaleFactor();
			GL11.glEnable(GL11.GL_SCISSOR_TEST);

			GL11.glScissor(p.x * guiScale, mc.displayHeight - p.y * guiScale - p.h * guiScale, p.w * guiScale, p.h * guiScale);
		} else {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}
	}

	/*
	 * class used to represent a currently drawable portion of the screen.
	 * Used in a stack for figuring out what composites may draw where
	 *
	 * @author John
	 */
	private static class Viewport {
		int x, y, w, h;

		private Viewport(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.h = h;
			this.w = w;
		}
	}

}
