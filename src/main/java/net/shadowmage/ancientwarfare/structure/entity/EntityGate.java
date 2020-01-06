package net.shadowmage.ancientwarfare.structure.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
import net.shadowmage.ancientwarfare.structure.tile.TEGateProxy;

import javax.annotation.Nonnull;
import java.util.Optional;

/*
 * an class to represent ALL gate types
 *
 * @author Shadowmage
 */
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

	private TEGateProxy renderedTile = null;

	public EntityGate(World par1World) {
		super(par1World);
		this.ignoreFrustumCheck = true;
		this.preventEntitySpawning = true;
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
		return this.gateType;
	}

	public void setGateType(Gate type) {
		this.gateType = type;
		setHealth(type.getMaxHealth());
	}

	@Override
	protected void entityInit() {
		//NOOP
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return Gate.getItemToConstruct(this.gateType.getGlobalID());
	}

	public void repackEntity() {
		if (world.isRemote || isDead) {
			return;
		}
		gateType.onGateStartOpen(this);//catch gates that have proxy blocks still in the world
		gateType.onGateStartClose(this);//
		@Nonnull ItemStack item = Gate.getItemToConstruct(this.gateType.getGlobalID());
		EntityItem entity = new EntityItem(world, posX, posY + 0.5d, posZ, item);
		this.world.spawnEntity(entity);
		this.setDead();
	}

	@Override
	public void setDead() {
		super.setDead();
		if (!this.world.isRemote) {
			//catch gates that have proxy blocks still in the world
			gateType.onGateStartOpen(this);
			gateType.onGateStartClose(this);
		}
	}

	private void setOpeningStatus(byte op) {
		this.gateStatus = op;
		if (!this.world.isRemote) {
			this.world.setEntityState(this, op);
		}
		if (op == -1) {
			this.gateType.onGateStartClose(this);
		} else if (op == 1) {
			this.gateType.onGateStartOpen(this);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender() {
		int i = MathHelper.floor(this.posX);
		int j = MathHelper.floor(this.posZ);
		int k = MathHelper.floor(this.posY);
		if (pos1.getY() > k)
			k = pos1.getY();
		if (pos2.getY() > k)
			k = pos2.getY();
		return this.world.getCombinedLight(new BlockPos(i, k, j), 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte par1) {
		if (par1 == -1 || par1 == 0 || par1 == 1) {
			this.setOpeningStatus(par1);
		}
		super.handleStatusUpdate(par1);
	}

	public boolean isClosed() {
		return gateStatus == 0 && edgePosition == 0;
	}

	public byte getOpeningStatus() {
		return this.gateStatus;
	}

	public int getHealth() {
		return this.health;
	}

	public void setHealth(int val) {
		int newHealth = Math.max(val, 0);
		if (newHealth < health) {
			this.hurtAnimationTicks = 20;
		}
		if (newHealth < health && !this.world.isRemote) {
			PacketEntity pkt = new PacketEntity(this);
			pkt.packetData.setInteger(HEALTH_TAG, newHealth);
			NetworkHandler.sendToAllTracking(this, pkt);
		}
		this.health = newHealth;
	}

	@Override
	public void setPosition(double par1, double par3, double par5) {
		this.posX = par1;
		this.posY = par3;
		this.posZ = par5;
		if (this.gateType != null) {
			this.gateType.setCollisionBoundingBox(this);
		}
	}

	@Override
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
		this.setPosition(x, y, z);
		this.setRotation(yaw, pitch);
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if (this.world.isRemote) {
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
				this.activateGate();
			}
			return true;
		} else {
			player.sendMessage(new TextComponentTranslation("guistrings.gate.use_error"));
		}
		return false;
	}

	public void activateGate() {
		if (this.gateStatus == 1 && this.gateType.canActivate(this, false)) {
			this.setOpeningStatus((byte) -1);
		} else if (this.gateStatus == -1 && this.gateType.canActivate(this, true)) {
			this.setOpeningStatus((byte) 1);
		} else if (this.edgePosition == 0 && this.gateType.canActivate(this, true)) {
			this.setOpeningStatus((byte) 1);
		} else if (this.gateType.canActivate(this, false))//gate is already open/opening, set to closing
		{
			this.setOpeningStatus((byte) -1);
		}
	}

	@Override
	@SuppressWarnings("squid:S2696") //World.MAX_ENTITY_RADIUS is static and can only be set this way, also just running in the main thread makes this safe
	public void onUpdate() {
		super.onUpdate();
		float prevEdge = this.edgePosition;
		this.setPosition(posX, posY, posZ);
		if (this.hurtInvulTicks > 0) {
			this.hurtInvulTicks--;
		}
		this.checkForPowerUpdates();
		gateType.setRenderedTileIfNotPresent(this);
		if (this.hurtAnimationTicks > 0) {
			this.hurtAnimationTicks--;
		}
		if (this.gateStatus == 1) {
			this.edgePosition += this.gateType.getMoveSpeed();
			if (this.edgePosition >= this.edgeMax) {
				this.edgePosition = this.edgeMax;
				this.gateStatus = 0;
				this.gateType.onGateFinishOpen(this);
			}
		} else if (this.gateStatus == -1) {
			this.edgePosition -= this.gateType.getMoveSpeed();
			if (this.edgePosition <= 0) {
				this.edgePosition = 0;
				this.gateStatus = 0;
				this.gateType.onGateFinishClose(this);
			}
		}
		this.openingSpeed = prevEdge - this.edgePosition;

		if (!hasSetWorldEntityRadius) {
			hasSetWorldEntityRadius = true;
			BlockPos min = BlockTools.getMin(pos1, pos2);
			BlockPos max = BlockTools.getMax(pos1, pos2);
			int xSize = max.getX() - min.getX() + 1;
			int zSize = max.getZ() - min.getZ() + 1;
			int ySize = max.getY() - min.getY() + 1;
			int largest = xSize > ySize ? xSize : ySize;
			largest = largest > zSize ? largest : zSize;
			largest = (largest / 2) + 1;
			if (World.MAX_ENTITY_RADIUS < largest) {
				World.MAX_ENTITY_RADIUS = largest;
			}
		}

	}

	private void checkForPowerUpdates() {
		if (this.world.isRemote) {
			return;
		}
		boolean activate = false;
		int y = Math.min(pos2.getY(), pos1.getY());
		boolean foundPowerA = this.world.isBlockIndirectlyGettingPowered(new BlockPos(pos1.getX(), y, pos1.getZ())) > 0;
		boolean foundPowerB = this.world.isBlockIndirectlyGettingPowered(new BlockPos(pos2.getX(), y, pos2.getZ())) > 0;
		if (foundPowerA && !wasPoweredA) {
			activate = true;
		}
		if (foundPowerB && !wasPoweredB) {
			activate = true;
		}
		this.wasPoweredA = foundPowerA;
		this.wasPoweredB = foundPowerB;
		if (activate) {
			this.activateGate();
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
		if (this.world.isRemote) {
			return true;
		}
		if (!damageSource.isExplosion()) {
			if (this.hurtInvulTicks > 0) {
				return false;
			}
			this.hurtInvulTicks = 10;
		}
		this.setHealth((int) (getHealth() - amount));

		if (getHealth() <= 0) {
			this.setDead();
		}
		return !this.isDead;
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
		return this.getEntityBoundingBox();
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
		if (isInside(entity))
			entity.addVelocity(0, -gateStatus * 0.5, 0);
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer entity) {
		if (isInside(entity))
			entity.addVelocity(0, -gateStatus * 0.5, 0);
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
		this.setGateType(Gate.getGateByID(tag.getInteger("type")));
		owner = Owner.deserializeFromNBT(tag);
		this.edgePosition = tag.getFloat("edge");
		this.edgeMax = tag.getFloat("edgeMax");
		this.setHealth(tag.getInteger(HEALTH_TAG));
		this.gateStatus = tag.getByte("status");
		this.gateOrientation = EnumFacing.VALUES[tag.getByte("orient")];
		this.wasPoweredA = tag.getBoolean("power");
		this.wasPoweredB = tag.getBoolean("power2");
		gateType.updateRenderBoundingBox(this);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		tag.setLong("pos1", pos1.toLong());
		tag.setLong("pos2", pos2.toLong());
		tag.setInteger("type", this.gateType.getGlobalID());
		getOwner().serializeToNBT(tag);
		tag.setFloat("edge", this.edgePosition);
		tag.setFloat("edgeMax", this.edgeMax);
		tag.setInteger(HEALTH_TAG, this.getHealth());
		tag.setByte("status", this.gateStatus);
		tag.setByte("orient", (byte) gateOrientation.ordinal());
		tag.setBoolean("power", this.wasPoweredA);
		tag.setBoolean("power2", this.wasPoweredB);
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		data.writeLong(pos1.toLong());
		data.writeLong(pos2.toLong());
		data.writeInt(this.gateType.getGlobalID());
		data.writeFloat(this.edgePosition);
		data.writeFloat(this.edgeMax);
		data.writeByte(this.gateStatus);
		data.writeByte(this.gateOrientation.ordinal());
		data.writeInt(health);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		setPositions(BlockPos.fromLong(data.readLong()), BlockPos.fromLong(data.readLong()));
		this.gateType = Gate.getGateByID(data.readInt());
		this.edgePosition = data.readFloat();
		this.edgeMax = data.readFloat();
		this.gateStatus = data.readByte();
		this.gateOrientation = EnumFacing.VALUES[data.readByte()];
		this.health = data.readInt();
		gateType.updateRenderBoundingBox(this);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey(HEALTH_TAG)) {
			this.health = tag.getInteger(HEALTH_TAG);
			this.hurtAnimationTicks = 20;
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

	public Optional<TEGateProxy> getRenderedTile() {
		return Optional.ofNullable(renderedTile);
	}

	public void setRenderedTile(TEGateProxy te) {
		renderedTile = te;
	}
}
