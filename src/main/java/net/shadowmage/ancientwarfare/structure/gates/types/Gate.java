package net.shadowmage.ancientwarfare.structure.gates.types;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.block.AWStructuresBlocks;
import net.shadowmage.ancientwarfare.structure.entity.DualBoundingBox;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.gates.IGateType;
import net.shadowmage.ancientwarfare.structure.tile.TEGateProxy;

import java.util.HashMap;

public class Gate implements IGateType {

	private static final Gate[] gateTypes = new Gate[16];

	private static final Gate basicWood = new Gate(0, "_wood_1.png").setName("gateBasicWood").setVariant(Variant.WOOD_BASIC);
	private static final Gate basicIron = new Gate(1, "_iron_1.png").setName("gateBasicIron").setVariant(Variant.IRON_BASIC).setModel(1);

	private static final Gate singleWood = new GateSingle(4, "_wood_1.png").setName("gateSingleWood").setVariant(Variant.WOOD_SINGLE);
	private static final Gate singleIron = new GateSingle(5, "_iron_1.png").setName("gateSingleIron").setVariant(Variant.IRON_SINGLE).setModel(1);

	private static final Gate doubleWood = new GateDouble(8, "_wood_1.png").setName("gateDoubleWood").setVariant(Variant.WOOD_DOUBLE);
	private static final Gate doubleIron = new GateDouble(9, "_iron_1.png").setName("gateDoubleIron").setVariant(Variant.IRON_DOUBLE).setModel(1);

	private static final Gate rotatingBridge = new GateRotatingBridge(12, "_bridge_wood_1.png");

	public static final HashMap<String, Integer> gateIDByName = new HashMap<>();

	static {
		gateIDByName.put("gate.verticalWooden", 0);
		gateIDByName.put("gate.verticalIron", 1);
		gateIDByName.put("gate.singleWood", 4);
		gateIDByName.put("gate.singleIron", 5);
		gateIDByName.put("gate.doubleWood", 8);
		gateIDByName.put("gate.doubleIron", 9);
		gateIDByName.put("gate.drawbridge", 12);
	}

	public Variant getVariant() {
		return variant;
	}

	public enum Variant {
		WOOD_BASIC, IRON_BASIC, WOOD_SINGLE, IRON_SINGLE, WOOD_DOUBLE, IRON_DOUBLE, WOOD_ROTATING
	}

	protected final int globalID;
	protected String displayName = "";
	protected String tooltip = "";
	protected Variant variant;
	protected int maxHealth = 40;
	protected int modelType = 0;

	protected boolean canSoldierInteract = true;

	protected float moveSpeed = 0.5f * 0.05f; ///1/2 block / second

	protected final ItemStack displayStack;

	protected final ResourceLocation textureLocation;

	/*
	 *
	 */
	public Gate(int id, String textureLocation) {
		this.globalID = id;
		this.tooltip = "item.gate." + id + ".tooltip";
		if (id >= 0 && id < gateTypes.length && gateTypes[id] == null) {
			gateTypes[id] = this;
		}
		this.displayStack = new ItemStack(AWItems.gateSpawner, 1, id);
		this.textureLocation = new ResourceLocation("ancientwarfare:textures/model/structure/gate/gate" + textureLocation);
	}

	protected final Gate setName(String name) {
		displayName = name;
		return this;
	}

	protected final Gate setVariant(Variant variant) {
		this.variant = variant;
		return this;
	}

	protected final Gate setModel(int type) {
		modelType = type;
		return this;
	}

	@Override
	public int getGlobalID() {
		return globalID;
	}

	@Override
	public int getModelType() {
		return modelType;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getTooltip() {
		return tooltip;
	}

	@Override
	public ItemStack getConstructingItem() {
		return new ItemStack(AWItems.gateSpawner, 1, this.globalID);
	}

	@Override
	public ItemStack getDisplayStack() {
		return displayStack;
	}

	@Override
	public int getMaxHealth() {
		return maxHealth;
	}

	@Override
	public float getMoveSpeed() {
		return moveSpeed;
	}

	@Override
	public ResourceLocation getTexture() {
		return textureLocation;
	}

	@Override
	public boolean canActivate(EntityGate gate, boolean open) {
		return true;
	}

	@Override
	public boolean canSoldierActivate() {
		return canSoldierInteract;
	}

	public static String getGateNameFor(EntityGate gate) {
		int id = gate.getGateType().getGlobalID();
		return getGateNameFor(id);
	}

	private static String getGateNameFor(int id) {
		int gateID;
		for (String key : gateIDByName.keySet()) {
			gateID = gateIDByName.get(key);
			if (gateID == id) {
				return key;
			}
		}
		return "gate.verticalWooden";
	}

	public static Gate getGateByName(String name) {
		if (gateIDByName.containsKey(name)) {
			return getGateByID(gateIDByName.get(name));
		}
		return basicWood;
	}

	public static Gate getGateByID(int id) {
		if (id >= 0 && id < gateTypes.length) {
			return gateTypes[id];
		}
		return basicWood;
	}

	@Override
	public void onUpdate(EntityGate ent) {

	}

	@Override
	public void setCollisionBoundingBox(EntityGate gate) {
		if (gate.pos1 == null || gate.pos2 == null) {
			return;
		}
		BlockPos min = BlockTools.getMin(gate.pos1, gate.pos2);
		BlockPos max = BlockTools.getMax(gate.pos1, gate.pos2);
		if (!(gate.getEntityBoundingBox() instanceof DualBoundingBox)) {
			try {
				ObfuscationReflectionHelper.setPrivateValue(Entity.class, gate, new DualBoundingBox(min, max), "boundingBox", "field_70121_D");
			}
			catch (Exception ignored) {

			}
		}
		if (gate.edgePosition > 0) {
			gate.setEntityBoundingBox(new AxisAlignedBB(min.getX(), max.getY() + 0.5d, min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1));
		} else {
			gate.setEntityBoundingBox(new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1));
		}
	}

