package net.shadowmage.ancientwarfare.npc.entity.faction;

import electroblob.wizardry.entity.living.EntityAIAttackSpell;
import electroblob.wizardry.entity.living.ISpellCaster;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackNearest;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDoor;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIHurt;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWatchClosest;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionArcherStayAtHome;

import java.util.ArrayList;
import java.util.List;

/* Spellcaster NPC class for Electroblob's Wizardry spell-capable spellcaster NPCs
 * The class is only loaded if EBWizardry is present, otherwise NpcFactionSpellcaster.class is loaded,
 * which cannot have any references to EBWizardry objects */
public class NpcFactionSpellcasterWizardry extends NpcFaction implements ISpellCaster {

	private EntityAIAttackSpell<NpcFactionSpellcasterWizardry> spellCastingAI = new EntityAIAttackSpell<>(this, 0.75D, 16.0F, 32, 30);

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

	@SuppressWarnings("unused")
	public NpcFactionSpellcasterWizardry(World world, String factionName) {
		super(world, factionName);
		addAI();
		tasks.addTask(3, spellCastingAI);
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
		return new SpellModifiers();
	}

	@Override
	public void setContinuousSpell(Spell spell) {
		continuousSpell = spell;
	}

	@Override
	public Spell getContinuousSpell() { return continuousSpell; }

	@Override
	public boolean hasAltGui() {
		return true;
	}

	@Override
	public void openAltGui(EntityPlayer player) {
		NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_FACTION_SPELLCASTER_WIZARDRY, getEntityId(), 0, 0);
	}

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

		targetTasks.addTask(1, new NpcAIHurt(this));
		targetTasks.addTask(2, new NpcAIAttackNearest(this, this::isHostileTowards));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setTag("spells", NBTExtras.listToNBT(getSpells(), spell -> new NBTTagString(spell.getRegistryName().toString())));
	}

	@Override
	public void writeAdditionalItemData(NBTTagCompound nbt) {
		nbt.setTag("spells", NBTExtras.listToNBT(getSpells(), spell -> new NBTTagString(spell.getRegistryName().toString())));
		super.writeAdditionalItemData(nbt);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		spells = ((List<Spell>) NBTExtras.NBTToList(nbt.getTagList("spells", Constants.NBT.TAG_STRING),
				(NBTTagString tag) -> Spell.get(tag.getString())));
	}

	@Override
	public void readAdditionalItemData(NBTTagCompound nbt) {
		spells = ((List<Spell>) NBTExtras.NBTToList(nbt.getTagList("spells", Constants.NBT.TAG_STRING),
				(NBTTagString tag) -> Spell.get(tag.getString())));
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
}
