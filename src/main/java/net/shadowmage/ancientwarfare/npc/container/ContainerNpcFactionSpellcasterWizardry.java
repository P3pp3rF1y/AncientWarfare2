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

	private boolean hasChanged;//if set to true, will set all flags to entity on container close

	public ContainerNpcFactionSpellcasterWizardry(EntityPlayer player, int x, int y, int z) {
		super(player, x);
		assignedSpells = entity.getSpells();

		if (entity.world.isRemote) {
			System.out.println("Client: " + entity.getSpells());
		} else {
			System.out.println("Server: " + entity.getSpells());
		}
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
	//
	//	public List<String> getAllSpellNames() {
	//		return allSpellNames;
	//	}

	public List<Spell> getAllSpells() {
		return allSpells;
	}

	//	public void handleSpellSelection(String name) { //todo: might remove this?
	//		//		NBTTagCompound tag = new NBTTagCompound();
	//		//		tag.setString("structName", name);
	//		//		sendDataToServer(tag);
	//	}
	//
	//	public List<String> getEntitySpellNames() {
	//		List<String> spellNames = entitySpells.stream().map(Spell::getDisplayName).collect(Collectors.toList());
	//		for (String spell : spellNames) {
	//			System.out.println(spell);
	//		}
	//		return spellNames;
	//	}

	//	public List<Spell> assignedSpells = new ArrayList<>();
	//
	//	private boolean hasChanged;//if set to true, will set all flags to entity on container close
	//
	//	public ContainerNpcFactionSpellcasterWizardry(EntityPlayer player, int x) {
	//		super(player, x);
	//		//		assignedSpells = entity.getAssignedSpells();
	//		assignedSpells.add(Spells.darkness_orb);
	//	}
	//
	//	public void sendChangesToServer() {
	//		sendDataToServer(serializeContainerData());
	//	}
	//

	//
	//	public List<Spell> getAssignedSpells() {
	//		return assignedSpells;
	//	}
	//
	//	@Override
	//	public void handlePacketData(NBTTagCompound tag) {
	//		refreshGui();
	//	}
	//
	//	//	@Override
	//	//	public void sendInitData() {
	//	//		NBTTagCompound tag = new NBTTagCompound();
	//	//		tag.setTag("assignedSpells", NBTExtras.listToNBT(assignedSpells, spell -> new NBTTagInt(spell.metadata())));
	//	//		sendDataToClient(tag);
	//	//	}
	//	//
	//	//	@Override
	//	//	public void handlePacketData(NBTTagCompound tag) {
	//	//		if (tag.hasKey("assignedSpells")) {
	//	//			assignedSpells = (List<Spell>) NBTExtras.NBTToList(tag.getTagList("assignedSpells", Constants.NBT.TAG_INT),
	//	//					(NBTTagInt nbt) -> Spell.byMetadata(nbt.getInt()));
	//	//		}
	//	//		refreshGui();
	//	//	}
	//
	//}
}