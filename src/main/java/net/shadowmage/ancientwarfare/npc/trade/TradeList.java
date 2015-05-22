package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.interfaces.INBTSerialable;
import net.shadowmage.ancientwarfare.core.util.OrderingList;

/**
 * Created by Olivier on 12/05/2015.
 */
public class TradeList extends OrderingList<Trade> implements INBTSerialable{

    public final void addNewTrade() {
        add(getNewTrade());
    }

    protected Trade getNewTrade(){
        return new POTrade();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        for (Trade aTrade : this.points) {
            list.appendTag(aTrade.writeToNBT(new NBTTagCompound()));
        }
        tag.setTag("tradeList", list);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        clear();
        NBTTagList list = tag.getTagList("tradeList", Constants.NBT.TAG_COMPOUND);
        Trade t;
        for (int i = 0; i < list.tagCount(); i++) {
            t = getNewTrade();
            t.readFromNBT(list.getCompoundTagAt(i));
            add(t);
        }
    }

    public void performTrade(EntityPlayer player, IInventory storage, int integer) {
        get(integer).performTrade(player, storage);
    }
}
