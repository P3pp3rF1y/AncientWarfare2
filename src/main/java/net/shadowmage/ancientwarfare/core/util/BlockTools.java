package net.shadowmage.ancientwarfare.core.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.BlockLever.EnumOrientation;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.entity.AWFakePlayer;
import net.shadowmage.ancientwarfare.core.util.parsing.PropertyState;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.function.Function;

import static net.minecraft.block.BlockRailBase.EnumRailDirection.*;

public class BlockTools {

	/*
	 * rotate a float X offset (-1<=x<=1) within a block
	 */
	public static float rotateFloatX(float x, float z, int turns) {
		float x1;
		float z1;
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
		float x1;
		float z1;
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
		return isPositionWithinHorizontalBounds(test, pos1, pos2)
				&& test.getY() >= pos1.getY() && test.getY() <= pos2.getY();
	}

	public static boolean isPositionWithinHorizontalBounds(BlockPos test, BlockPos minPos, BlockPos maxPos) {
		return test.getX() >= minPos.getX() && test.getX() <= maxPos.getX()
				&& test.getZ() >= minPos.getZ() && test.getZ() <= maxPos.getZ();
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
		int positiveTurns = turns > 0 ? turns : 4 + turns;
		for (int i = 0; i < positiveTurns; i++) {
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
			serializedProps.addProperty(prop.getKey().getName(), serializeValue(prop.getKey(), prop.getValue()));
		}
		if (!serializedProps.entrySet().isEmpty()) {
			serializedState.add("properties", serializedProps);
		}
		return serializedState;
	}

	private static <T extends Comparable<T>> String serializeValue(IProperty<T> property, Comparable<?> valueString) {
		//noinspection unchecked
		return property.getName((T) valueString);
	}

	public static EnumFacing getHorizontalFacingFromMeta(int meta) {
		return EnumFacing.VALUES[meta].getAxis() != EnumFacing.Axis.Y ? EnumFacing.VALUES[meta] : EnumFacing.NORTH;
	}

	public static <T> T getBlockState(Tuple<String, Map<String, String>> blockProps, Function<Block, T> init, AddPropertyFunction<T> addProperty) {

		String registryName = blockProps.getFirst();
		Map<String, String> properties = blockProps.getSecond();

		Block block = RegistryTools.getBlock(registryName);

		T ret = init.apply(block);
		BlockStateContainer stateContainer = block.getBlockState();

		for (Map.Entry<String, String> prop : properties.entrySet()) {
			IProperty<?> property = stateContainer.getProperty(prop.getKey());

			if (property == null) {
				//noinspection ConstantConditions
				throw new MissingResourceException("Block \"" + block.getRegistryName().toString() + "\" doesn't have \"" + prop.getKey() + "\" property",
						IProperty.class.getName(), prop.getKey());
			}

			ret = addProperty.apply(ret, property, getPropertyState(block, stateContainer, prop.getKey(), prop.getValue()).getValue());
		}

		return ret;
	}

	public static <T extends Comparable<T>> IBlockState updateProperty(IBlockState state, IProperty<T> property, Comparable<?> value) {
		//noinspection unchecked
		return state.withProperty(property, (T) value);
	}

	private static <T extends Comparable<T>> Optional<T> getValueHelper(IProperty<T> property, String valueString) {
		return toJUtilOptional(property.parseValue(valueString));
	}

