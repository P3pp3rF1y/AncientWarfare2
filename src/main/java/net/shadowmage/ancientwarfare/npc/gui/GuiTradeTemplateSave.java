package net.shadowmage.ancientwarfare.npc.gui;

import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;

public class GuiTradeTemplateSave extends GuiContainerBase {
	private final GuiNpcFactionTradeSetup parent;

	public GuiTradeTemplateSave(GuiNpcFactionTradeSetup parent) {
		super(parent.getContainer(), 320, 240);
		this.parent = parent;
	}

	@Override
	public void initElements() {
		addGuiElement(new Label(6, 6, "guistrings.template_name"));
		Text templateName = new Text(80, 4, 60, "", this);
		addGuiElement(templateName);
		Checkbox factionSpecific = new Checkbox(145, 4, 12, 12, "guistrings.template_faction_specific");
		addGuiElement(factionSpecific);
		addGuiElement(new Button(6, 18, 100, 12, "guistrings.save_template") {
			@Override
			protected void onPressed() {
				parent.getContainer().saveTradeTemplate(templateName.getText(), factionSpecific.checked());
				super.onPressed();
			}
		});
	}

	@Override
	public void setupElements() {
		//noop
	}
}
