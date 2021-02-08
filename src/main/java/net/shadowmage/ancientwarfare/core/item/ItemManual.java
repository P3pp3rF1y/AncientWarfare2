package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.gamedata.WorldData;
import net.shadowmage.ancientwarfare.core.gui.manual.GuiManual;
import net.shadowmage.ancientwarfare.core.init.AWCoreItems;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

import javax.annotation.Nullable;
import java.util.List;

public class ItemManual extends ItemBaseCore {
	public ItemManual() {
		super("manual");
		setMaxStackSize(1);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		super.registerClient();

		NetworkHandler.registerGui(NetworkHandler.GUI_MANUAL, GuiManual.class);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_MANUAL);
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format("item.manual.tooltip"));
	}

	@SubscribeEvent
	public void handlePlayerFirstAWCraft(PlayerEvent.ItemCraftedEvent evt) {
		EntityPlayer player = evt.player;

		if (player.world.isRemote) {
			return;
		}

		Item item = evt.crafting.getItem();
		if (item != AWCoreItems.MANUAL && item.getRegistryName() != null && item.getRegistryName().getResourceDomain().startsWith("ancientwarfare")) {
			WorldData data = AWGameData.INSTANCE.getPerWorldData(player.getEntityWorld(), WorldData.class);
			if (!data.wasPlayerGivenManual(player)) {
				EntityItem manualDrop = new EntityItem(player.world, player.posX, player.posY, player.posZ, new ItemStack(AWCoreItems.MANUAL));
				manualDrop.setPickupDelay(0);
				player.world.spawnEntity(manualDrop);
				data.addPlayerThatWasGivenManual(player);
			}
		}
	}
}
