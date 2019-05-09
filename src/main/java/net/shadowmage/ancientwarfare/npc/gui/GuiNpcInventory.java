package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcInventory;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcCombat;
import net.shadowmage.ancientwarfare.npc.init.AWNPCItems;

public class GuiNpcInventory extends GuiContainerBase<ContainerNpcInventory> {
	private Text nameInput;
	Button button;
	private Checkbox doNotPursueCheckbox;
	private static int buttonX = 8 + 18 + 18 + 18 + 18 + 4;
	private Button skinButton;

	public GuiNpcInventory(ContainerBase container) {
		super(container);
		this.xSize = 178;
		this.ySize = getContainer().guiHeight;
	}

	@Override
	public void initElements() {
		Label label = new Label(8 + 18 + 18 + 4, 9, "guistrings.npc.npc_name");
		addGuiElement(label);

		nameInput = new Text(75, 8, 95, getContainer().entity.getCustomNameTag(), this) {
			@Override
			public void onTextUpdated(String oldText, String newText) {
				getContainer().handleNpcNameUpdate(newText);
			}
		};
		addGuiElement(nameInput);

		label = new Label(8 + 18 + 18 + 4, 21, "guistrings.npc.npc_texture");
		addGuiElement(label);

		skinButton = new Button(75, 20, 95, 12, getContainer().skinSettings.getDescription()) {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiSkinSelection(GuiNpcInventory.this, getContainer()));
			}
		};
		addGuiElement(skinButton);

		if (getContainer().entity instanceof NpcBase) {

			button = new Button(buttonX, 36, 75, 12, "guistrings.npc.togglefollow") {
				@Override
				protected void onPressed() {
					getContainer().togglefollow();
					closeGui();
				}
			};
			addGuiElement(button);
		}

		button = new Button(buttonX, 48, 75, 12, "guistrings.npc.set_home") {
			@Override
			protected void onPressed() {
				getContainer().setHome();
			}
		};
		addGuiElement(button);

		button = new Button(buttonX, 60, 75, 12, "guistrings.npc.clear_home") {
			@Override
			protected void onPressed() {
				getContainer().clearHome();
			}
		};
		addGuiElement(button);

		if (getContainer().entity.hasAltGui()) {
			button = new Button(buttonX, 72, 75, 12, "guistrings.npc.alt_gui") {
				@Override
				protected void onPressed() {
					getContainer().entity.openAltGui(player);
				}
			};
			addGuiElement(button);
		}

		if (!AWNPCStatics.repackCreativeOnly || (AWNPCStatics.repackCreativeOnly && player.capabilities.isCreativeMode)) {
			button = new Button(buttonX, 84, 75, 12, "guistrings.npc.repack") {
				@Override
				protected void onPressed() {
					getContainer().repack();
					closeGui();
				}
			};
			addGuiElement(button);
		}

		if (player.capabilities.isCreativeMode) {
			button = new Button(buttonX, 96, 75, 12, "guistrings.npc.creative_gui") {
				@Override
				protected void onPressed() {
					NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_CREATIVE, getContainer().entity.getEntityId(), 0, 0);
				}
			};
			addGuiElement(button);
		}

		if (getContainer().isArcher) {
			doNotPursueCheckbox = new Checkbox(buttonX, 108, 12, 12, "guistrings.npc.donotpursue") {
				@Override
				public void onToggled() {
					getContainer().doNotPursue = checked();
				}
			};
			addGuiElement(doNotPursueCheckbox);
		}

		ItemSlot slot;
		boolean isCombatNpc = getContainer().entity instanceof NpcCombat;

		slot = new ItemSlot(26, 8, new ItemStack(isCombatNpc ? Items.IRON_SWORD : Items.IRON_PICKAXE), this);
		slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false).addTooltip(isCombatNpc ? "guistrings.npc.weapon_slot" : "guistrings.npc.tool_slot");
		addGuiElement(slot);

		slot = new ItemSlot(26, 8 + 18 * 1, new ItemStack(AWNPCItems.WOODEN_SHIELD), this);
		slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false).addTooltip(isCombatNpc ? "guistrings.npc.shield_slot" : "guistrings.npc.offhand_slot");
		addGuiElement(slot);

		slot = new ItemSlot(26, 8 + 18 * 2, new ItemStack(Items.IRON_HELMET), this);
		slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false).addTooltip("guistrings.npc.helmet_slot");
		addGuiElement(slot);

		slot = new ItemSlot(26, 8 + 18 * 3, new ItemStack(Items.IRON_CHESTPLATE), this);
		slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false).addTooltip("guistrings.npc.chest_slot");
		addGuiElement(slot);

		slot = new ItemSlot(26, 8 + 18 * 4, new ItemStack(Items.IRON_LEGGINGS), this);
		slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false).addTooltip("guistrings.npc.legs_slot");
		addGuiElement(slot);

		slot = new ItemSlot(26, 8 + 18 * 5, new ItemStack(Items.IRON_BOOTS), this);
		slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false).addTooltip("guistrings.npc.boots_slot");
		addGuiElement(slot);

		slot = new ItemSlot(28 + 18 * 2, 8 + 18 * 2, new ItemStack(AWNPCItems.UPKEEP_ORDER), this);
		slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false).addTooltip("guistrings.npc.upkeep_order_slot");
		addGuiElement(slot);

		slot = new ItemSlot(28 + 18 * 2, 8 + 18 * 3, new ItemStack(AWNPCItems.ROUTING_ORDER), this);
		slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false).addTooltip("guistrings.npc.manual_order_slot");
		addGuiElement(slot);
	}

	@Override
	public void setupElements() {
		nameInput.setText(getContainer().entity.getCustomNameTag());
		if (getContainer().isArcher) {
			doNotPursueCheckbox.setChecked(getContainer().doNotPursue);
		}
		skinButton.setText(getContainer().skinSettings.getDescription());
	}

	@Override
	protected boolean onGuiCloseRequested() {
		getContainer().sendChangesToServer();
		getContainer().setName();
		return super.onGuiCloseRequested();

	}

}
