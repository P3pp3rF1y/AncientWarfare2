package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.*;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcInventory;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;

public class GuiNpcInventory extends GuiNpcBase {

    Button repackButton;
    Text nameInput;
    ContainerNpcInventory container;

    Text textureInput;

    int buttonX = 8 + 18 + 18 + 18 + 18 + 4;

    public GuiNpcInventory(ContainerBase container) {
        super(container);
        this.xSize = 178;
        this.ySize = ((ContainerNpcInventory) container).guiHeight;
        this.container = (ContainerNpcInventory) container;
    }

    @Override
    public void initElements() {
        Label label = new Label(8 + 18 + 18 + 4, 9, "guistrings.npc.npc_name");
        addGuiElement(label);

        nameInput = new Text(75, 8, 95, container.npc.getCustomNameTag(), this) {
            @Override
            public void onTextUpdated(String oldText, String newText) {
                container.handleNpcNameUpdate(newText);
            }
        };
        addGuiElement(nameInput);

        label = new Label(8 + 18 + 18 + 4, 21, "guistrings.npc.npc_texture");
        addGuiElement(label);
        textureInput = new Text(75, 20, 95, container.npc.getCustomTex(), this) {
            @Override
            public void onTextUpdated(String oldText, String newText) {
                container.handleNpcTextureUpdate(newText);
                container.npc.setCustomTexRef(newText);
            }
        };
        addGuiElement(textureInput);

        repackButton = new Button(buttonX, 36, 75, 12, "guistrings.npc.repack") {
            @Override
            protected void onPressed() {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setBoolean("repack", true);
                sendDataToContainer(tag);
                closeGui();
            }
        };
        addGuiElement(repackButton);

        Button button = new Button(buttonX, 48, 75, 12, "guistrings.npc.set_home") {
            @Override
            protected void onPressed() {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setBoolean("setHome", true);
                sendDataToContainer(tag);
            }
        };
        addGuiElement(button);

        button = new Button(buttonX, 60, 75, 12, "guistrings.npc.clear_home") {
            @Override
            protected void onPressed() {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setBoolean("clearHome", true);
                sendDataToContainer(tag);
            }
        };
        addGuiElement(button);

        if (container.npc.hasAltGui()) {
            button = new Button(buttonX, 72, 75, 12, "guistrings.npc.alt_gui") {
                @Override
                protected void onPressed() {
                    container.npc.openAltGui(player);
                }
            };
            addGuiElement(button);
        }

        if (player.capabilities.isCreativeMode) {
            button = new Button(buttonX, 84, 75, 12, "guistrings.npc.creative_gui") {
                @Override
                protected void onPressed() {
                    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_CREATIVE, container.npc.getEntityId(), 0, 0);
                }
            };
            addGuiElement(button);
        }

        ItemSlot slot;
        Tooltip t;
        String text;
        int tw;

        slot = new ItemSlot(26, 8, new ItemStack(Items.iron_sword), this);
        slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false);
        text = "guistrings.npc.weapon_slot";
        tw = fontRendererObj.getStringWidth(text);
        t = new Tooltip(tw, 10);
        t.addTooltipElement(new Label(0, 0, text));
        slot.setTooltip(t);
        addGuiElement(slot);

        slot = new ItemSlot(26, 8 + 18 * 1, new ItemStack(AWNpcItemLoader.woodenShield), this);
        slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false);
        text = "guistrings.npc.shield_slot";
        tw = fontRendererObj.getStringWidth(text);
        t = new Tooltip(tw, 10);
        t.addTooltipElement(new Label(0, 0, text));
        slot.setTooltip(t);
        addGuiElement(slot);

        slot = new ItemSlot(26, 8 + 18 * 2, new ItemStack(Items.iron_helmet), this);
        slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false);
        text = "guistrings.npc.helmet_slot";
        tw = fontRendererObj.getStringWidth(text);
        t = new Tooltip(tw, 10);
        t.addTooltipElement(new Label(0, 0, text));
        slot.setTooltip(t);
        addGuiElement(slot);

        slot = new ItemSlot(26, 8 + 18 * 3, new ItemStack(Items.iron_chestplate), this);
        slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false);
        text = "guistrings.npc.chest_slot";
        tw = fontRendererObj.getStringWidth(text);
        t = new Tooltip(tw, 10);
        t.addTooltipElement(new Label(0, 0, text));
        slot.setTooltip(t);
        addGuiElement(slot);

        slot = new ItemSlot(26, 8 + 18 * 4, new ItemStack(Items.iron_leggings), this);
        slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false);
        text = "guistrings.npc.legs_slot";
        tw = fontRendererObj.getStringWidth(text);
        t = new Tooltip(tw, 10);
        t.addTooltipElement(new Label(0, 0, text));
        slot.setTooltip(t);
        addGuiElement(slot);

        slot = new ItemSlot(26, 8 + 18 * 5, new ItemStack(Items.iron_boots), this);
        slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false);
        text = "guistrings.npc.boots_slot";
        tw = fontRendererObj.getStringWidth(text);
        t = new Tooltip(tw, 10);
        t.addTooltipElement(new Label(0, 0, text));
        slot.setTooltip(t);
        addGuiElement(slot);


        slot = new ItemSlot(28 + 18 * 2, 8 + 18 * 2, new ItemStack(AWNpcItemLoader.upkeepOrder), this);
        slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false);
        text = "guistrings.npc.upkeep_order_slot";
        tw = fontRendererObj.getStringWidth(text);
        t = new Tooltip(tw, 10);
        t.addTooltipElement(new Label(0, 0, text));
        slot.setTooltip(t);
        addGuiElement(slot);

        slot = new ItemSlot(28 + 18 * 2, 8 + 18 * 3, new ItemStack(AWNpcItemLoader.routingOrder), this);
        slot.setRenderSlotBackground(false).setRenderItemQuantity(false).setHighlightOnMouseOver(false);
        text = "guistrings.npc.manual_order_slot";
        tw = fontRendererObj.getStringWidth(text);
        t = new Tooltip(tw, 10);
        t.addTooltipElement(new Label(0, 0, text));
        slot.setTooltip(t);
        addGuiElement(slot);
    }

    @Override
    protected boolean onGuiCloseRequested() {
        if (player.worldObj.isRemote) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("customName", container.name);
            sendDataToContainer(tag);
        }
        return super.onGuiCloseRequested();
    }

    @Override
    public void setupElements() {
        nameInput.setText(container.npc.getCustomNameTag());
    }

}
