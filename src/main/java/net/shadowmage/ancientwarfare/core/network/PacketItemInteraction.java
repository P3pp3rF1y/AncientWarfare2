package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface.ItemKey;

public class PacketItemInteraction extends PacketBase {

    byte type = 0;
    byte key = 0;
    ItemKey iKey;

    public PacketItemInteraction() {

    }

    public PacketItemInteraction(int type) {
        this.type = (byte) type;
    }

    public PacketItemInteraction(int type, ItemKey iKey) {
        this.type = (byte) type;
        this.key = (byte) iKey.ordinal();
        this.iKey = iKey;
    }

    @Override
    protected void writeToStream(ByteBuf data) {
        data.writeByte(type);
        data.writeByte(key);
    }

    @Override
    protected void readFromStream(ByteBuf data) {
        type = data.readByte();
        key = data.readByte();
        iKey = ItemKey.values()[key];
    }

    @Override
    protected void execute() {
        if (player != null && player.inventory.getCurrentItem() != null) {
            ItemStack stack = player.inventory.getCurrentItem();
            if (type == 0) {
                if (stack.getItem() instanceof IItemKeyInterface) {
                    IItemKeyInterface interf = (IItemKeyInterface) player.inventory.getCurrentItem().getItem();
                    interf.onKeyAction(player, player.inventory.getCurrentItem(), iKey);
                }
            } else if (type == 1)//item left-click
            {
                if (stack.getItem() instanceof IItemClickable) {
                    ((IItemClickable) stack.getItem()).onLeftClick(player, stack);
                }
            } else if (type == 2)//item right-click
            {
                if (stack.getItem() instanceof IItemClickable) {
                    ((IItemClickable) stack.getItem()).onRightClick(player, stack);
                }
            }
        }
    }

}
