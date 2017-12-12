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
import net.shadowmage.ancientwarfare.core.interfaces.IEntityPacketHandler;
import net.shadowmage.ancientwarfare.core.interop.ModAccessors;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketEntity;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.gates.types.Gate;
import net.shadowmage.ancientwarfare.structure.gates.types.GateRotatingBridge;

import javax.annotation.Nonnull;
import java.util.UUID;

/*
 * an class to represent ALL gate types
 *
 * @author Shadowmage
 */
public class EntityGate extends Entity implements IEntityAdditionalSpawnData, IEntityPacketHandler {

    public BlockPos pos1;
    public BlockPos pos2;

    public float edgePosition;//the bottom/opening edge of the gate (closed should correspond to pos1)
    public float edgeMax;//the 'fully extended' position of the gate

    public float openingSpeed = 0.f;//calculated speed of the opening gate -- used during animation

    private Gate gateType = Gate.getGateByID(0);

    private String ownerName;
    private int health = 0;
    public int hurtAnimationTicks = 0;
    private byte gateStatus = 0;
    public EnumFacing gateOrientation = EnumFacing.SOUTH;
    public int hurtInvulTicks = 0;

    private boolean hasSetWorldEntityRadius = false;
    public boolean wasPoweredA = false;
    public boolean wasPoweredB = false;
    private UUID ownerId;

    public EntityGate(World par1World) {
        super(par1World);
        this.ignoreFrustumCheck = true;
        this.preventEntitySpawning = true;
    }

    public void setOwner(EntityPlayer player) {
        this.ownerName = player.getName();
        this.ownerId = player.getUniqueID();
    }

