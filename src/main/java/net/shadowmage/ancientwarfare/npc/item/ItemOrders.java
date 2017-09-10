package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemOrders extends Item implements IItemKeyInterface {

    public ItemOrders() {
        this.setCreativeTab(AWNpcItemLoader.npcTab);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("guistrings.npc.orders.open_gui"));
        String key = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_0);
        tooltip.add(I18n.format("guistrings.npc.orders.add_position", key));
    }

    @Override
    public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key) {
        return key == ItemKey.KEY_0;
    }

    public abstract List<BlockPos> getPositionsForRender(ItemStack stack);

    public void addMessage(EntityPlayer player){
        player.sendMessage(new TextComponentTranslation("guistrings.npc.orders.position_added"));
    }
}
