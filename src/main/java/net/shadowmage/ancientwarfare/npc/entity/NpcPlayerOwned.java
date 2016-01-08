package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedRideHorse;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;
import net.shadowmage.ancientwarfare.npc.orders.UpkeepOrder;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

import java.util.List;

public abstract class NpcPlayerOwned extends NpcBase implements IKeepFood{

    public boolean isAlarmed = false;
    
    private Command playerIssuedCommand;//TODO load/save
    private int foodValueRemaining = 0;

    protected NpcAIPlayerOwnedRideHorse horseAI;

    private BlockPosition townHallPosition;
    private BlockPosition upkeepAutoBlock;

    public NpcPlayerOwned(World par1World) {
        super(par1World);
    }

    @Override
    public final boolean canPickUpLoot(){
        return true;
    }

    @Override
    public final int getMaxSafePointTries() {
        return super.getMaxSafePointTries() - 1;
    }

    @Override
    public void onDeath(DamageSource source) {
        if (!worldObj.isRemote) {
            if (horseAI != null) {
                horseAI.onKilled();
            }
            validateTownHallPosition();
            TileTownHall townHall = getTownHall();
            if (townHall != null) {
                townHall.handleNpcDeath(this, source);
            }
        }
        super.onDeath(source);
    }

    @Override
    public final int getArmorValueOverride() {
        return -1;
    }

    @Override
    public final int getAttackDamageOverride() {
        return -1;
    }

    public void setTownHallPosition(BlockPosition pos) {
        if (pos != null) {
            this.townHallPosition = pos.copy();
        } else {
            this.townHallPosition = null;
        }
    }

    @Override
    public BlockPosition getTownHallPosition() {
        return townHallPosition;
    }

    public TileTownHall getTownHall() {
        BlockPosition pos = getTownHallPosition();
        if (pos != null) {
            TileEntity te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
            if (te instanceof TileTownHall) {
                return (TileTownHall) te;
            }
        }
        return null;
    }

    public void handleTownHallBroadcast(TileTownHall tile, BlockPosition position) {
        validateTownHallPosition();
        BlockPosition pos = getTownHallPosition();
        if (pos != null) {
            double curDist = getDistanceSq(pos.x + 0.5d, pos.y, pos.z + 0.5d);
            double newDist = getDistanceSq(position.x + 0.5d, position.y, position.z + 0.5d);
            if (newDist < curDist) {
                setTownHallPosition(position);
                if (upkeepAutoBlock == null || upkeepAutoBlock.equals(pos)) {
                    upkeepAutoBlock = position;
                }
            }
        } else {
            setTownHallPosition(position);
            if (upkeepAutoBlock == null) {
                upkeepAutoBlock = position;
            }
        }
        // (un)set alarmed status
        isAlarmed = getTownHall().alarmActive;
    }

    private boolean validateTownHallPosition() {
        BlockPosition pos = getTownHallPosition();
        if (pos == null) {
            return false;
        }
        if (!worldObj.blockExists(pos.x, pos.y, pos.z)) {
            return true;
        }//cannot validate, unloaded...assume good
        TileEntity te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
        if (te instanceof TileTownHall) {
            if (canBeCommandedBy(((TileTownHall) te).getOwnerName())) {
                return true;
            }
        }
        setTownHallPosition(null);
        return false;
    }

    /**
     * Returns the currently following player-issues command, or null if none
     */
    public Command getCurrentCommand() {
        return playerIssuedCommand;
    }

    /**
     * input path from command baton - default implementation for player-owned NPC is to set current command==input command and then let AI do the rest
     */
    public void handlePlayerCommand(Command cmd) {
        if (cmd != null && cmd.type == CommandType.ATTACK) {
            Entity e = cmd.getEntityTarget(worldObj);
            AWLog.logDebug("Handling attack command : " + e);
            if (e instanceof EntityLivingBase) {
                EntityLivingBase elb = (EntityLivingBase) e;
                if (canTarget(elb))//only attacked allowed targets
                {
                    setAttackTarget(elb);
                }
            }
            cmd = null;
        }
        this.setPlayerCommand(cmd);
    }

    public void setPlayerCommand(Command cmd) {
        this.playerIssuedCommand = cmd;
    }

