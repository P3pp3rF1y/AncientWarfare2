/*
 Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
 This software is distributed under the terms of the GNU General Public License.
 Please see COPYING for precise license information.

 This file is part of Ancient Warfare.

 Ancient Warfare is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Ancient Warfare is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.


 */

package net.shadowmage.ancientwarfare.core.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.entity.AWFakePlayer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class BlockTools {

	/*
	 * rotate a float X offset (-1<=x<=1) within a block
	 */
	public static float rotateFloatX(float x, float z, int turns) {
		float x1, z1;
		x1 = x;
		z1 = z;
		for (int i = 0; i < turns; i++) {
			z = x1;
			x = 1.f - z1;
			x1 = x;
			z1 = z;
		}
		return x;
	}

	public static float rotateFloatZ(float x, float z, int turns) {
		float x1, z1;
		x1 = x;
		z1 = z;
		for (int i = 0; i < turns; i++) {
			z = x1;
			x = 1.f - z1;
			x1 = x;
			z1 = z;
		}
		return z;
	}

	public static BlockPos getAverageOf(BlockPos... positions) {
		float x = 0;
		float y = 0;
		float z = 0;
		int count = 0;
		for (BlockPos pos : positions) {
			x += pos.getX();
			y += pos.getY();
			z += pos.getZ();
			count++;
		}
		if (count > 0) {
			x /= count;
			y /= count;
			z /= count;
		}
		return new BlockPos(x, y, z);
	}

	/*
	 * will return null if nothing is in range
	 */
	@Nullable
	public static BlockPos getBlockClickedOn(EntityPlayer player, World world, boolean offset) {
		//TODO can this be replaced with regular rayTrace?
		float rotPitch = player.rotationPitch;
		float rotYaw = player.rotationYaw;
		double testX = player.posX;
		double testY = player.posY + player.getEyeHeight();
		double testZ = player.posZ;
		Vec3d testVector = new Vec3d(testX, testY, testZ);
		float var14 = MathHelper.cos(-rotYaw * 0.017453292F - (float) Math.PI);
		float var15 = MathHelper.sin(-rotYaw * 0.017453292F - (float) Math.PI);
		float var16 = -MathHelper.cos(-rotPitch * 0.017453292F);
		float vectorY = MathHelper.sin(-rotPitch * 0.017453292F);
		float vectorX = var15 * var16;
		float vectorZ = var14 * var16;
		double reachLength = 5.0D;
		Vec3d testVectorFar = testVector.addVector(vectorX * reachLength, vectorY * reachLength, vectorZ * reachLength);
		RayTraceResult testHitPosition = world.rayTraceBlocks(testVector, testVectorFar, true);

        /*
		 * if nothing was hit, return null
         */
		if (testHitPosition == null) {
			return null;
		}

		Vec3d var25 = player.getLook(1.0F);
		float var27 = 1.0F;
		List<Entity> entitiesPossiblyHitByVector = world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().expand(var25.x * reachLength, var25.y * reachLength, var25.z * reachLength).expand(var27, var27, var27));
		for (Entity testEntity : entitiesPossiblyHitByVector) {
			if (testEntity.canBeCollidedWith()) {
				float bbExpansionSize = testEntity.getCollisionBorderSize();
				AxisAlignedBB entityBB = testEntity.getEntityBoundingBox().expand(bbExpansionSize, bbExpansionSize, bbExpansionSize);
				/*
				 * if an entity is hit, return its position
                 */
				if (entityBB.contains(testVector)) {
					return new BlockPos(testEntity.posX, testEntity.posY, testEntity.posZ);
				}
			}
		}
		/*
		 * if no entity was hit, return the position impacted.
         */
		return offset ? testHitPosition.getBlockPos().offset(testHitPosition.sideHit) : testHitPosition.getBlockPos();
	}

	public static BlockPos rotateAroundOrigin(BlockPos pos, int turns) {
		for (int i = 0; i < turns; i++) {
			pos = rotateAroundOrigin(pos);
		}
		return pos;
	}

	/*
	 * rotate a position around its origin (0,0,0), in 90' clockwise steps
	 */
	private static BlockPos rotateAroundOrigin(BlockPos pos) {
		return new BlockPos(-pos.getZ(), pos.getY(), pos.getX());
	}

	/*
	 * checks to see if TEST lies somewhere in the cube bounded by pos1 and pos2
	 *
	 * @return true if it does
	 */
	public static boolean isPositionWithinBounds(BlockPos test, BlockPos pos1, BlockPos pos2) {
		if (test.getX() >= pos1.getX() && test.getX() <= pos2.getX()) {
			if (test.getY() >= pos1.getY() && test.getY() <= pos2.getY()) {
				return test.getZ() >= pos1.getZ() && test.getZ() <= pos2.getZ();
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/*
	 * return a new BlockPos containing the minimum coordinates from the two passed in BlockPos
	 */
	public static BlockPos getMin(BlockPos pos1, BlockPos pos2) {
		return new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
	}

	/*
	 * return a new BlockPos containing the maximum coordinates from the two passed in BlockPos
	 */
	public static BlockPos getMax(BlockPos pos1, BlockPos pos2) {
		return new BlockPos(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
	}

	/*
	 * rotates a given block-position in a given area by the number of turns.  Used by templates
	 * to get a relative position.
	 */
	public static BlockPos rotateInArea(BlockPos pos, int xSize, int zSize, int turns) {
		int xSize1 = xSize;
		int zSize1 = zSize;
		int x = pos.getX();
		int z = pos.getZ();
		if (x >= xSize) {
			x = 0;
		}
		if (z >= zSize) {
			z = 0;
		}
		int x1 = x;
		int z1 = z;
		for (int i = 0; i < turns; i++) {
			x = zSize - 1 - z1;
			z = x1;
			x1 = x;
			z1 = z;
			xSize = zSize1;
			zSize = xSize1;
			xSize1 = xSize;
			zSize1 = zSize;
		}
		return new BlockPos(x, pos.getY(), z);
	}

	public static boolean breakBlockAndDrop(World world, BlockPos pos) {
		return breakBlock(world, pos, 0, true);
	}

	public static boolean breakBlock(World world, BlockPos pos, int fortune, boolean doDrop) {
		if (world.isRemote) {
			return false;
		}
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (world.isAirBlock(pos) || state.getBlockHardness(world, pos) < 0) {
			return false;
		}
		if (doDrop) {
			if (!canBreakBlock(world, pos, state)) {
				return false;
			}
			block.dropBlockAsItem(world, pos, state, fortune);
		}
		return world.setBlockToAir(pos);
	}

	private static boolean canBreakBlock(World world, BlockPos pos, IBlockState state) {
		return !AWCoreStatics.fireBlockBreakEvents || !MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(world, pos, state, AWFakePlayer.get(world)));
	}

	public static boolean breakBlockNoDrops(World world, BlockPos pos, IBlockState state) {
		if (!BlockTools.canBreakBlock(world, pos, state) || !world.setBlockToAir(pos)) {
			return false;
		}
		world.playEvent(2001, pos, Block.getStateId(state));

		return true;
	}

	public static boolean placeItemBlockRightClick(ItemStack stack, World world, BlockPos pos) {
		EntityPlayer owner = AWFakePlayer.get(world);
		owner.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
		owner.setHeldItem(EnumHand.MAIN_HAND, stack);
		owner.rotationPitch = 90F % 360F;

		return stack.useItemRightClick(world, owner, EnumHand.MAIN_HAND).getType() == EnumActionResult.SUCCESS;
	}

	public static boolean placeItemBlock(ItemStack stack, World world, BlockPos pos, EnumFacing face) {
		EnumFacing direction = face.getOpposite();

		EntityPlayer owner = AWFakePlayer.get(world);
		owner.setHeldItem(EnumHand.MAIN_HAND, stack);
		return stack.onItemUse(owner, world, pos.offset(direction), EnumHand.MAIN_HAND, face, 0.25F, 0.25F, 0.25F) == EnumActionResult.SUCCESS;
	}

	public static void notifyBlockUpdate(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 3);

	}

	public static void notifyBlockUpdate(TileEntity tile) {
		notifyBlockUpdate(tile.getWorld(), tile.getPos());
	}

	public static JsonElement serializeToJson(IBlockState state) {
		JsonObject serializedState = new JsonObject();
		//noinspection ConstantConditions
		serializedState.addProperty("name", state.getBlock().getRegistryName().toString());

		JsonObject serializedProps = new JsonObject();
		for (Map.Entry<IProperty<?>, Comparable<?>> prop : state.getProperties().entrySet()) {
			serializedProps.addProperty(prop.getKey().getName(), prop.getValue().toString());
		}
		if (!serializedProps.entrySet().isEmpty()) {
			serializedState.add("properties", serializedProps);
		}
		return serializedState;
	}
}
