package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EntityTools {
	private static final String FACTION_NAME_TAG = "factionName";

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
		return "entity." + (registryName.getResourceDomain().equals(AncientWarfareNPC.MOD_ID) ? AncientWarfareNPC.MOD_ID + "." : "") + e.getName() + ".name";
	}

	public static <T extends Entity> List<T> getEntitiesWithinBounds(World world, Class<? extends T> clazz, BlockPos p1, BlockPos p2) {
		AxisAlignedBB bb = new AxisAlignedBB(p1, p2.add(1, 1, 1));
		return world.getEntitiesWithinAABB(clazz, bb);
	}

	public static void spawnEntity(World world, ResourceLocation entity, NBTTagCompound entityNBT, BlockPos pos) {
		Entity e = EntityList.createEntityByIDFromName(entity, world);
		if (e == null)
			return;
		e.setLocationAndAngles(pos.getX() + 0.5d, pos.getY(), pos.getZ() + 0.5d, world.rand.nextFloat() * 360, 0);
		if (e instanceof EntityLiving) {
			((EntityLiving) e).onInitialSpawn(world.getDifficultyForLocation(e.getPosition()), null);
			((EntityLiving) e).spawnExplosionParticle();
		}
		setDataFromTag(e, entityNBT); //some data needs to be set before spawning entity in the world (like factionName)
		world.spawnEntity(e);
		setDataFromTag(e, entityNBT); //and some data needs to be set after onInitialSpawn fires for entity]
	}

	private static void setDataFromTag(Entity e, NBTTagCompound entityNBT) {
		NBTTagCompound temp = new NBTTagCompound();
		if (e instanceof NpcFaction && entityNBT.hasKey(FACTION_NAME_TAG)) {
			((NpcFaction) e).setFactionNameAndDefaults(entityNBT.getString(FACTION_NAME_TAG));
		}
		e.writeToNBT(temp);
		Set<String> keys = entityNBT.getKeySet();
		for (String key : keys) {
			temp.setTag(key, entityNBT.getTag(key));
		}
		e.readFromNBT(temp);
	}
}
