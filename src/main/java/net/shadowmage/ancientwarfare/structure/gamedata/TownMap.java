package net.shadowmage.ancientwarfare.structure.gamedata;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

import java.util.ArrayList;
import java.util.List;

public class TownMap extends WorldSavedData {

    public static final String NAME = "AWTownMap";

    private List<StructureBB> boundingBoxes = new ArrayList<StructureBB>();

    public TownMap(String name) {
        super(name);
    }

    public TownMap() {
        this(NAME);
    }

    public void setGenerated(World world, StructureBB bb) {
        boundingBoxes.add(bb);
        markDirty();
    }

    /**
     * return the distance of the closest found town or defaultVal if no town was found closer
     */
    public float getClosestTown(World world, int bx, int bz, float defaultVal) {
        float distance = defaultVal;
        float d;
        List<StructureBB> bbs = boundingBoxes;
        if (bbs != null && !bbs.isEmpty()) {
            for (StructureBB bb : bbs) {
                d = Trig.getDistance(bx, 0, bz, bb.getCenterX(), 0, bb.getCenterZ());
                if (d < distance) {
                    distance = d;
                }
            }
        }
        return distance;
    }

    public boolean isChunkInUse(World world, int cx, int cz) {
        List<StructureBB> bbs = boundingBoxes;
        if (bbs != null && !bbs.isEmpty()) {
            cx *= 16;
            cz *= 16;
            for (StructureBB bb : bbs) {
                if (bb.isPositionInBoundingBox(cx, bb.min.y, cz)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean intersectsWithTown(World world, StructureBB bb) {
        for (StructureBB tbb : boundingBoxes) {
            if (tbb.collidesWith(bb)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        StructureBB bb;
        NBTTagList list = tag.getTagList("boundingBoxes", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            bb = new StructureBB(new BlockPosition(), new BlockPosition());
            bb.readFromNBT(list.getCompoundTagAt(i));
            boundingBoxes.add(bb);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        for (StructureBB bb : boundingBoxes) {
            list.appendTag(bb.writeToNBT(new NBTTagCompound()));
        }
        tag.setTag("boundingBoxes", list);
    }

}
