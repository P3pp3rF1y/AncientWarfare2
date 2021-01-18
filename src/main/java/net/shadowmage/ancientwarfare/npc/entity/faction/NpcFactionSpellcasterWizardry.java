package net.shadowmage.ancientwarfare.npc.entity.faction;

import electroblob.wizardry.entity.living.ISpellCaster;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackNearest;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDoor;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWatchClosest;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionArcherStayAtHome;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionHurt;
import net.shadowmage.ancientwarfare.npc.compat.ebwizardry.ai.EntityAIAttackSpellImproved;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;

import java.util.ArrayList;
import java.util.List;

/* Spellcaster NPC class for Electroblob's Wizardry spell-capable spellcaster NPCs
 * The class is only loaded if EBWizardry is present, otherwise NpcFactionSpellcaster.class is loaded,
 * which cannot have any references to EBWizardry objects */
public class NpcFactionSpellcasterWizardry extends NpcFaction implements ISpellCaster {

	private EntityAIAttackSpellImproved<NpcFactionSpellcasterWizardry> spellCastingAI = new EntityAIAttackSpellImproved<>(this, 0.85D, 16.0F, 16, 60);

	/**
	 * Data parameter for the cooldown time for wizards healing themselves.
	 */
	private static final DataParameter<Integer> HEAL_COOLDOWN = EntityDataManager.createKey(NpcFactionSpellcasterWizardry.class, DataSerializers.VARINT);
	// Field implementations
	private List<Spell> spells = new ArrayList<Spell>(4);
	private Spell continuousSpell;
	private int spellCounter;

	@SuppressWarnings("unused")
	public NpcFactionSpellcasterWizardry(World world) {
		super(world);
		addAI();
		tasks.addTask(3, spellCastingAI);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(HEAL_COOLDOWN, -1);
	}

	@SuppressWarnings("unused")
	public NpcFactionSpellcasterWizardry(World world, String factionName) {
		super(world, factionName);
		addAI();
		tasks.addTask(3, spellCastingAI);
	}

	@SuppressWarnings("Duplicates")
	private void addAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
		tasks.addTask(0, new NpcAIDoor(this, true));
		tasks.addTask(1, new NpcAIFollowPlayer(this));
		tasks.addTask(2, new NpcAIMoveHome(this, 50.f, 5.f, 30.f, 5.f));
		tasks.addTask(2, new NpcAIFactionArcherStayAtHome(this));

		tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		tasks.addTask(102, new NpcAIWander(this));
		tasks.addTask(103, new NpcAIWatchClosest(this, EntityLiving.class, 8.0F));

