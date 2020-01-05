package net.shadowmage.ancientwarfare.npc.entity.faction;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;
import net.shadowmage.ancientwarfare.npc.ai.AIHelper;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionFleeSun;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionRestrictSun;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.entity.faction.attributes.AdditionalAttributes;
import net.shadowmage.ancientwarfare.npc.entity.faction.attributes.IAdditionalAttribute;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;
import net.shadowmage.ancientwarfare.npc.registry.FactionNpcDefault;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;
import net.shadowmage.ancientwarfare.npc.registry.NpcDefaultsRegistry;
import net.shadowmage.ancientwarfare.npc.registry.StandingChanges;
import net.shadowmage.ancientwarfare.structure.util.CapabilityRespawnData;
import net.shadowmage.ancientwarfare.structure.util.IRespawnData;
import net.shadowmage.ancientwarfare.structure.util.SpawnerHelper;
import org.apache.commons.lang3.Range;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings({"squid:MaximumInheritanceDepth", "squid:S2160"})
public abstract class NpcFaction extends NpcBase {
	private static final int DEATH_REVENGE_TICKS = 6000;
	private static final int HIT_REVENGE_TICKS = DEATH_REVENGE_TICKS / 20;
	private static final int REVENGE_LIST_VALIDATION_TICKS = DEATH_REVENGE_TICKS / 100;
	private static final double REVENGE_SET_RANGE = 50D;
	private static final String HEIGHT_TAG = "height";

	protected String factionName;
	private Map<String, Long> revengePlayers = new HashMap<>();

	public NpcFaction(World world) {
		super(world);
		addAI();
	}

	public NpcFaction(World world, String factionName) {
		super(world);
		setFactionNameAndDefaults(factionName);
		addAI();
	}

	private Map<IAdditionalAttribute<?>, Object> additionalAttributes = new HashMap<>();
	private boolean canDespawn = false;

	@Override
	protected boolean canDespawn() {
		return canDespawn;
	}

	private void setDeathRevengePlayer(String playerName) {
		revengePlayers.put(playerName, world.getTotalWorldTime() + DEATH_REVENGE_TICKS);
	}

	@Override
	protected void despawnEntity() {
		boolean deadPreDespawn = isDead;

		super.despawnEntity();

		if (!deadPreDespawn && isDead && hasCapability(CapabilityRespawnData.RESPAWN_DATA_CAPABILITY, null)) {
			IRespawnData respawnData = getCapability(CapabilityRespawnData.RESPAWN_DATA_CAPABILITY, null);
			if (respawnData != null) {
				SpawnerHelper.createSpawner(respawnData, world);
			}
		}
	}

	public void setCanDespawn() {
		canDespawn = true;
	}

	private void addAI() {
		tasks.addTask(2, new NpcAIFactionRestrictSun(this));
		tasks.addTask(3, new NpcAIFactionFleeSun(this, 1.0D));
	}

	public void setAdditionalAttribute(IAdditionalAttribute<?> attribute, Object value) {
		additionalAttributes.put(attribute, value);
	}

	<T> Optional<T> getAdditionalAttributeValue(IAdditionalAttribute<T> attribute) {
		return Optional.ofNullable(attribute.getValueClass().cast(additionalAttributes.get(attribute)));
	}

	public boolean burnsInSun() {
		return getAdditionalAttributeValue(AdditionalAttributes.BURNS_IN_SUN).orElse(false);
	}

	public boolean isUndead() {
		return getAdditionalAttributeValue(AdditionalAttributes.UNDEAD).orElse(false);
	}

	@Override
	public void onLivingUpdate() {
		doSunBurn();
		super.onLivingUpdate();
	}

	private void doSunBurn() {
		if (burnsInSun() && world.isDaytime() && !world.isRemote) {
			float brightness = getBrightness();
			BlockPos blockpos = getRidingEntity() instanceof EntityBoat ? (new BlockPos(posX, (double) Math.round(posY), posZ)).up() : new BlockPos(posX, Math.round(posY), posZ);

			if (brightness > 0.5F && rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F && world.canSeeSky(blockpos)) {
				ItemStack helmet = getItemStackFromSlot(EntityEquipmentSlot.HEAD);
				if (helmet.isEmpty()) {
					setFire(8);
				} else {
					damageHelmet(helmet);
				}
			}
		}
	}

