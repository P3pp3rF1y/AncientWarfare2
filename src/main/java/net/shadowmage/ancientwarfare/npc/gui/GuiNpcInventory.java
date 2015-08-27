package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcInventory;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;

public class GuiNpcInventory extends GuiContainerBase<ContainerNpcInventory> {

    private Text nameInput;

    private static int buttonX = 8 + 18 + 18 + 18 + 18 + 4;

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
        Text textureInput = new Text(75, 20, 95, getContainer().entity.getCustomTex(), this) {
            @Override
            public void onTextUpdated(String oldText, String newText) {
                getContainer().handleNpcTextureUpdate(newText);
                getContainer().entity.setCustomTexRef(newText);
            }
        };
        addGuiElement(textureInput);

        Button button = new Button(buttonX, 36, 75, 12, "guistrings.npc.repack") {
            @Override
            protected void onPressed() {
                getContainer().repack();
                closeGui();
            }
        };
        addGuiElement(button);

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

        if (player.capabilities.isCreativeMode) {
            button = new Button(buttonX, 84, 75, 12, "guistrings.npc.creative_gui") {
                @Override
                protected void onPressed() {
                    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_CREATIVE, getContainer().entity.getEntityId(), 0, 0);
                }
            };
            addGuiElement(button);
        }

        ItemSlot slot;

        slot = new ItemSlot(26, 8, new ItemStack(Items.iron_sword), this);
        slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false).addTooltip("guistrings.npc.weapon_slot");
        addGuiElement(slot);

        slot = new ItemSlot(26, 8 + 18 * 1, new ItemStack(AWNpcItemLoader.woodenShield), this);
        slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false).addTooltip("guistrings.npc.shield_slot");
        addGuiElement(slot);

        slot = new ItemSlot(26, 8 + 18 * 2, new ItemStack(Items.iron_helmet), this);
        slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false).addTooltip("guistrings.npc.helmet_slot");
        addGuiElement(slot);

        slot = new ItemSlot(26, 8 + 18 * 3, new ItemStack(Items.iron_chestplate), this);
        slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false).addTooltip("guistrings.npc.chest_slot");
        addGuiElement(slot);

        slot = new ItemSlot(26, 8 + 18 * 4, new ItemStack(Items.iron_leggings), this);
        slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false).addTooltip("guistrings.npc.legs_slot");
        addGuiElement(slot);

        slot = new ItemSlot(26, 8 + 18 * 5, new ItemStack(Items.iron_boots), this);
        slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false).addTooltip("guistrings.npc.boots_slot");
        addGuiElement(slot);


        slot = new ItemSlot(28 + 18 * 2, 8 + 18 * 2, new ItemStack(AWItems.upkeepOrder), this);
        slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false).addTooltip("guistrings.npc.upkeep_order_slot");
        addGuiElement(slot);

        slot = new ItemSlot(28 + 18 * 2, 8 + 18 * 3, new ItemStack(AWItems.routingOrder), this);
        slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false).addTooltip("guistrings.npc.manual_order_slot");
        addGuiElement(slot);
    }

    @Override
    protected boolean onGuiCloseRequested() {
        getContainer().setName();
        return super.onGuiCloseRequested();
    }

    @Override
    public void setupElements() {
        nameInput.setText(getContainer().entity.getCustomNameTag());
    }

}
