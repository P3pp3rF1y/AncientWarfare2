package net.shadowmage.ancientwarfare.structure.gamedata;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

import java.util.ArrayList;
import java.util.List;
//TODO world capability
public class TownMap extends WorldSavedData {

    private List<StructureBB> boundingBoxes = new ArrayList<>();

    public TownMap(String name) {
        super(name);
    }

    public void setGenerated(StructureBB bb) {
        boundingBoxes.add(bb);
        markDirty();
    }

    /*
     * return the distance of the closest found town or defaultVal if no town was found closer
     */
    public float getClosestTown(int bx, int bz, float defaultVal) {
        float distance = defaultVal;
        float d;
        if (boundingBoxes!=null) {
            for (StructureBB bb : boundingBoxes) {
                d = Trig.getDistance(bx, 0, bz, bb.getCenterX(), 0, bb.getCenterZ());
                if (d < distance) {
                    distance = d;
                }
            }
        }
        return distance;
    }

    public boolean isChunkInUse(int cx, int cz) {
        if (!boundingBoxes.isEmpty()) {
            cx *= 16;
            cz *= 16;
            for (StructureBB bb : boundingBoxes) {
                if (bb.isPositionIn(cx, bb.min.getY(), cz)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean intersectsWithTown(StructureBB bb) {
        for (StructureBB tbb : boundingBoxes) {
            if (tbb.crossWith(bb)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        StructureBB bb;
        NBTTagList list = tag.getTagList("boundingBoxes", Constants.NBT.TAG_COMPOUND);
        boundingBoxes.clear();
        for (int i = 0; i < list.tagCount(); i++) {
            bb = new StructureBB(BlockPos.ORIGIN, BlockPos.ORIGIN);
            bb.deserializeNBT(list.getCompoundTagAt(i));
            boundingBoxes.add(bb);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        for (StructureBB bb : boundingBoxes) {
            list.appendTag(bb.serializeNBT());
        }
        tag.setTag("boundingBoxes", list);
        return tag;
    }

}
