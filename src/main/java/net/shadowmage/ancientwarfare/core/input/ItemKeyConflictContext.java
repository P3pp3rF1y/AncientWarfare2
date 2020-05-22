package net.shadowmage.ancientwarfare.core.input;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemKeyConflictContext implements IKeyConflictContext {
	public static final ItemKeyConflictContext INSTANCE = new ItemKeyConflictContext();

	private ItemKeyConflictContext() {
	}

	@Override
	public boolean isActive() {
		Minecraft mc = Minecraft.getMinecraft();
		//noinspection SimplifiableIfStatement
		if (mc.currentScreen != null || mc.player == null || mc.world == null) {
			return false;
		}

		return mc.player.getHeldItemMainhand().getItem() instanceof IItemKeyInterface || mc.player.getHeldItemOffhand().getItem() instanceof IItemKeyInterface;
	}

	@Override
	public boolean conflicts(IKeyConflictContext other) {
		return this == other;
	}
}
