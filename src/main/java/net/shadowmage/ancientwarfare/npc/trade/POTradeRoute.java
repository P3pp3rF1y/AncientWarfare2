package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public final class POTradeRoute {

    private List<POTradePoint> route = new ArrayList<>();

    public int size() {
        return route.size();
    }

    public POTradePoint get(int index) {
        return route.get(index);
    }

    public void decrementRoutePoint(int index) {
        if (index <= 0 || index >= route.size()) {
            return;
        }
        POTradePoint p = route.remove(index);
        route.add(index - 1, p);
    }

    public void incrementRoutePoint(int index) {
        if (index < 0 || index >= route.size() - 1) {
            return;
        }
        POTradePoint p = route.remove(index);
        route.add(index + 1, p);
    }

    public void deleteRoutePoint(int index) {
        if (index < 0 || index >= route.size()) {
            return;
        }
        route.remove(index);
    }

    public void addRoutePoint(BlockPos pos) {
        POTradePoint p = new POTradePoint();
        p.position = pos;
        p.delay = 20 * 60;
        p.shouldUpkeep = false;
        route.add(p);
    }

    public void setPointDelay(int index, int delay) {
        route.get(index).setDelay(delay);
    }

    public void setUpkeep(int index, boolean val) {
        route.get(index).setShouldUpkeep(val);
    }

    public void readFromNBT(NBTTagCompound tag) {
        route.clear();
        NBTTagList list = tag.getTagList("route", Constants.NBT.TAG_COMPOUND);
        POTradePoint p;
        for (int i = 0; i < list.tagCount(); i++) {
            p = new POTradePoint();
            p.readFromNBT(list.getCompoundTagAt(i));
            route.add(p);
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        for (POTradePoint point : route) {
            list.appendTag(point.writeToNBT(new NBTTagCompound()));
        }
        tag.setTag("route", list);
        return tag;
    }

}