		targetTasks.addTask(1, new NpcAIFactionHurt(this, this::isHostileTowards));
		targetTasks.addTask(2, new NpcAIAttackNearest(this, this::isHostileTowards));
	}

	@Override
	public void onLivingUpdate() {

		super.onLivingUpdate();

		// Still better to store this to a local variable as it's almost certainly more efficient.
		int healCooldown = getHealCooldown();

		// This is now done slightly differently because isPotionActive doesn't work on client here, meaning that when
		// affected with arcane jammer and healCooldown == 0, whilst the wizard didn't actually heal or play the sound,
		// the particles still spawned, and since healCooldown wasn't reset they spawned every tick until the arcane
		// jammer wore off.
		if (healCooldown == 0 && getHealth() < getMaxHealth() && getHealth() > 0
				&& !isPotionActive(WizardryPotions.arcane_jammer)) {

			heal(4);
			setHealCooldown(-1);

			// deathTime == 0 checks the spellcaster isn't currently dying
		} else if (healCooldown == -1 && deathTime == 0) {

			// Heal particles, colored with faction colors
			if (world.isRemote) {
				spawnHealParticles();
			} else {
				if (getHealth() < 25) {
					// spellcasters heal themselves more often if they have low health
					setHealCooldown(150);
				} else {
					setHealCooldown(400);
				}

				playSound(Spells.heal.getSounds()[0], 0.7F, rand.nextFloat() * 0.4F + 1.0F);
			}
		}

		if (healCooldown > 0) {
			setHealCooldown(healCooldown - 1);
		}
	}

	/**
	 * prevents spellcasters from attacking their own summoned minions
	 */
	@Override
	public boolean isHostileTowards(Entity e) {
		return e instanceof ISummonedCreature && (((ISummonedCreature) e).getOwnerId() != getUniqueID() || ((ISummonedCreature) e).getOwner() != null
				&& isHostileTowards(((ISummonedCreature) e).getOwner())) || super.isHostileTowards(e);
	}

	private int getHealCooldown() {
		return dataManager.get(HEAL_COOLDOWN);
	}

	private void setHealCooldown(int cooldown) {
		dataManager.set(HEAL_COOLDOWN, cooldown);
	}

	public void setSpells(List<Spell> spells) {
		this.spells = spells;
	}

	@Override
	public List<Spell> getSpells() {
		return spells;
	}

	@Override
	public void setSpellCounter(int count) {
		spellCounter = count;
	}

	@Override
	public int getSpellCounter() {
		return spellCounter;
	}

	@Override
	public SpellModifiers getModifiers() {
		SpellModifiers spellModifiers = new SpellModifiers();
		spellModifiers.set("potency", 1.5f, true);
		return spellModifiers;
	}

	@Override
	public void setContinuousSpell(Spell spell) {
		continuousSpell = spell;
	}

	@Override
	public Spell getContinuousSpell() { return continuousSpell; }

	@Override
	public int getAimingError(EnumDifficulty difficulty) { // for spells
		switch (difficulty) {
			case EASY:
				return 7;
			case NORMAL:
				return 4;
			case HARD:
				return 1;
			default:
				return 7; // Peaceful counts as easy
		}
	}

	@Override
	public boolean hasAltGui() {
		return true;
	}

	@Override
	public void openAltGui(EntityPlayer player) {
		NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_FACTION_SPELLCASTER_WIZARDRY, getEntityId(), 0, 0);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setTag("spells", NBTHelper.getTagList(getSpells(), spell -> new NBTTagString(spell.getRegistryName().toString())));
	}

	@Override
	public void writeAdditionalItemData(NBTTagCompound nbt) {
		nbt.setTag("spells", NBTHelper.getTagList(getSpells(), spell -> new NBTTagString(spell.getRegistryName().toString())));
		super.writeAdditionalItemData(nbt);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		spells = (NBTHelper.getList(nbt.getTagList("spells", Constants.NBT.TAG_STRING),
				tag -> Spell.get(((NBTTagString) tag).getString())));
	}

	@Override
	public void readAdditionalItemData(NBTTagCompound nbt) {
		spells = ((List<Spell>) NBTHelper.getList(nbt.getTagList("spells", Constants.NBT.TAG_STRING),
				tag -> Spell.get(((NBTTagString) tag).getString())));
		super.readAdditionalItemData(nbt);
	}

	@Override
	public boolean canAttackClass(Class claz) {
		return true;
	}

	@Override
	public boolean worksInRain() {
		return true;
	}

	@Override
	public boolean isPassive() {
		return false;
	}

	@Override
	public String getNpcType() {
		return "spellcaster";
	}

	@Override
	public boolean shouldSleep() {
		return false;
	}

	private void spawnHealParticles() {
		int color = FactionRegistry.getFaction(factionName).getColor();

		for (int i = 0; i < 10; i++) {
			double x = posX + world.rand.nextDouble() * 2 - 1;
			double y = getEntityBoundingBox().minY + getEyeHeight() - 0.5 + world.rand.nextDouble();
			double z = posZ + world.rand.nextDouble() * 2 - 1;
			ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).clr(color).spawn(world);
		}

		ParticleBuilder.create(ParticleBuilder.Type.BUFF).entity(this).clr(color).spawn(world);
	}

}