    public Team getTeam() {
        return world.getScoreboard().getPlayersTeam(ownerName);
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

    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target){
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

    protected void setOpeningStatus(byte op) {
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
    public int getBrightnessForRender() {
        int i = MathHelper.floor(this.posX);
        int j = MathHelper.floor(this.posZ);
        int k = MathHelper.floor(this.posY);
        if(pos1.getY() > k)
            k = pos1.getY();
        if(pos2.getY() > k)
            k = pos2.getY();
        return this.world.getCombinedLight(new BlockPos(i, k, j), 0);
    }

    @Override
    public void handleStatusUpdate(byte par1) {
        if (world.isRemote) {
            if (par1 == -1 || par1 == 0 || par1 == 1) {
                this.setOpeningStatus(par1);
            }
        }
        super.handleStatusUpdate(par1);
    }

    public boolean isClosed(){
        return gateStatus == 0 && edgePosition == 0;
    }

    public byte getOpeningStatus() {
        return this.gateStatus;
    }

    public int getHealth() {
        return this.health;
    }

    public void setHealth(int val) {
        if (val < 0) {
            val = 0;
        }
        if (val < health) {
            this.hurtAnimationTicks = 20;
        }
        if (val < health && !this.world.isRemote) {
            PacketEntity pkt = new PacketEntity(this);
            pkt.packetData.setInteger("health", val);
            NetworkHandler.sendToAllTracking(this, pkt);
        }
        this.health = val;
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
        
        boolean canInteract = false;
        if (ownerId == null)
            canInteract = true; // neutral/worldgen gates
        if (player.getUniqueID().equals(ownerId))
            canInteract = true; // owned gates
        if (player.getTeam()!=null && player.getTeam().isSameTeam(getTeam()))
            canInteract = true; // same team gates
        if (ModAccessors.FTBU.areTeamMates(player.getUniqueID(), ownerId))
            canInteract = true; // friend gates
        if(canInteract){
            if (player.isSneaking()) {
                NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_GATE_CONTROL, getEntityId(), 0, 0);
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
    public void onUpdate() {
        super.onUpdate();
        this.gateType.onUpdate(this);
        float prevEdge = this.edgePosition;
        this.setPosition(posX, posY, posZ);
        if (this.hurtInvulTicks > 0) {
            this.hurtInvulTicks--;
        }
        this.checkForPowerUpdates();
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

    protected void checkForPowerUpdates() {
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

    private boolean isInsensitiveTo(DamageSource source){
        return source == null || source == DamageSource.ANVIL || source == DamageSource.CACTUS || source == DamageSource.DROWN || source == DamageSource.FALL || source == DamageSource.FALLING_BLOCK || source == DamageSource.IN_WALL || source == DamageSource.STARVE;
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        if(isInsensitiveTo(par1DamageSource) || par2 < 0){
            return false;
        }
        if (this.world.isRemote) {
            return true;
        }
//  if(Config.gatesOnlyDamageByRams)
//    {
//    if(par1DamageSource.getEntity()==null || !(par1DamageSource.getEntity() instanceof VehicleBase))  
//      {
//      return !this.isDead;
//      }
//    VehicleBase vehicle = (VehicleBase) par1DamageSource.getEntity();
//    if(vehicle.vehicleType.getGlobalVehicleType()!=VehicleRegistry.BATTERING_RAM.getGlobalVehicleType())
//      {
//      return !this.isDead;
//      }
//    }
        if(!par1DamageSource.isExplosion()){
            if(this.hurtInvulTicks > 0) {
                return false;
            }
            this.hurtInvulTicks = 10;
        }
        int health = this.getHealth();
        health -= par2;
        this.setHealth(health);

        if (health <= 0) {
            this.setDead();
        }
        return !this.isDead;
    }

//Prevent moving from external means

    @Override
    public Entity changeDimension(int dimension){
        return this;
    }

    @Override
    public boolean handleWaterMovement(){
        return false;
    }

    @Override
    public void moveRelative(float strafe, float up, float forward, float friction) {

    }

    @Override
    public void addVelocity(double moveX, double moveY, double moveZ){

    }

//Interaction, collision handling

    @Override
    public AxisAlignedBB getCollisionBoundingBox(){
        return this.getEntityBoundingBox();
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public float getCollisionBorderSize() {
        return -0.1F;
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    public void applyEntityCollision(Entity entity){
        super.applyEntityCollision(entity);
        if(isInside(entity))
            entity.addVelocity(0, -gateStatus*0.5, 0);
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entity) {
        if(isInside(entity))
            entity.addVelocity(0, -gateStatus*0.5, 0);
    }

    private boolean isInside(Entity entity){
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
        this.pos1 = BlockPos.fromLong(tag.getLong("pos1"));
        this.pos2 = BlockPos.fromLong(tag.getLong("pos2"));
        this.setGateType(Gate.getGateByID(tag.getInteger("type")));
        this.ownerName = tag.getString("owner");
        this.ownerId = tag.getUniqueId("ownerId");
        this.edgePosition = tag.getFloat("edge");
        this.edgeMax = tag.getFloat("edgeMax");
        this.setHealth(tag.getInteger("health"));
        this.gateStatus = tag.getByte("status");
        this.gateOrientation = EnumFacing.VALUES[tag.getByte("orient")];
        this.wasPoweredA = tag.getBoolean("power");
        this.wasPoweredB = tag.getBoolean("power2");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setLong("pos1", pos1.toLong());
        tag.setLong("pos2", pos2.toLong());
        tag.setInteger("type", this.gateType.getGlobalID());
        if (ownerName != null && !ownerName.isEmpty()) {
            tag.setString("owner", ownerName);
        }
        if (ownerId != null) {
            tag.setUniqueId("ownerId", ownerId);
        }
        tag.setFloat("edge", this.edgePosition);
        tag.setFloat("edgeMax", this.edgeMax);
        tag.setInteger("health", this.getHealth());
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
        this.pos1 = BlockPos.fromLong(data.readLong());
        this.pos2 = BlockPos.fromLong(data.readLong());
        this.gateType = Gate.getGateByID(data.readInt());
        this.edgePosition = data.readFloat();
        this.edgeMax = data.readFloat();
        this.gateStatus = data.readByte();
        this.gateOrientation = EnumFacing.VALUES[data.readByte()];
        this.health = data.readInt();
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("health")) {
            this.health = tag.getInteger("health");
            this.hurtAnimationTicks = 20;
        }
    }

}
