package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.core.interop.ModAccessors;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class EntityTools {
    
    /*
     * Teleport the player to the specified block position. Will try to place the player at once of the four positions beside
     * the block, if they can't fit anywhere beside then it will try on-top.
     * @param entityPlayer The player you want to teleport
     * @param world World object that the entity exists in and the teleport target exists in (cross-dimension teleport not possible)
     * @param targetPos An array containing the x/y/z co-ord of the target. IS NOT VALIDATED.
     * @param doRaw if true, will update the posX/posY/posZ fields instead of calling setPositionAndUpdate. Different parts of Minecraft need one or the other method for whatever reason so if one doesn't work try the other.
     * @return true is successful, otherwise false
     */
    public static boolean teleportPlayerToBlock(EntityPlayer entityPlayer, World world, BlockPos targetPos, boolean doRaw) {
        BlockPos tpPos = null;
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            if (EntityTools.canPlayerFit(world, targetPos.offset(facing))) {
                tpPos = targetPos.offset(facing);
                break;
            }
        }

        if (tpPos == null && EntityTools.canPlayerFit(world, targetPos.up())) {
            tpPos = targetPos.up();
        }

        if (tpPos != null) {
            if (doRaw) {
                entityPlayer.posX = tpPos.getX() + 0.5;
                entityPlayer.posY = tpPos.getY();
                entityPlayer.posZ = tpPos.getZ() + 0.5;
            } else {
                entityPlayer.setPositionAndUpdate(tpPos.getX() + 0.5, tpPos.getY(), tpPos.getZ() + 0.5);
            }
            return true;
        }
        
        return false;
    }
    
    private static boolean canPlayerFit(World world, BlockPos pos) {
        if (world.getBlockState(pos).getMaterial().blocksMovement())
            return false;
        if (world.getBlockState(pos.up()).getMaterial().blocksMovement())
            return false;
        return true;
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
        for(Class itemClass : itemClasses) {
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

    public static boolean isOwnerOrSameTeam(@Nullable EntityPlayer player, @Nullable UUID ownerId, String ownerName) {
        if(player == null)
            return false;
        if(ownerId!=null)
            return ModAccessors.FTBU.areFriendly(player.getUniqueID(), ownerId);
        return player.getName().equals(ownerName);
    }

	public static <T extends Entity> List<T> getEntitiesWithinBounds(World world, Class<? extends T> clazz, BlockPos p1, BlockPos p2) {
		AxisAlignedBB bb = new AxisAlignedBB(p1, p2.add(1, 1, 1));
		return world.getEntitiesWithinAABB(clazz, bb);
	}
}
