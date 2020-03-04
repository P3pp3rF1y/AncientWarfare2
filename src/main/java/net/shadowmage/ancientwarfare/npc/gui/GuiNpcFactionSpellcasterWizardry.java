package net.shadowmage.ancientwarfare.npc.gui;

import electroblob.wizardry.spell.Spell;
import net.minecraft.util.text.translation.I18n;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcFactionSpellcasterWizardry;
import net.shadowmage.ancientwarfare.npc.registry.NpcDefaultsRegistry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GuiNpcFactionSpellcasterWizardry extends GuiContainerBase<ContainerNpcFactionSpellcasterWizardry> {

	private Text spellFilterInput;
	private CompositeScrolled selectionArea;
	private CompositeScrolled assignedSpellsArea;
	private Label selection;
	Button button;

	private boolean hasChanged = false;

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
				getContainer().addSlots();
				closeGui();
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

		for (int i = 0; i < 5; i++) {
			String name = I18n.translateToLocal("entity.ancientwarfarenpc." + getContainer().entity.getNpcFullType() + "." + (i + 1 + ".name"));
			String presetSubtypeName = "spellcaster." + (i + 1);
			if (NpcDefaultsRegistry.getFactionNpcDefault(getContainer().entity.getFaction(), presetSubtypeName).isEnabled()) {
				int x;
				int y;
				if (i < 2) {
					x = 8; // first button row
					y = 8 + (15 * i);
				} else {
					x = 8 + 112; // second button row
					y = 8 + (15 * (i - 2));
				}
				button = new Button(x, y, 100, 12, name) {
					@Override
					protected void onPressed() {
						hasChanged = true;
						getContainer().setNameAndPresetDefaults(presetSubtypeName);
						getContainer().sendChangesToServer();
						refreshGui();
					}
				};
			}
			addGuiElement(button);
		}
	}

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

		SpellAddButton spellAddButton;
		int selectionAreaHeight = 8;

		List<Spell> castableSpells = new ArrayList<>();
		castableSpells.addAll(getContainer().getAllSpells());

		/*
		Generates an ordered button list of all spells. Clicking on a button will assign the spell to the entity
		 */
		for (Spell currSpell : castableSpells.stream()
				.filter(spell -> spell.getDisplayName().toLowerCase().contains(spellFilterInput.getText().toLowerCase()))
				.sorted(Comparator.comparing(spell -> spell.getDisplayName().toLowerCase())).collect(Collectors.toList())) {
			spellAddButton = new SpellAddButton(8, selectionAreaHeight, currSpell);
			selectionArea.addGuiElement(spellAddButton);
			selectionAreaHeight += 12;
		}
		selectionArea.setAreaSize(selectionAreaHeight + 8);

		assignedSpellsArea.clearElements();

		SpellRemoveButton assignedSpellButton;
		int assignedSpellsAreaHeight = 8;

		List<Spell> assignedSpells = getContainer().getAssignedSpells();

		/*
		Generates an ordered button list of all assigned spells. Clicking on a button will unassign that spell
		 */
		for (Spell currSpell : assignedSpells.stream()
				.sorted(Comparator.comparing(spell -> spell.getDisplayName().toLowerCase())).collect(Collectors.toList())) {
			assignedSpellButton = new SpellRemoveButton(8, assignedSpellsAreaHeight, currSpell);
			assignedSpellsArea.addGuiElement(assignedSpellButton);
			assignedSpellsAreaHeight += 12;
		}

		assignedSpellsArea.setAreaSize(assignedSpellsAreaHeight + 8);

	}

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
