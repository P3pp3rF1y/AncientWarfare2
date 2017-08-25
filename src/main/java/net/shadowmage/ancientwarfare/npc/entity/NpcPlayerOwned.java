package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
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
    public boolean deathNotifiedTownhall = false;
    
    private Command playerIssuedCommand;//TODO load/save
    private int foodValueRemaining = 0;

    protected NpcAIPlayerOwnedRideHorse horseAI;

    private BlockPos townHallPosition;
    private BlockPos upkeepAutoBlock;

    public NpcPlayerOwned(World par1World) {
        super(par1World);
    }

    @Override
    public final int getMaxSafePointTries() {
        return super.getMaxSafePointTries() - 1;
    }

    @Override
    public void onDeath(DamageSource source) {
        if (!world.isRemote) {
            if (horseAI != null) {
                horseAI.onKilled();
            }
            validateTownHallPosition();
            TileTownHall townHall = getTownHall();
            if (townHall != null) {
                deathNotifiedTownhall = true;
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

    public void setTownHallPosition(BlockPos pos) {
        if (pos != null) {
            this.townHallPosition = pos.copy();
        } else {
            this.townHallPosition = null;
        }
    }

    @Override
    public BlockPos getTownHallPosition() {
        return townHallPosition;
    }

    public TileTownHall getTownHall() {
        BlockPos pos = getTownHallPosition();
        if (pos != null) {
            TileEntity te = world.getTileEntity(pos.x, pos.y, pos.z);
            if (te instanceof TileTownHall) {
                return (TileTownHall) te;
            }
        }
        return null;
    }

    public void handleTownHallBroadcast(TileTownHall tile, BlockPos position) {
        validateTownHallPosition();
        BlockPos pos = getTownHallPosition();
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
        BlockPos pos = getTownHallPosition();
        if (pos == null) {
            return false;
        }
        if (!world.blockExists(pos.x, pos.y, pos.z)) {
            return true;
        }//cannot validate, unloaded...assume good
        TileEntity te = world.getTileEntity(pos.x, pos.y, pos.z);
        if (te instanceof TileTownHall) {
            if (hasCommandPermissions(((TileTownHall) te).getOwnerName()))
                return true;
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
            Entity e = cmd.getEntityTarget(world);
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
    public boolean isHostileTowards(Entity entityTarget) {
        if (NpcAI.isAlwaysHostileToNpcs(entityTarget))
            return true;
        else if ((entityTarget instanceof NpcPlayerOwned) || (entityTarget instanceof EntityPlayer)) {
            return !isEntitySameTeamOrFriends(entityTarget);
        } else if (entityTarget instanceof NpcFaction) {
            return ((NpcFaction) entityTarget).isHostileTowards(this); // hostility is based on faction standing
        } else {
            // TODO
            // This is for forced inclusions, which we don't currently support in new auto-targeting. This 
            // is complicated because reasons. See comments in the AWNPCStatics class for details.
            
            if (!AncientWarfareNPC.statics.autoTargetting) {
                String n = EntityList.getEntityString(entityTarget);
                List<String> targets = AncientWarfareNPC.statics.getValidTargetsFor(getNpcType(), getNpcSubType());
                if (targets.contains(n)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canTarget(Entity e) {
        if (isEntitySameTeamOrFriends(e))
            return false; // don't let npcs target their own teams npcs/players
        return e instanceof EntityLivingBase;
    }

    @Override
    public boolean canBeAttackedBy(Entity e) {
        if (isEntitySameTeamOrFriends(e))
            return false; // can only be attacked by different team - prevent friendly fire and neutral infighting
        return true;
    }

    /*
    protected boolean isHostileTowards(Team team) {
        Team a = getTeam();
        return a != null && !a.isSameTeam(team);
    }
    */

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
    public BlockPos getUpkeepPoint() {
        UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
        if (order != null) {
            return order.getUpkeepPosition();
        }
        return upkeepAutoBlock;
    }

    @Override
    public void setUpkeepAutoPosition(BlockPos pos) {
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
        return world.provider.getDimension();
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
        if (hasCommandPermissions(player.getName()))
            return super.tryCommand(player);
        return false;
    }

    public boolean withdrawFood(IInventory inventory, int side) {
        int amount = getUpkeepAmount() - getFoodRemaining();
        if (amount <= 0) {
            return true;
        }
        @Nonnull ItemStack stack;
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
                while (eaten < amount && !stack.isEmpty()) {
                    eaten += val;
                    stack.shrink(1);
                    inventory.markDirty();
                }
                if (stack.getCount() <= 0) {
                    inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                }
            }
        } else {
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                stack = inventory.getStackInSlot(i);
                val = AncientWarfareNPC.statics.getFoodValue(stack);
                if (val <= 0) {
                    continue;
                }
                while (eaten < amount && !stack.isEmpty()) {
                    eaten += val;
                    stack.shrink(1);
                    inventory.markDirty();
                }
                if (stack.getCount() <= 0) {
                    inventory.setInventorySlotContents(i, ItemStack.EMPTY);
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
                if(!world.isRemote){
                    player.getHeldItem().shrink(1);
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
        if (foodValueRemaining > 0 && !getSleeping()) {
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
            townHallPosition = new BlockPos(tag.getCompoundTag("townHall"));
        }
        if (tag.hasKey("upkeepPos")) {
            upkeepAutoBlock = new BlockPos(tag.getCompoundTag("upkeepPos"));
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
