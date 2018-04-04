package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.automation.container.ContainerMailbox;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import org.lwjgl.input.Mouse;

import java.util.List;

public class GuiMailboxNameSelect extends GuiContainerBase<ContainerMailbox> {

	private final GuiMailboxInventory parent;
	private final boolean name;//true if setting mailbox name, false for setting target name
	private final boolean privateBox;

	private Text nameInputArea;
	private CompositeScrolled nameSelectArea;

	public GuiMailboxNameSelect(GuiMailboxInventory parent, boolean name) {
		super(parent.getContainer());
		this.parent = parent;
		this.name = name;
		this.privateBox = getContainer().privateBox;
	}

	@Override
	public void initElements() {
		nameInputArea = new Text(8, 8, 120, name ? getContainer().mailboxName : getContainer().targetName, this);
		addGuiElement(nameInputArea);

		Button addNameButton = new Button(8, 22, 55, 12, "guistrings.automation.add_mailbox") {
			@Override
			protected void onPressed() {
				String name = nameInputArea.getText();
				if (!name.isEmpty()) {
					getContainer().handleNameAdd(name);
				}
			}
		};
		addGuiElement(addNameButton);

		Button selectButton = new Button(256 - 8 - 55, 8, 55, 12, "guistrings.automation.select_mailbox") {
			@Override
			protected void onPressed() {
				if (name) {
					getContainer().handleNameSelection(nameInputArea.getText());
				} else {
					getContainer().handleTargetSelection(nameInputArea.getText());
				}
				closeGui();
			}
		};
		addGuiElement(selectButton);

		Button deleteNameButton = new Button(8 + 55, 22, 55, 12, "guistrings.automation.delete_mailbox") {
			@Override
			protected void onPressed() {
				String name = nameInputArea.getText();
				if (!name.isEmpty()) {
					getContainer().handleNameDelete(name);
				}
			}
		};
		addGuiElement(deleteNameButton);

		nameSelectArea = new CompositeScrolled(this, 0, 20 + 4 + 12, 256, 240 - 8 - 12 - 4 - 12);
		addGuiElement(nameSelectArea);
	}

	@Override
	public void setupElements() {
		nameSelectArea.clearElements();

		List<String> names = privateBox ? getContainer().privateBoxNames : getContainer().publicBoxNames;
		Button button;
		int totalHeight = 8;
		for (String name : names) {
			if (name.equals(getContainer().mailboxName) || name.equals(getContainer().targetName)) {
				continue;
			}
			button = new Button(8, totalHeight, 240 - 24 - 14, 12, name) {
				@Override
				protected void onPressed() {
					nameInputArea.setText(text);
				}
			};
			nameSelectArea.addGuiElement(button);
			totalHeight += 12;
		}
		nameSelectArea.setAreaSize(totalHeight);
	}

	@Override
	protected boolean onGuiCloseRequested() {
		getContainer().addSlots();
		getContainer().setGui(parent);
		int x = Mouse.getX();
		int y = Mouse.getY();
		Minecraft.getMinecraft().displayGuiScreen(parent);
		Mouse.setCursorPosition(x, y);
		return false;
	}

}
