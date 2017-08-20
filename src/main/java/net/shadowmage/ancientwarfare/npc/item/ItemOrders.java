package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;

import java.util.Collection;
import java.util.List;

public abstract class ItemOrders extends Item implements IItemKeyInterface {

    public ItemOrders() {
        this.setCreativeTab(AWNpcItemLoader.npcTab);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        list.add(I18n.format("guistrings.npc.orders.open_gui"));
        String key = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_0);
        list.add(I18n.format("guistrings.npc.orders.add_position", key));
    }

    @Override
    public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key) {
        return key == ItemKey.KEY_0;
    }

    public abstract Collection<? extends BlockPos> getPositionsForRender(ItemStack stack);

    public void addMessage(EntityPlayer player){
        player.addChatComponentMessage(new TextComponentTranslation("guistrings.npc.orders.position_added"));
    }
}