	@Override
	public boolean arePointsValidPair(BlockPos pos1, BlockPos pos2) {
		return pos1.getX() == pos2.getX() || pos1.getZ() == pos2.getZ();
	}

	@Override
	public void setInitialBounds(EntityGate gate, BlockPos pos1, BlockPos pos2) {
		BlockPos min = BlockTools.getMin(pos1, pos2);
		BlockPos max = BlockTools.getMax(pos1, pos2);
		boolean wideOnXAxis = min.getX() != max.getX();
		float width = wideOnXAxis ? max.getX() - min.getX() + 1 : max.getZ() - min.getZ() + 1;
		float xOffset = wideOnXAxis ? width * 0.5f : 0.5f;
		float zOffset = wideOnXAxis ? 0.5f : width * 0.5f;
		gate.pos1 = min;
		gate.pos2 = max;
		gate.edgeMax = max.getY() - min.getY() + 1;
		gate.setPosition(min.getX() + xOffset, min.getY(), min.getZ() + zOffset);
	}

	@Override
	public void onGateStartOpen(EntityGate gate) {
		if (gate.world.isRemote) {
			return;
		}
		BlockPos min = BlockTools.getMin(gate.pos1, gate.pos2);
		BlockPos max = BlockTools.getMax(gate.pos1, gate.pos2);
		removeBetween(gate.world, min, max);
	}

	@Override
	public void onGateFinishOpen(EntityGate gate) {

	}

	@Override
	public void onGateStartClose(EntityGate gate) {

	}

	@Override
	public void onGateFinishClose(EntityGate gate) {
		if (gate.world.isRemote) {
			return;
		}
		BlockPos min = BlockTools.getMin(gate.pos1, gate.pos2);
		BlockPos max = BlockTools.getMax(gate.pos1, gate.pos2);
		placeBetween(gate, min, max);
	}

	public final void removeBetween(World world, BlockPos min, BlockPos max) {
		Block id;
		for (int x = min.getX(); x <= max.getX(); x++) {
			for (int y = min.getY(); y <= max.getY(); y++) {
				for (int z = min.getZ(); z <= max.getZ(); z++) {
					id = world.getBlockState(new BlockPos(x, y, z)).getBlock();
					if (id == AWStructuresBlocks.gateProxy) {
						world.setBlockToAir(new BlockPos(x, y, z));
					}
				}
			}
		}
	}

	public final void placeBetween(EntityGate gate, BlockPos min, BlockPos max) {
		for (int x = min.getX(); x <= max.getX(); x++) {
			for (int y = min.getY(); y <= max.getY(); y++) {
				for (int z = min.getZ(); z <= max.getZ(); z++) {
					BlockPos pos = new BlockPos(x, y, z);
					IBlockState state = gate.world.getBlockState(pos);
					Block block = state.getBlock();
					if (!gate.world.isAirBlock(pos)) {
						block.dropBlockAsItem(gate.world, pos, state, 0);
					}
					if (gate.world.setBlockState(pos, AWStructuresBlocks.gateProxy.getDefaultState())) {
						WorldTools.getTile(gate.world, pos, TEGateProxy.class).ifPresent(t -> t.setOwner(gate));
					}
				}
			}
		}
	}

	/*
	 * @return a fully setup gate, or null if chosen spawn position is invalid (blocks in the way)
	 */
	public static EntityGate constructGate(World world, BlockPos pos1, BlockPos pos2, Gate type, EnumFacing facing) {
		BlockPos min = BlockTools.getMin(pos1, pos2);
		BlockPos max = BlockTools.getMax(pos1, pos2);
		for (int x = min.getX(); x <= max.getX(); x++) {
			for (int y = min.getY(); y <= max.getY(); y++) {
				for (int z = min.getZ(); z <= max.getZ(); z++) {
					BlockPos pos = new BlockPos(x, y, z);
					if (!world.isAirBlock(pos)) {
						AWLog.logDebug("could not create gate for non-air block at: " + x + "," + y + "," + z + " block: " + world.getBlockState(pos).getBlock());
						return null;
					}
				}
			}
		}

		if (pos1.getX() == pos2.getX()) {
			if (facing == EnumFacing.SOUTH || facing == EnumFacing.NORTH) {
				facing = facing.rotateY();
			}
		} else if (pos1.getZ() == pos2.getZ()) {
			if (facing == EnumFacing.EAST || facing == EnumFacing.WEST) {
				facing = facing.rotateY();
			}
		}

		EntityGate ent = new EntityGate(world);
		ent.setGateType(type);
		ent.gateOrientation = facing;
		type.setInitialBounds(ent, pos1, pos2);
		type.onGateFinishClose(ent);
		return ent;
	}

	public static ItemStack getItemToConstruct(int type) {
		return getGateByID(type).getConstructingItem();
	}

	public static ItemStack getItemToConstruct(String typeName) {
		return getGateByName(typeName).getConstructingItem();
	}
}