    @Override
    public boolean isHostileTowards(Entity e) {
        if (e instanceof NpcPlayerOwned) {
            NpcPlayerOwned npc = (NpcPlayerOwned) e;
            Team t = npc.getTeam();
            return t != getTeam();
        } else if (e instanceof NpcFaction) {
            NpcFaction npc = (NpcFaction) e;
            return npc.isHostileTowards(this);//cheap trick to determine if should be hostile or not using the faction-based npcs standing towards this players npcs...handled in NpcFaction
        } else if (e instanceof EntityPlayer) {
            Team t = ((EntityPlayer) e).getTeam();
            return t != getTeam();
        } else {
            String n = EntityList.getEntityString(e);
            List<String> targets = AncientWarfareNPC.statics.getValidTargetsFor(getNpcType(), getNpcSubType());
            if (targets.contains(n)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canTarget(Entity e) {
        if (e instanceof NpcPlayerOwned) {
            Team t = ((NpcPlayerOwned) e).getTeam();
            return t != getTeam();//do not allow npcs to target their own teams npcs
        } else if (e instanceof EntityPlayer) {
            Team t = ((EntityPlayer) e).getTeam();
            return t != getTeam();//do not allow npcs to target their own teams players
        }
        return e instanceof EntityLivingBase;
    }

    @Override
    public boolean canBeAttackedBy(Entity e) {
        if (e instanceof NpcPlayerOwned) {
            return ((NpcPlayerOwned) e).getTeam() != getTeam();//can only be attacked by non-same team -- disable friendly fire and combat amongst neutrals
        }
        return true;
    }

    protected boolean isHostileTowards(Team team) {
        Team a = getTeam();
        return a != null && !a.isSameTeam(team);
    }

    @Override
    public void onWeaponInventoryChanged() {
        updateTexture();
    }

    @Override
    public int getFoodRemaining() {
        return foodValueRemaining;
    }

    @Override
    public void setFoodRemaining(int food) {
        this.foodValueRemaining = food;
    }

    @Override
    public BlockPosition getUpkeepPoint() {
        UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
        if (order != null) {
            return order.getUpkeepPosition();
        }
        return upkeepAutoBlock;
    }

    @Override
    public void setUpkeepAutoPosition(BlockPosition pos) {
        upkeepAutoBlock = pos;
    }

    @Override
    public int getUpkeepBlockSide() {
        UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
        if (order != null) {
            return order.getUpkeepBlockSide();
        }
        return 0;
    }

    @Override
    public int getUpkeepDimensionId() {
        UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
        if (order != null) {
            return order.getUpkeepDimension();
        }
        return worldObj.provider.dimensionId;
    }

    @Override
    public int getUpkeepAmount() {
        UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
        if (order != null) {
            return order.getUpkeepAmount();
        }
        return AWNPCStatics.npcDefaultUpkeepWithdraw;
    }

    @Override
    protected boolean tryCommand(EntityPlayer player) {
        if (this.canBeCommandedBy(player.getCommandSenderName()) && (getTeam() == null || getTeam().isSameTeam(player.getTeam()))) {
            return super.tryCommand(player);
        }
        return false;
    }

    public boolean withdrawFood(IInventory inventory, int side) {
        int amount = getUpkeepAmount() - getFoodRemaining();
        if (amount <= 0) {
            return true;
        }
        ItemStack stack;
        int val;
        int eaten = 0;
        if (side >= 0 && inventory instanceof ISidedInventory) {
            int[] ind = ((ISidedInventory) inventory).getAccessibleSlotsFromSide(side);
            for (int i : ind) {
                stack = inventory.getStackInSlot(i);
                val = AncientWarfareNPC.statics.getFoodValue(stack);
                if (val <= 0) {
                    continue;
                }
                while (eaten < amount && stack.stackSize > 0) {
                    eaten += val;
                    stack.stackSize--;
                    inventory.markDirty();
                }
                if (stack.stackSize <= 0) {
                    inventory.setInventorySlotContents(i, null);
                }
            }
        } else {
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                stack = inventory.getStackInSlot(i);
                val = AncientWarfareNPC.statics.getFoodValue(stack);
                if (val <= 0) {
                    continue;
                }
                while (eaten < amount && stack.stackSize > 0) {
                    eaten += val;
                    stack.stackSize--;
                    inventory.markDirty();
                }
                if (stack.stackSize <= 0) {
                    inventory.setInventorySlotContents(i, null);
                }
            }
        }
        setFoodRemaining(getFoodRemaining() + eaten);
        return getFoodRemaining() >= getUpkeepAmount();
    }

    @Override
    protected boolean interact(EntityPlayer player) {
        if(getFoodRemaining() < getUpkeepAmount()){
            int value = AncientWarfareNPC.statics.getFoodValue(player.getHeldItem());
            if(value>0){
                if(!worldObj.isRemote){
                    player.getHeldItem().stackSize--;
                }
                foodValueRemaining += value;
                return true;
            }
        }
        return super.interact(player);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (foodValueRemaining > 0) {
            foodValueRemaining--;
        }
    }

    @Override
    public void travelToDimension(int par1) {
        this.townHallPosition = null;
        this.upkeepAutoBlock = null;
        super.travelToDimension(par1);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        foodValueRemaining = tag.getInteger("foodValue");
        if (tag.hasKey("command")) {
            playerIssuedCommand = new Command(tag.getCompoundTag("command"));
        }
        if (tag.hasKey("townHall")) {
            townHallPosition = new BlockPosition(tag.getCompoundTag("townHall"));
        }
        if (tag.hasKey("upkeepPos")) {
            upkeepAutoBlock = new BlockPosition(tag.getCompoundTag("upkeepPos"));
        }
        onOrdersInventoryChanged();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setInteger("foodValue", foodValueRemaining);
        if (playerIssuedCommand != null) {
            tag.setTag("command", playerIssuedCommand.writeToNBT(new NBTTagCompound()));
        }
        if (townHallPosition != null) {
            tag.setTag("townHall", townHallPosition.writeToNBT(new NBTTagCompound()));
        }
        if (upkeepAutoBlock != null) {
            tag.setTag("upkeepPos", upkeepAutoBlock.writeToNBT(new NBTTagCompound()));
        }
    }

}
