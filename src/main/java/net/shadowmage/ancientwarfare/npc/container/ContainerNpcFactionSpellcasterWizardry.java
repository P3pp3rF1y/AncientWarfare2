package net.shadowmage.ancientwarfare.npc.container;

import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.NBTExtras;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionSpellcasterWizardry;

import java.util.List;

public class ContainerNpcFactionSpellcasterWizardry extends ContainerNpcBase<NpcFactionSpellcasterWizardry> {

	private final List<Spell> allSpells = Spell.getAllSpells();

	private List<Spell> assignedSpells;

	private boolean hasChanged; //if set to true, will set all flags to entity on container close

	public ContainerNpcFactionSpellcasterWizardry(EntityPlayer player, int x, int y, int z) {
		super(player, x);
		assignedSpells = entity.getSpells();
	}

	public void sendChangesToServer() {
		sendDataToServer(serializeContainerData());
	}

	private NBTTagCompound serializeContainerData() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("assignedSpells", NBTExtras.listToNBT(assignedSpells, spell -> new NBTTagInt(spell.metadata())));
		return tag;
	}

	@Override
	public void sendInitData() {
		sendDataToClient(serializeContainerData());
	}

	@Override
	public void handlePacketData(NBTTagCompound nbt) {
		assignedSpells = ((List<Spell>) NBTExtras.NBTToList(nbt.getTagList("assignedSpells", Constants.NBT.TAG_INT),
				(NBTTagInt tag) -> Spell.byMetadata(tag.getInt())));
		hasChanged = true;
		refreshGui();
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		if (hasChanged && !player.world.isRemote) {
			hasChanged = false;
			entity.setSpells(assignedSpells);
		}
		super.onContainerClosed(par1EntityPlayer);
	}

	public List<Spell> getAssignedSpells() {
		return assignedSpells;
	}

	public void addSpell(Spell spell) {
		assignedSpells.add(spell);
	}

	public void removeSpell(Spell spell) {
		assignedSpells.remove(spell);
	}

	public List<Spell> getAllSpells() {
		return allSpells;
	}
}