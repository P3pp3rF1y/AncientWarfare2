package net.shadowmage.ancientwarfare.core.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.manual.ManualContentRegistry;
import net.shadowmage.ancientwarfare.core.registry.RegistryLoader;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/*
 * client-proxy for AW-Core
 *
 * @author Shadowmage
 */
@SideOnly(Side.CLIENT)
public class ClientProxy extends ClientProxyBase {
	private static FontRenderer unicodeFontRenderer;

	public static FontRenderer getUnicodeFontRenderer() {
		return unicodeFontRenderer;
	}

	@Override
	public void preInit() {
		super.preInit();

		MinecraftForge.EVENT_BUS.register(this);

		if (AWCoreStatics.DEBUG) {
			setDebugResolution();
		}
	}

	@Override
	public void init() {
		InputHandler.initKeyBindings();
		RegistryLoader.registerParser(new ManualContentRegistry.ManualContentParser());
	}

	private void setDebugResolution() {
		org.lwjgl.opengl.DisplayMode mode = new DisplayMode(512, 288);
		try {
			Display.setDisplayMode(mode);
		}
		catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void postInit() {
		super.postInit();

		Minecraft mc = Minecraft.getMinecraft();
		unicodeFontRenderer = new FontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.getTextureManager(), true);
	}
}
