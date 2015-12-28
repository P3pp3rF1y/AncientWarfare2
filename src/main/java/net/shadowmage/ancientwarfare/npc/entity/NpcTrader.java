package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.ai.*;
import net.shadowmage.ancientwarfare.npc.ai.owned.*;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.item.ItemTradeOrder;
import net.shadowmage.ancientwarfare.npc.orders.TradeOrder;
import net.shadowmage.ancientwarfare.npc.trade.TradeList;

public class NpcTrader extends NpcPlayerOwned {

    private EntityPlayer trader;//used by guis/containers to prevent further interaction
    private TradeList tradeList = new TradeList();
    private NpcAIPlayerOwnedTrader tradeAI;

    public NpcTrader(World par1World) {
        super(par1World);

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(0, new NpcAIDoor(this, true));
        this.tasks.addTask(0, (horseAI = new NpcAIPlayerOwnedRideHorse(this)));
        this.tasks.addTask(2, new NpcAIFollowPlayer(this));
        this.tasks.addTask(2, new NpcAIPlayerOwnedFollowCommand(this));
        this.tasks.addTask(3, new NpcAIFleeHostiles(this));
        this.tasks.addTask(3, new NpcAIPlayerOwnedAlarmResponse(this));
        this.tasks.addTask(4, tradeAI = new NpcAIPlayerOwnedTrader(this));
        this.tasks.addTask(5, new NpcAIPlayerOwnedGetFood(this));
        this.tasks.addTask(6, new NpcAIPlayerOwnedIdleWhenHungry(this));
        this.tasks.addTask(7, new NpcAIMoveHome(this, 50F, 3F, 30F, 3F));

        //post-100 -- used by delayed shared tasks (look at random stuff, wander)
        this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(102, new NpcAIWander(this));
        this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
    }

    @Override
    public boolean isValidOrdersStack(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemTradeOrder;
    }

    @Override
    public void onOrdersInventoryChanged() {
        tradeList = null;
        if (isValidOrdersStack(ordersStack)) {
            tradeList = TradeOrder.getTradeOrder(ordersStack).getTradeList();
        }
        tradeAI.onOrdersUpdated();
    }

    @Override
    public String getNpcSubType() {
        return "";
    }

    @Override
    public String getNpcType() {
        return "trader";
    }

    @Override
    protected boolean interact(EntityPlayer player) {
        boolean baton = player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemCommandBaton;
        if (baton) {
            return false;
        }
        if (player.worldObj.isRemote) {
            return true;
        }
        if (isOwner(player))//owner
        {
            return tryCommand(player);
        } else//non-owner
        {
            if(trader!=null && !trader.isEntityAlive()){
                closeTrade();
            }
            if (getFoodRemaining() > 0 && trader == null) {
                startTrade(player);
                openAltGui(player);
            }
        }
        return true;
    }

    public void startTrade(EntityPlayer player){
        trader = player;
    }

    public void closeTrade(){
        trader = null;
    }

    @Override
    public void openAltGui(EntityPlayer player) {
        NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_PLAYER_OWNED_TRADE, getEntityId(), 0, 0);
    }

    @Override
    public boolean hasAltGui() {
        return true;
    }

    @Override
    public boolean shouldBeAtHome() {
        return (!worldObj.provider.hasNoSky && !worldObj.isDaytime()) || worldObj.canLightningStrikeAt(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
    }

    @Override
    public boolean isHostileTowards(Entity e) {
        return false;
    }

    public TradeList getTradeList() {
        return tradeList;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setTag("tradeAI", tradeAI.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        tradeAI.readFromNBT(tag.getCompoundTag("tradeAI"));
    }

}
