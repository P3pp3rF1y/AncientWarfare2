package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.trade.FactionTradeList;

public abstract class NpcFactionTrader extends NpcFaction {

    private FactionTradeList tradeList = new FactionTradeList();
    private EntityPlayer trader;

    public NpcFactionTrader(World par1World) {
        super(par1World);

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(0, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(1, new NpcAIFollowPlayer(this));
        this.tasks.addTask(2, new NpcAIMoveHome(this, 50F, 5F, 30F, 5F));

        this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(102, new NpcAIWander(this, 0.625D));
        this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
    }

    public FactionTradeList getTradeList() {
        return tradeList;
    }

    public void startTrade(EntityPlayer player) {
        trader = player;
    }

    public void closeTrade() {
        trader = null;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!worldObj.isRemote) {
            tradeList.tick();
        }
    }

    @Override
    protected boolean interact(EntityPlayer player) {
        if (!player.worldObj.isRemote && trader == null) {
            boolean baton = player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemCommandBaton;
            if (!baton) {
                trader = player;
                NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_FACTION_TRADE_VIEW, getEntityId(), 0, 0);
            }
        }
        return false;
    }

    @Override
    public boolean isHostileTowards(Entity e) {
        return false;
    }

    @Override
    public boolean canTarget(Entity e) {
        return false;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        tradeList.readFromNBT(tag.getCompoundTag("tradeList"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setTag("tradeList", tradeList.writeToNBT(new NBTTagCompound()));
    }
}
