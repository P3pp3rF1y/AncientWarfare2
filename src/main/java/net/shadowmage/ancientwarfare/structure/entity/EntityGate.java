package net.shadowmage.ancientwarfare.structure.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.interfaces.IEntityPacketHandler;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketEntity;
import net.shadowmage.ancientwarfare.core.owner.Owner;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.gates.types.Gate;
import net.shadowmage.ancientwarfare.structure.gates.types.GateRotatingBridge;

import javax.annotation.Nonnull;
import java.util.Optional;

/*
 * an class to represent ALL gate types
 *
 * @author Shadowmage
 */
@SuppressWarnings("squid:S2160") // no reason to override equals because the default implementation comparing entityId is enough
public class EntityGate extends Entity implements IEntityAdditionalSpawnData, IEntityPacketHandler {
	private static final String HEALTH_TAG = "health";
	public BlockPos pos1;
	public BlockPos pos2;

	public float edgePosition;//the bottom/opening edge of the gate (closed should correspond to pos1)
	public float edgeMax;//the 'fully extended' position of the gate

	public float openingSpeed = 0.f;//calculated speed of the opening gate -- used during animation

	public Gate gateType = Gate.getGateByID(0);

	private Owner owner = Owner.EMPTY;
	private int health = 0;
	public int hurtAnimationTicks = 0;
	private byte gateStatus = 0;
	public EnumFacing gateOrientation = EnumFacing.SOUTH;
	private int hurtInvulTicks = 0;

	private boolean hasSetWorldEntityRadius = false;
	private boolean wasPoweredA = false;
	private boolean wasPoweredB = false;
	private AxisAlignedBB renderBoundingBox = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

	private BlockPos renderedTilePos = null;
	private int soundTicks = 15;

