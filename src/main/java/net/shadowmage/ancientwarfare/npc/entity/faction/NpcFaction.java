package net.shadowmage.ancientwarfare.npc.entity.faction;

import com.google.common.base.Predicate;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public abstract class NpcFaction extends NpcBase {
	protected String factionName;

	protected final Predicate<Entity> selector = this::isHostileTowards;

	public NpcFaction(World world) {
		super(world);
	}

	public NpcFaction(World world, String factionName) {
		super(world);
		this.factionName = factionName;
		String type = this.getNpcFullType();
		@Nonnull ItemStack eqs;
		for (int i = 0; i < 8; i++) {
			eqs = AncientWarfareNPC.statics.getStartingEquipmentForSlot(type, i);
			if (eqs != null) {
				setItemStackToSlot(i, eqs);
			}
		}
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
	public boolean isHostileTowards(Entity e) {
		if (NpcAI.isAlwaysHostileToNpcs(e))
			return true;
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
			return AncientWarfareNPC.statics.shouldFactionBeHostileTowards(getFaction(), npc.getFaction());
		} else {
			// TODO
			// This is for forced inclusions, which we don't currently support in new auto-targeting. This
			// is complicated because reasons. See comments in the AWNPCStatics class for details.

			if (!AncientWarfareNPC.statics.autoTargetting) {
				List<String> targets = AncientWarfareNPC.statics.getValidTargetsFor(getNpcFullType(), "");
				String t = EntityList.getEntityString(e);
				if (targets.contains(t)) {
					return true;
				}
			}
		}
		return false;
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
		if (e instanceof NpcFaction) {
			return !getFaction().equals(((NpcFaction) e).getFaction());//can only be attacked by other factions, not your own...disable friendly fire
		}
		return true;
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		super.onDeath(damageSource);
		if (damageSource.getTrueSource() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) damageSource.getTrueSource();
			FactionTracker.INSTANCE.adjustStandingFor(world, player.getName(), getFaction(), -AWNPCStatics.factionLossOnDeath);
		} else if (damageSource.getTrueSource() instanceof NpcPlayerOwned) {
			String playerName = ((NpcBase) damageSource.getTrueSource()).getOwner().getName();
			if (playerName != null) {
				FactionTracker.INSTANCE.adjustStandingFor(world, playerName, getFaction(), -AWNPCStatics.factionLossOnDeath);
			}
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
		tag.setString("factionName", factionName);
	}
}
