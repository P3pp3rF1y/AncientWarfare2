package net.shadowmage.ancientwarfare.npc.entity.faction;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;
import net.shadowmage.ancientwarfare.npc.registry.NpcDefault;
import net.shadowmage.ancientwarfare.npc.registry.NpcDefaultsRegistry;

import java.util.UUID;

public abstract class NpcFaction extends NpcBase {
	protected String factionName;

	public NpcFaction(World world) {
		super(world);
	}

	public NpcFaction(World world, String factionName) {
		super(world);
		setFactionNameAndDefaults(factionName);
	}

	public void setFactionNameAndDefaults(String factionName) {
		this.factionName = factionName;
		applyFactionNpcSettings();
	}

	private void applyFactionNpcSettings() {
		NpcDefault npcDefault = NpcDefaultsRegistry.getFactionNpcDefault(this);
		npcDefault.applyAttributes(this);
		experienceValue = npcDefault.getExperienceDrop();
		npcDefault.applyPathSettings((PathNavigateGround) getNavigator());
		npcDefault.applyEquipment(this);
	}

	@Override
	public int getMaxFallHeight() {
		int i = super.getMaxFallHeight();
		if (i > 4)
			i += world.getDifficulty().getDifficultyId() * getMaxHealth() / 5;
		if (i >= getHealth())
			return (int) getHealth();
		return i;
	}

	@Override
	protected boolean tryCommand(EntityPlayer player) {
		return player.capabilities.isCreativeMode && super.tryCommand(player);
	}

	@Override
	public boolean hasCommandPermissions(UUID playerUuid, String playerName) {
		return false;
	}

	@Override
	public String getName() {
		String name = I18n.translateToLocal("entity.ancientwarfarenpc." + getNpcFullType() + ".name");
		if (hasCustomName()) {
			name = name + " : " + getCustomNameTag();
		}
		return name;
	}

	@Override
	public String getNpcFullType() {
		return factionName + "." + super.getNpcFullType();
	}

	@Override
	public boolean isHostileTowards(Entity e) {
		if (e instanceof EntityPlayer) {
			int standing = FactionTracker.INSTANCE.getStandingFor(world, e.getName(), getFaction());
			if (getNpcFullType().endsWith("elite")) {
				standing -= 50;
			}
			return standing < 0;
		} else if (e instanceof NpcPlayerOwned) {
			NpcBase npc = (NpcBase) e;
			int standing = FactionTracker.INSTANCE.getStandingFor(world, npc.getOwner().getName(), getFaction());
			if (getNpcFullType().endsWith("elite")) {
				standing -= 50;
			}
			return standing < 0;
		} else if (e instanceof NpcFaction) {
			NpcFaction npc = (NpcFaction) e;
			return !npc.getFaction().equals(factionName) && FactionRegistry.getFaction(getFaction()).isHostileTowards(npc.getFaction());
		} else {
			return FactionRegistry.getFaction(factionName).isTarget(e);
		}
	}

	@Override
	public boolean canTarget(Entity e) {
		if (e instanceof NpcFaction) {
			return !((NpcFaction) e).getFaction().equals(getFaction());
		}
		return e instanceof EntityLivingBase;
	}

	@Override
	public boolean canBeAttackedBy(Entity e) {
		//can only be attacked by other factions, not your own...disable friendly fire
		return !(e instanceof NpcFaction) || !getFaction().equals(((NpcFaction) e).getFaction());
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		super.onDeath(damageSource);
		if (damageSource.getTrueSource() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) damageSource.getTrueSource();
			FactionTracker.INSTANCE.adjustStandingFor(world, player.getName(), getFaction(), -AWNPCStatics.factionLossOnDeath);
		} else if (damageSource.getTrueSource() instanceof NpcPlayerOwned) {
			String playerName = ((NpcBase) damageSource.getTrueSource()).getOwner().getName();
			FactionTracker.INSTANCE.adjustStandingFor(world, playerName, getFaction(), -AWNPCStatics.factionLossOnDeath);
		}
	}

	@Override
	public String getNpcSubType() {
		return "";
	}

	public String getFaction() {
		return factionName;
	}

	@Override
	public Team getTeam() {
		return null;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		super.writeSpawnData(buffer);
		new PacketBuffer(buffer).writeString(factionName);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		super.readSpawnData(buffer);
		factionName = new PacketBuffer(buffer).readString(20);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		factionName = tag.getString("factionName");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		if (factionName != null) {
			tag.setString("factionName", factionName);
		}
	}
}
