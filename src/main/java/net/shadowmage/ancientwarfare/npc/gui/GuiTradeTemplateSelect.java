package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcFactionTradeSetup;
import net.shadowmage.ancientwarfare.npc.registry.FactionTradeListRegistry;
import net.shadowmage.ancientwarfare.npc.registry.FactionTradeListTemplate;

import java.util.HashMap;
import java.util.Map;

public class GuiTradeTemplateSelect extends GuiContainerBase {
	private final GuiContainerBase<ContainerNpcFactionTradeSetup> parent;
	private String factionName;
	private CompositeScrolled area;

	protected GuiTradeTemplateSelect(GuiContainerBase<ContainerNpcFactionTradeSetup> parent, String factionName) {
		super(parent.getContainer(), 320, 240);
		this.parent = parent;
		this.factionName = factionName;
	}

	@Override
	public void initElements() {
		area = new CompositeScrolled(this, 0, 0, 320, 148);
		addGuiElement(area);
		int totalHeight = 8;
		Button button;

		Map<String, FactionTradeListTemplate> tradeLists = new HashMap<>(FactionTradeListRegistry.getDefaults());
		tradeLists.putAll(FactionTradeListRegistry.getFactionDefaults(factionName));
		for (Map.Entry<String, FactionTradeListTemplate> tradeListTemplate : tradeLists.entrySet()) {
			button = new Button(8, totalHeight, 320 - 8 - 16, 12, tradeListTemplate.getKey()) {
				@Override
				protected void onPressed() {
					parent.getContainer().setTradeList(tradeListTemplate.getValue().toTradeList());
					Minecraft.getMinecraft().displayGuiScreen(parent);
					parent.refreshGui();
				}
			};
			area.addGuiElement(button);
			totalHeight += 12;
		}
		area.setAreaSize(totalHeight);
	}

	@Override
	public void setupElements() {
		//noop
	}

	@Override
	protected boolean onGuiCloseRequested() {
		Minecraft.getMinecraft().displayGuiScreen(parent);
		return false;
	}
}