	private void damageHelmet(ItemStack helmet) {
		if (helmet.isItemStackDamageable()) {
			helmet.setItemDamage(helmet.getItemDamage() + rand.nextInt(2));

			if (helmet.getItemDamage() >= helmet.getMaxDamage()) {
				renderBrokenItemStack(helmet);
				setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
			}
		}
	}

	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		if (isUndead()) {
			return EnumCreatureAttribute.UNDEAD;
		} else
			return EnumCreatureAttribute.UNDEFINED;
	}

	public void setFactionNameAndDefaults(String factionName) {
		this.factionName = factionName;
		FactionNpcDefault npcDefault = NpcDefaultsRegistry.getFactionNpcDefault(this);
		applyFactionNpcSettings(npcDefault);
		// do not apply the default equipment if the hasCustomEquipment tag was set to true
		if (!getCustomEquipmentOverride()) {
			npcDefault.applyEquipment(this);
		}

		// makes faction NPCs drop all their items otherwise use the default vanilla drop rate
		if (!AWNPCStatics.vanillaEquipmentDropRate) {
			inventoryArmorDropChances = new float[] {1.f, 1.f, 1.f, 1.f};
			inventoryHandsDropChances = new float[] {1.f, 1.f};
		}

		Range<Float> heightRange = npcDefault.getHeightRange();
		float newHeight = heightRange.getMinimum() + world.rand.nextFloat() * (heightRange.getMaximum() - heightRange.getMinimum());
		setSize((newHeight / 1.8f) * 0.6f * npcDefault.getThinness(), newHeight);
	}

	private void applyFactionNpcSettings(FactionNpcDefault npcDefault) {
		npcDefault.applyAttributes(this);
		npcDefault.applyAdditionalAttributes(this);
		experienceValue = npcDefault.getExperienceDrop();
		npcDefault.applyPathSettings((PathNavigateGround) getNavigator());
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
	@SuppressWarnings("squid:CallToDeprecatedMethod") //need to use I18n call that's available server side for this
	public String getName() {
		//noinspection deprecation
		String name = I18n.translateToLocal("entity.ancientwarfarenpc." + getNpcFullType() + ".name");
		if (hasCustomName()) {
			name = name + " : " + getCustomNameTag();
		}
		return name;
	}

	@Override
	protected float getLitBlockWeight(BlockPos pos) {
		return burnsInSun() ? 1F - world.getLightBrightness(pos) : super.getLitBlockWeight(pos);
	}

	@Override
	public String getNpcFullType() {
		return factionName + "." + super.getNpcFullType();
	}

	@Override
	public boolean isHostileTowards(Entity e) {
		if (e instanceof EntityPlayer || e instanceof NpcPlayerOwned) {
			String playerName = e instanceof EntityPlayer ? e.getName() : ((NpcBase) e).getOwner().getName();
			UUID playerUUID = e instanceof EntityPlayer ? e.getUniqueID() : ((NpcBase) e).getOwner().getUUID();
			return revengePlayers.keySet().contains(playerName) || FactionTracker.INSTANCE.isHostileToPlayer(world, playerUUID, playerName, getFaction());
		} else if (e instanceof NpcFaction) {
			NpcFaction npc = (NpcFaction) e;
			return !npc.getFaction().equals(factionName) && FactionRegistry.getFaction(getFaction()).isHostileTowards(npc.getFaction());
		} else {
			return FactionRegistry.getFaction(factionName).isTarget(e) || AIHelper.isAdditionalEntityToTarget(e);
		}
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if (world.getWorldTime() % REVENGE_LIST_VALIDATION_TICKS == 0) {
			Iterator<Map.Entry<String, Long>> it = revengePlayers.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Long> playerRevengeTime = it.next();
				if (world.getTotalWorldTime() > playerRevengeTime.getValue()) {
					it.remove();
					setAttackTarget(null);
					setRevengeTarget(null);
				}
			}
		}

	}

	@Override
	protected void damageEntity(DamageSource damageSrc, float damageAmount) {
		super.damageEntity(damageSrc, damageAmount);

		if (damageSrc.damageType.equals("player")) {
			//noinspection ConstantConditions
			revengePlayers.put(damageSrc.getTrueSource().getName(), world.getTotalWorldTime() + HIT_REVENGE_TICKS);
		} else if ((damageSrc.damageType.equals("mob") && damageSrc.getTrueSource() instanceof NpcBase)) {
			revengePlayers.put(((NpcBase) damageSrc.getTrueSource()).getOwner().getName(), world.getTotalWorldTime() + HIT_REVENGE_TICKS);
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
		if (damageSource.getTrueSource() instanceof EntityPlayer || damageSource.getTrueSource() instanceof NpcPlayerOwned) {
			String playerName = damageSource.getTrueSource() instanceof EntityPlayer ? damageSource.getTrueSource().getName() :
					((NpcBase) damageSource.getTrueSource()).getOwner().getName();
			FactionTracker.INSTANCE.adjustStandingFor(world, playerName, getFaction(), FactionRegistry.getFaction(getFaction()).getStandingChange(StandingChanges.KILL));

			setDeathRevengePlayer(playerName);
			world.getEntitiesWithinAABB(NpcFaction.class, new AxisAlignedBB(getPosition()).grow(REVENGE_SET_RANGE))
					.forEach(factionNpc -> {
						if (factionNpc.getFaction().equals(getFaction())) {
							factionNpc.setDeathRevengePlayer(playerName);
						}
					});
		}
	}

	@Nullable
	@Override
	protected ResourceLocation getLootTable() {
		return NpcDefaultsRegistry.getFactionNpcDefault(this).getLootTable();
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
		buffer.writeFloat(height);
		buffer.writeFloat(width);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		super.readSpawnData(buffer);
		factionName = new PacketBuffer(buffer).readString(20);
		height = buffer.readFloat();
		width = buffer.readFloat();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		factionName = tag.getString("factionName");
		applyFactionNpcSettings(NpcDefaultsRegistry.getFactionNpcDefault(this));
		canDespawn = tag.getBoolean("canDespawn");
		revengePlayers = NBTHelper.getMap(tag.getTagList("revengePlayers", Constants.NBT.TAG_COMPOUND),
				t -> t.getString("playerName"), t -> t.getLong("time"));
		if (tag.hasKey(HEIGHT_TAG)) {
			height = tag.getFloat(HEIGHT_TAG);
			width = tag.getFloat("width");
		} else {
			setFactionNameAndDefaults(factionName);
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		if (factionName != null) {
			tag.setString("factionName", factionName);
		}
		tag.setBoolean("canDespawn", canDespawn);
		tag.setTag("revengePlayers", NBTHelper.mapToCompoundList(revengePlayers,
				(t, playerName) -> t.setString("playerName", playerName), (t, time) -> t.setLong("time", time)));
		tag.setFloat(HEIGHT_TAG, height);
		tag.setFloat("width", width);
	}

	@Override
	public float getRenderSizeModifier() {
		return height / 1.8f;
	}

	public float getWidthModifier() {
		return width / 0.6f;
	}
}
