package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDoor;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.trade.FactionTradeList;

public class NpcFactionTrader extends NpcFaction {

	private FactionTradeList tradeList = new FactionTradeList();
	private EntityPlayer trader;
	private boolean noTradesDespawn = false;

	@SuppressWarnings("unused")
	public NpcFactionTrader(World world) {
		super(world);
		addAI();
	}

	@SuppressWarnings("unused")
	public NpcFactionTrader(World world, String factionName) {
		super(world, factionName);
		addAI();
	}

	private void addAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
		tasks.addTask(0, new NpcAIDoor(this, true));
		tasks.addTask(1, new NpcAIFollowPlayer(this));
		tasks.addTask(2, new NpcAIMoveHome(this, 50F, 5F, 30F, 5F));

		tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		tasks.addTask(102, new NpcAIWander(this));
		tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
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
		if (!world.isRemote) {
			if (noTradesDespawn && tradeList.isEmpty()) {
				setDead();
			}
			tradeList.tick(world);
		}
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		boolean baton = !player.getHeldItem(hand).isEmpty() && player.getHeldItem(hand).getItem() instanceof ItemCommandBaton;
		if (!baton && isEntityAlive()) {
			if (!player.world.isRemote && trader == null) {
				startTrade(player);
				NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_FACTION_TRADE_VIEW, getEntityId(), 0, 0);
			}
			return true;
		}
		return false;
	}

	@Override
	public String getNpcType() {
		return "trader";
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
		tradeList.deserializeNBT(tag.getCompoundTag("tradeList"));
		noTradesDespawn = true;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setTag("tradeList", tradeList.serializeNBT());
	}
}
