package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface.ItemKey;

public class PacketItemInteraction extends PacketBase {

    private byte type, key;

    public PacketItemInteraction() {

    }

    public PacketItemInteraction(int type) {
        this.type = (byte) type;
    }

    public PacketItemInteraction(int type, ItemKey iKey) {
        this.type = (byte) type;
        this.key = (byte) iKey.ordinal();
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
    }

    @Override
    protected void execute(EntityPlayer player) {
        if (player != null) {
            @Nonnull ItemStack stack = player.getHeldItem();
            if (stack.isEmpty())
                return;
            if (type == 0) {
                if (key >= 0 && key < ItemKey.values().length && stack.getItem() instanceof IItemKeyInterface) {
                    ((IItemKeyInterface) stack.getItem()).onKeyAction(player, stack, ItemKey.values()[key]);
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
