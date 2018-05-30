package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

import javax.annotation.Nullable;
import java.util.List;

public class EntityTools {
	private EntityTools() {
	}

	@Nullable
	public static EnumHand getHandHoldingItem(EntityLivingBase entity, Item item) {
		if (entity.getHeldItemMainhand().getItem() == item) {
			return EnumHand.MAIN_HAND;
		} else if (entity.getHeldItemOffhand().getItem() == item) {
			return EnumHand.OFF_HAND;
		}
		return null;
	}

	public static ItemStack getItemFromEitherHand(EntityPlayer player, Class... itemClasses) {
		for (Class itemClass : itemClasses) {
			if (itemClass.isInstance(player.getHeldItemMainhand().getItem())) {
				return player.getHeldItemMainhand();
			} else if (itemClass.isInstance(player.getHeldItemOffhand().getItem())) {
				return player.getHeldItemOffhand();
			}
		}
		return ItemStack.EMPTY;
	}

	public static String getUnlocName(String resourceLocation) {
		return getUnlocName(new ResourceLocation(resourceLocation));
	}

	public static String getUnlocName(ResourceLocation registryName) {
		EntityEntry e = ForgeRegistries.ENTITIES.getValue(registryName);
		return "entity." + (registryName.getResourceDomain().equals(AncientWarfareNPC.modID) ? "AncientWarfareNpc." : "") + e.getName() + ".name";
	}

	public static <T extends Entity> List<T> getEntitiesWithinBounds(World world, Class<? extends T> clazz, BlockPos p1, BlockPos p2) {
		AxisAlignedBB bb = new AxisAlignedBB(p1, p2.add(1, 1, 1));
		return world.getEntitiesWithinAABB(clazz, bb);
	}
}