	public EntityGate(World par1World) {
		super(par1World);
		ignoreFrustumCheck = true;
		preventEntitySpawning = true;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public void setOwner(EntityPlayer player) {
		owner = new Owner(player);
	}

	public void setPositions(BlockPos pos1, BlockPos pos2) {
		this.pos1 = pos1;
		this.pos2 = pos2;
	}

	@Override
	public Team getTeam() {
		return world.getScoreboard().getPlayersTeam(getOwner().getName());
	}

	public Gate getGateType() {
		return gateType;
	}

	public void setGateType(Gate type) {
		gateType = type;
		setHealth(type.getMaxHealth());
	}

	@Override
	protected void entityInit() {
		//NOOP
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return Gate.getItemToConstruct(gateType.getGlobalID());
	}

	public void repackEntity() {
		if (world.isRemote || isDead) {
			return;
		}
		gateType.onGateStartClose(this);//
		@Nonnull ItemStack item = Gate.getItemToConstruct(gateType.getGlobalID());
		EntityItem entity = new EntityItem(world, posX, posY + 0.5d, posZ, item);
		world.spawnEntity(entity);
		setDead();
	}

	@Override
	public void setDead() {
		super.setDead();
		if (!world.isRemote) {
			//catch gates that have proxy blocks still in the world
			gateType.onGateStartClose(this);
			playSound(gateType.breakSound, 1, 1);
		}
	}

	private void setOpeningStatus(byte op) {
		gateStatus = op;
		if (!world.isRemote) {
			world.setEntityState(this, op);
		}
		if (op == -1) {
			gateType.onGateStartClose(this);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender() {
		int i = MathHelper.floor(posX);
		int j = MathHelper.floor(posZ);
		int k = MathHelper.floor(posY);
		if (pos1.getY() > k) {
			k = pos1.getY();
		}
		if (pos2.getY() > k) {
			k = pos2.getY();
		}
		return world.getCombinedLight(new BlockPos(i, k, j), 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte par1) {
		if (par1 == -1 || par1 == 0 || par1 == 1) {
			setOpeningStatus(par1);
		}
		super.handleStatusUpdate(par1);
	}

	public boolean isClosed() {
		return gateStatus == 0 && edgePosition == 0;
	}

	public byte getOpeningStatus() {
		return gateStatus;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int val) {
		int newHealth = Math.max(val, 0);
		if (newHealth < health) {
			hurtAnimationTicks = 20;
		}
		if (newHealth < health && !world.isRemote) {
			PacketEntity pkt = new PacketEntity(this);
			pkt.packetData.setInteger(HEALTH_TAG, newHealth);
			NetworkHandler.sendToAllTracking(this, pkt);
		}
		health = newHealth;
	}

	@Override
	public void setPosition(double par1, double par3, double par5) {
		posX = par1;
		posY = par3;
		posZ = par5;
		if (gateType != null) {
			gateType.setCollisionBoundingBox(this);
		}
	}

	@Override
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
		setPosition(x, y, z);
		setRotation(yaw, pitch);
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if (world.isRemote) {
			return true;
		}

		boolean gateOwner = getOwner().isOwnerOrSameTeamOrFriend(player);
		if (player.capabilities.isCreativeMode || getOwner() == Owner.EMPTY || gateOwner) {
			if (player.isSneaking()) {
				if (player.capabilities.isCreativeMode) {
					NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_GATE_CONTROL_CREATIVE, getEntityId(), 0, 0);
				} else if (gateOwner) {
					NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_GATE_CONTROL, getEntityId(), 0, 0);
				}
			} else {
				activateGate();
			}
			return true;
		} else {
			player.sendMessage(new TextComponentTranslation("guistrings.gate.use_error"));
		}
		return false;
	}

	public void activateGate() {
		if (gateStatus == 1 && gateType.canActivate(this, false)) {
			setOpeningStatus((byte) -1);
		} else if (gateStatus == -1 && gateType.canActivate(this, true)) {
			setOpeningStatus((byte) 1);
		} else if (edgePosition == 0 && gateType.canActivate(this, true)) {
			setOpeningStatus((byte) 1);
		} else if (gateType.canActivate(this, false))//gate is already open/opening, set to closing
		{
			setOpeningStatus((byte) -1);
		}
	}

	private void playGateMoveSound(BlockPos pos, Gate gateType) {
		world.playSound(null, pos, gateType.moveSound, SoundCategory.AMBIENT, 2, 1);
		soundTicks = 0;
	}

	@Override
	@SuppressWarnings("squid:S2696") //World.MAX_ENTITY_RADIUS is static and can only be set this way, also just running in the main thread makes this safe
	public void onUpdate() {
		super.onUpdate();
		float prevEdge = edgePosition;
		setPosition(posX, posY, posZ);
		if (hurtInvulTicks > 0) {
			hurtInvulTicks--;
		}
		checkForPowerUpdates();
		gateType.setRenderedTileIfNotPresent(this);
		if (hurtAnimationTicks > 0) {
			hurtAnimationTicks--;
		}
		if (gateStatus == 1) {
			edgePosition += gateType.getMoveSpeed();
			if (soundTicks == 15) {
				playGateMoveSound(pos1, gateType);
			}
			soundTicks++;
			if (edgePosition >= edgeMax) {
				edgePosition = edgeMax;
				gateStatus = 0;
				soundTicks = 15;
				gateType.onGateFinishOpen(this);
			}
		} else if (gateStatus == -1) {
			edgePosition -= gateType.getMoveSpeed();
			if (soundTicks == 15) {
				playGateMoveSound(pos1, gateType);
			}
			soundTicks++;
			if (edgePosition <= 0) {
				edgePosition = 0;
				gateStatus = 0;
				soundTicks = 15;
				gateType.onGateFinishClose(this);
			}
		}
		openingSpeed = prevEdge - edgePosition;

		if (!hasSetWorldEntityRadius) {
			hasSetWorldEntityRadius = true;
			BlockPos min = BlockTools.getMin(pos1, pos2);
			BlockPos max = BlockTools.getMax(pos1, pos2);
			int xSize = max.getX() - min.getX() + 1;
			int zSize = max.getZ() - min.getZ() + 1;
			int ySize = max.getY() - min.getY() + 1;
			int largest = Math.max(xSize, ySize);
			largest = Math.max(largest, zSize);
			largest = (largest / 2) + 1;
			if (World.MAX_ENTITY_RADIUS < largest) {
				World.MAX_ENTITY_RADIUS = largest;
			}
		}

	}

	private void checkForPowerUpdates() {
		if (world.isRemote) {
			return;
		}
		boolean activate = false;
		int y = Math.min(pos2.getY(), pos1.getY());
		boolean foundPowerA = world.isBlockIndirectlyGettingPowered(new BlockPos(pos1.getX(), y, pos1.getZ())) > 0;
		boolean foundPowerB = world.isBlockIndirectlyGettingPowered(new BlockPos(pos2.getX(), y, pos2.getZ())) > 0;
		if (foundPowerA && !wasPoweredA) {
			activate = true;
		}
		if (foundPowerB && !wasPoweredB) {
			activate = true;
		}
		wasPoweredA = foundPowerA;
		wasPoweredB = foundPowerB;
		if (activate) {
			activateGate();
		}
	}

	private boolean isInsensitiveTo(DamageSource source) {
		return source == DamageSource.ANVIL || source == DamageSource.CACTUS || source == DamageSource.DROWN || source == DamageSource.FALL || source == DamageSource.FALLING_BLOCK || source == DamageSource.IN_WALL || source == DamageSource.STARVE;
	}

	@Override
	public boolean attackEntityFrom(DamageSource damageSource, float amount) {
		if (isInsensitiveTo(damageSource) || amount < 0) {
			return false;
		}
		if (world.isRemote) {
			return true;
		}
		if (!damageSource.isExplosion()) {
			if (hurtInvulTicks > 0) {
				return false;
			}
			hurtInvulTicks = 10;
		} else {
			amount *= 10;
		}

		playSound(gateType.hurtSound, 1, 1);
		if (damageSource.getImmediateSource() instanceof EntityArrow) { // ignore arrow dmg
			return false;
		}
		if (damageSource.getImmediateSource() instanceof EntityLivingBase) {
			/*  getTrueSource is no good here because that would make it work for ranged attacks like arrows and vehicles too,
			we only want to reduce melee damage */
			EntityLivingBase entitylivingbase = (EntityLivingBase) damageSource.getImmediateSource();
			if ((!entitylivingbase.getHeldItem(EnumHand.MAIN_HAND).isEmpty())) {
				Item heldItem = entitylivingbase.getHeldItem(EnumHand.MAIN_HAND).getItem();
				if (gateType.isWood(gateType.getVariant())) { // wooden gates
					amount = (heldItem instanceof ItemAxe) ? amount / 2 : amount / 4; // half dmg for axe, 1/4 for anything else

				} else { // iron gates
					if ((heldItem instanceof ItemPickaxe)) {
						Item.ToolMaterial material = Item.ToolMaterial.valueOf(((ItemPickaxe) heldItem).getToolMaterialName());
						amount = material == Item.ToolMaterial.DIAMOND ? amount / 2 : amount / 3; // half dmg for diamond pickaxe, 1/3 for any other axes
					} else { // anything but a pickaxe
						amount = amount / 4;
					}
				}
			}
		}
		setHealth((int) (getHealth() - amount));

		if (getHealth() <= 0) {
			setDead();
		}
		return !isDead;
	}

	@Override
	public Entity changeDimension(int dimension) {
		return this;
	}

	@Override
	public boolean handleWaterMovement() {
		return false;
	}

	@Override
	public void moveRelative(float strafe, float up, float forward, float friction) {
		//NOOP
	}

	@Override
	public void addVelocity(double moveX, double moveY, double moveZ) {
		//NOOP
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return getEntityBoundingBox();
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public float getCollisionBorderSize() {
		return 0.1F;
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	public void applyEntityCollision(Entity entity) {
		super.applyEntityCollision(entity);
		if (isInside(entity)) {
			entity.addVelocity(0, -gateStatus * 0.5, 0);
		}
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer entity) {
		if (isInside(entity)) {
			entity.addVelocity(0, -gateStatus * 0.5, 0);
		}
	}

	private boolean isInside(Entity entity) {
		return gateType instanceof GateRotatingBridge && getEntityBoundingBox().intersects(entity.getEntityBoundingBox());
	}

	@Override
	public boolean startRiding(Entity entityIn, boolean force) {
		return false;
	}

	//Rendering
	public String getTexture() {
		return "textures/models/gate/" + gateType.getTexture();
	}

	//Data
	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		setPositions(BlockPos.fromLong(tag.getLong("pos1")), BlockPos.fromLong(tag.getLong("pos2")));
		setGateType(Gate.getGateByID(tag.getInteger("type")));
		owner = Owner.deserializeFromNBT(tag);
		edgePosition = tag.getFloat("edge");
		edgeMax = tag.getFloat("edgeMax");
		setHealth(tag.getInteger(HEALTH_TAG));
		gateStatus = tag.getByte("status");
		gateOrientation = EnumFacing.VALUES[tag.getByte("orient")];
		wasPoweredA = tag.getBoolean("power");
		wasPoweredB = tag.getBoolean("power2");
		gateType.updateRenderBoundingBox(this);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		tag.setLong("pos1", pos1.toLong());
		tag.setLong("pos2", pos2.toLong());
		tag.setInteger("type", gateType.getGlobalID());
		getOwner().serializeToNBT(tag);
		tag.setFloat("edge", edgePosition);
		tag.setFloat("edgeMax", edgeMax);
		tag.setInteger(HEALTH_TAG, getHealth());
		tag.setByte("status", gateStatus);
		tag.setByte("orient", (byte) gateOrientation.ordinal());
		tag.setBoolean("power", wasPoweredA);
		tag.setBoolean("power2", wasPoweredB);
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		data.writeLong(pos1.toLong());
		data.writeLong(pos2.toLong());
		data.writeInt(gateType.getGlobalID());
		data.writeFloat(edgePosition);
		data.writeFloat(edgeMax);
		data.writeByte(gateStatus);
		data.writeByte(gateOrientation.ordinal());
		data.writeInt(health);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		setPositions(BlockPos.fromLong(data.readLong()), BlockPos.fromLong(data.readLong()));
		gateType = Gate.getGateByID(data.readInt());
		edgePosition = data.readFloat();
		edgeMax = data.readFloat();
		gateStatus = data.readByte();
		gateOrientation = EnumFacing.VALUES[data.readByte()];
		health = data.readInt();
		gateType.updateRenderBoundingBox(this);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey(HEALTH_TAG)) {
			health = tag.getInteger(HEALTH_TAG);
			hurtAnimationTicks = 20;
		}
	}

	public Owner getOwner() {
		return owner;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return renderBoundingBox;
	}

	public void setRenderBoundingBox(AxisAlignedBB renderBoundingBox) {
		this.renderBoundingBox = renderBoundingBox;
	}

	public Optional<BlockPos> getRenderedTilePos() {
		return Optional.ofNullable(renderedTilePos);
	}

	public void setRenderedTilePos(BlockPos pos) {
		renderedTilePos = pos;
	}
}