	@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "Guava"})
	private static <T> Optional<T> toJUtilOptional(com.google.common.base.Optional<T> optional) {
		return optional.transform(Optional::of).or(Optional::empty);
	}

	public static IBlockState rotateFacing(IBlockState state, int turns) {
		for (Map.Entry<IProperty<?>, Comparable<?>> property : state.getProperties().entrySet()) {
			Class<?> valueClass = property.getKey().getValueClass();
			if (ROTATORS.containsKey(valueClass)) {
				//noinspection unchecked
				state = rotateY(state, property.getKey(), turns);
			}
		}

		return state;
	}

	private static <T extends Comparable<T>> IBlockState rotateY(IBlockState state, IProperty<T> property, int turns) {
		//noinspection unchecked
		return state.withProperty(property, ((IRotator<T>) ROTATORS.get(property.getValueClass())).rotateY(state.getValue(property), turns));
	}

	@SuppressWarnings({"Convert2Lambda", "squid:S1604"})
	private static final Map<Class, IRotator<?>> ROTATORS = new ImmutableMap.Builder<Class, IRotator<?>>()
			.put(EnumFacing.class, new IRotator<EnumFacing>() {
				@Override
				public EnumFacing rotateY(EnumFacing facing, int turns) {
					if (facing.getAxis() == EnumFacing.Axis.Y) {
						return facing;
					}
					for (int i = 0; i < turns; i++) {
						facing = facing.rotateY();
					}

					return facing;
				}
			})
			.put(EnumOrientation.class, new IRotator<EnumOrientation>() {
				@Override
				public EnumOrientation rotateY(EnumOrientation orientation, int turns) {
					for (int i = 0; i < turns; i++) {
						orientation = rotateYLeverOrientation(orientation);
					}
					return orientation;
				}

				@SuppressWarnings("SuspiciousNameCombination")
				private EnumOrientation rotateYLeverOrientation(EnumOrientation orientation) {
					switch (orientation) {
						case UP_X:
							return EnumOrientation.UP_Z;
						case UP_Z:
							return EnumOrientation.UP_X;
						case DOWN_X:
							return EnumOrientation.DOWN_Z;
						case DOWN_Z:
							return EnumOrientation.DOWN_X;
						case NORTH:
							return EnumOrientation.EAST;
						case EAST:
							return EnumOrientation.SOUTH;
						case SOUTH:
							return EnumOrientation.WEST;
						case WEST:
							return EnumOrientation.NORTH;
					}

					return orientation;
				}
			})
			.put(BlockLog.EnumAxis.class, new IRotator<BlockLog.EnumAxis>() {
				@Override
				public BlockLog.EnumAxis rotateY(BlockLog.EnumAxis facing, int turns) {
					if (facing == BlockLog.EnumAxis.Y || facing == BlockLog.EnumAxis.NONE || turns % 2 == 0) {
						return facing;
					}
					return facing == BlockLog.EnumAxis.X ? BlockLog.EnumAxis.Z : BlockLog.EnumAxis.X;
				}
			})
			.put(EnumFacing.Axis.class, new IRotator<EnumFacing.Axis>() {
				@Override
				public EnumFacing.Axis rotateY(EnumFacing.Axis facing, int turns) {
					if (facing == EnumFacing.Axis.Y || turns % 2 == 0) {
						return facing;
					}
					return facing == EnumFacing.Axis.X ? EnumFacing.Axis.Z : EnumFacing.Axis.X;
				}
			})
			.put(EnumRailDirection.class, new IRotator<EnumRailDirection>() {
				@Override
				public EnumRailDirection rotateY(EnumRailDirection facing, int turns) {
					EnumRailDirection rotatedFacing = facing;
					for (int i = 0; i < turns; i++) {
						rotatedFacing = rotateOnce(rotatedFacing);
					}
					return rotatedFacing;
				}

				private EnumRailDirection rotateOnce(EnumRailDirection facing) {
					switch (facing) {
						case NORTH_SOUTH:
							return EAST_WEST;
						case EAST_WEST:
							return NORTH_SOUTH;
						case ASCENDING_EAST:
							return ASCENDING_SOUTH;
						case ASCENDING_SOUTH:
							return ASCENDING_WEST;
						case ASCENDING_WEST:
							return ASCENDING_NORTH;
						case ASCENDING_NORTH:
							return ASCENDING_EAST;
						case SOUTH_EAST:
							return SOUTH_WEST;
						case SOUTH_WEST:
							return NORTH_WEST;
						case NORTH_WEST:
							return NORTH_EAST;
						case NORTH_EAST:
						default:
							return SOUTH_EAST;
					}
				}
			})
			.put(BlockHugeMushroom.EnumType.class, new IRotator<BlockHugeMushroom.EnumType>() {
				@Override
				public BlockHugeMushroom.EnumType rotateY(BlockHugeMushroom.EnumType facing, int turns) {
					BlockHugeMushroom.EnumType rotatedFacing = facing;
					for (int i = 0; i < turns; i++) {
						rotatedFacing = rotateOnce(rotatedFacing);
					}
					return rotatedFacing;
				}

				private BlockHugeMushroom.EnumType rotateOnce(BlockHugeMushroom.EnumType rotatedFacing) {
					switch (rotatedFacing) {
						case NORTH_WEST:
							return BlockHugeMushroom.EnumType.NORTH_EAST;
						case NORTH_EAST:
							return BlockHugeMushroom.EnumType.SOUTH_EAST;
						case SOUTH_EAST:
							return BlockHugeMushroom.EnumType.SOUTH_WEST;
						case SOUTH_WEST:
							return BlockHugeMushroom.EnumType.NORTH_WEST;
						case NORTH:
							return BlockHugeMushroom.EnumType.EAST;
						case EAST:
							return BlockHugeMushroom.EnumType.SOUTH;
						case SOUTH:
							return BlockHugeMushroom.EnumType.WEST;
						case WEST:
							return BlockHugeMushroom.EnumType.NORTH;
						default:
							return rotatedFacing;
					}
				}
			})
			.build();

	public static PropertyState getPropertyState(Block block, BlockStateContainer stateContainer, String propName, String propValue) {
		IProperty<?> property = stateContainer.getProperty(propName);
		if (property == null) {
			//noinspection ConstantConditions
			throw new MissingResourceException("Block \"" + block.getRegistryName().toString() + "\" doesn't have \"" + propName + "\" property",
					IProperty.class.getName(), propName);
		}

		return getPropertyState(property, propName, propValue);
	}

	private static <T extends Comparable<T>, V extends T> PropertyState<T, V> getPropertyState(IProperty<T> property, String propName, String propValue) {
		Optional<?> value = getValueHelper(property, propValue);
		if (!value.isPresent()) {
			throw new MissingResourceException("Invalid value \"" + propValue + "\" for property \"" + propName + "\"", IProperty.class.getName(), propName);
		}
		//noinspection unchecked
		return new PropertyState<>(property, (V) value.get());
	}

	public static int getTopFilledHeight(World world, int x, int z, boolean skippables, int maxY) {
		return getTopFilledHeight(world.getChunkFromChunkCoords(x >> 4, z >> 4), x, z, skippables, maxY);
	}

	public static int getTopFilledHeight(World world, int x, int z, boolean skippables) {
		return getTopFilledHeight(world.getChunkFromChunkCoords(x >> 4, z >> 4), x, z, skippables);
	}

	public static int getTopFilledHeight(Chunk chunk, int x, int z, boolean skippables) {
		return getTopFilledHeight(chunk, x, z, skippables, chunk.getTopFilledSegment() + 16);
	}

	private static int getTopFilledHeight(Chunk chunk, int x, int z, boolean skippables, int maxY) {
		for (int y = maxY; y > 0; y--) {
			IBlockState state = chunk.getBlockState(new BlockPos(x, y, z));
			Block block = state.getBlock();
			if (block == Blocks.AIR || (skippables && AWStructureStatics.isSkippable(state))) {
				continue;
			}
			return y;
		}
		return -1;
	}

	private interface IRotator<T extends Comparable<T>> {

		T rotateY(T facing, int turns);
	}

	public interface AddPropertyFunction<T> {
		T apply(T obj, IProperty<?> property, Comparable<?> value);
	}

	public static Iterable<BlockPos> getAllInBoxTopDown(BlockPos from, BlockPos to) {
		return getAllInBoxTopDown(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()), Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
	}

	public static Iterable<BlockPos> getAllInBoxTopDown(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
		return () -> new AbstractIterator<BlockPos>() {
			private boolean first = true;
			private int lastPosX;
			private int lastPosY;
			private int lastPosZ;

			protected BlockPos computeNext() {
				if (this.first) {
					this.first = false;
					this.lastPosX = x1;
					this.lastPosY = y2;
					this.lastPosZ = z1;
					return new BlockPos(x1, y2, z1);
				} else if (this.lastPosX == x2 && this.lastPosY == y1 && this.lastPosZ == z2) {
					return this.endOfData();
				} else {
					if (this.lastPosX < x2) {
						++this.lastPosX;
					} else if (this.lastPosZ < z2) {
						this.lastPosX = x1;
						++this.lastPosZ;
					} else if (this.lastPosY > y1) {
						this.lastPosX = x1;
						this.lastPosZ = z1;
						--this.lastPosY;
					}

					return new BlockPos(this.lastPosX, this.lastPosY, this.lastPosZ);
				}
			}
		};
	}
}
