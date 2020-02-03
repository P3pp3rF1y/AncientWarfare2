package net.shadowmage.ancientwarfare.npc.gui;

import electroblob.wizardry.spell.Spell;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcFactionSpellcasterWizardry;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GuiNpcFactionSpellcasterWizardry extends GuiContainerBase<ContainerNpcFactionSpellcasterWizardry> {

	private Text spellFilterInput;
	private Spell currentSelection;
	private CompositeScrolled selectionArea;
	private CompositeScrolled assignedSpellsArea;

	private Label selection;

	private boolean hasChanged = false;

	//	private final List<Spell> allSpells = Spell.getAllSpells();
	//	private final List<String> allSpellNames = allSpells.stream().map(Spell::getDisplayName).collect(Collectors.toList());

	//	private List<Spell> entitySpells;
	//	List<Spell> spells = new ArrayList<Spell>(getContainer().entity.getAssignedSpells());

	public List<String> getEntitySpellNames() {
		List<String> spelz = getContainer().entity.getSpells().stream().map(Spell::getDisplayName).collect(Collectors.toList());
		//		getContainer().entity.getAssignedSpells().
		return spelz;
	}

	public GuiNpcFactionSpellcasterWizardry(ContainerBase container) {
		super(container);
	}

	@Override
	public void initElements() {
		xSize = 295;
		ySize = 240;
		addGuiElement(new Button(xSize - 55 - 8, 8, 55, 12, "guistrings.done") {
			@Override
			protected void onPressed() {
				//				if (currentSelection != null) { // do saving here??? todo:
				//					getContainer().handleNameSelection(currentSelection.name);
				getContainer().addSlots();
				closeGui();
				//				for (String spell : allSpellNames) {
				//					System.out.println(spell);
				//				}
				//				}
			}
		});

		Label label = new Label(10, 43, "guistrings.npc.search");
		addGuiElement(label);

		Label current = new Label(147, 55, "guistrings.npc.current_spells");
		addGuiElement(current);

		selection = new Label(8, 20, "");
		addGuiElement(selection);

		spellFilterInput = new Text(8, 18 + 36, 116, "", this) {
			@Override
			protected void handleKeyInput(int keyCode, char ch) {
				super.handleKeyInput(keyCode, ch);
				refreshGui();
			}
		};
		addGuiElement(spellFilterInput);

		selectionArea = new CompositeScrolled(this, 0, 70, 143, 168);
		addGuiElement(selectionArea);

		assignedSpellsArea = new CompositeScrolled(this, 143, 70, 143, 168);
		addGuiElement(assignedSpellsArea);

	}
	//
	//	protected List<String> getSpellNamesForDisplay() {
	//		return allSpellNames;
	//	}
	//
	//	private Set<String> getSpellNamesForDisplay() {
	//		re
	//	}

	private class SpellAddButton extends Button {
		private Spell spell;

		private SpellAddButton(int topLeftX, int topLeftY, Spell spell) {
			super(topLeftX, topLeftY, 115, 12, spell.getDisplayName());
			this.spell = spell;
		}

		@Override
		protected void onPressed() {
			hasChanged = true;
			getContainer().addSpell(spell);
			getContainer().sendChangesToServer();
			refreshGui();
		}
	}

	private class SpellRemoveButton extends Button {
		private Spell spell;

		private SpellRemoveButton(int topLeftX, int topLeftY, Spell spell) {
			super(topLeftX, topLeftY, 115, 12, spell.getDisplayName());
			this.spell = spell;
		}

		@Override
		protected void onPressed() {
			hasChanged = true;
			getContainer().removeSpell(spell);
			getContainer().sendChangesToServer();
			refreshGui();
		}
	}

	@Override
	public void setupElements() {

		selectionArea.clearElements();

		SpellAddButton button;
		int totalHeight = 8;

		/*
		Generates an ordered button list of all spells. Clicking on a button will assign the spell to the entity
		 */
		for (Spell currSpell : getContainer().getAllSpells().stream()
				.filter(spell -> spell.getDisplayName().toLowerCase().contains(spellFilterInput.getText().toLowerCase()))
				.sorted(Comparator.comparing(spell -> spell.getDisplayName().toLowerCase())).collect(Collectors.toList())) {
			button = new SpellAddButton(8, totalHeight, currSpell);
			selectionArea.addGuiElement(button);
			totalHeight += 12;
		}
		selectionArea.setAreaSize(totalHeight + 8);

		assignedSpellsArea.clearElements();

		SpellRemoveButton assignedSpellButton;
		int totalHeight2 = 8;

		List<Spell> assignedSpells = getContainer().getAssignedSpells();

		/*
		Generates an ordered button list of all assigned spells. Clicking on a button will unassign that spell
		 */
		for (Spell currSpell : assignedSpells.stream()
				.sorted(Comparator.comparing(spell -> spell.getDisplayName().toLowerCase())).collect(Collectors.toList())) {
			assignedSpellButton = new SpellRemoveButton(8, totalHeight2, currSpell);
			assignedSpellsArea.addGuiElement(assignedSpellButton);
			totalHeight2 += 12;
		}

		assignedSpellsArea.setAreaSize(totalHeight2 + 8);

	}

	//				.filter(spellName -> spellName.toLowerCase().contains(spellFilterInput.getText().toLowerCase()))

	@Override
	protected boolean onGuiCloseRequested() {
		/*
		 * if changes were made while gui was open, send these to server
		 */
		if (hasChanged) {
			getContainer().sendChangesToServer();
		}

		/*
		 * force opening of normal gui (whatever that may be for the npc) when advanced controls is closed
		 */
		getContainer().entity.openGUI(player);
		return false;

	}

}
