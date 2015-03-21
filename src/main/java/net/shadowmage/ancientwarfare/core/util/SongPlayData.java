package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class SongPlayData {
    private boolean random = false;
    private boolean playOnPlayerEntry = false;
    private int minDelay;
    private int maxDelay;
    private List<SongEntry> tunes = new ArrayList<SongEntry>();

    public int size() {
        return tunes.size();
    }

    public SongEntry get(int index) {
        return tunes.get(index);
    }

    public void addNewEntry() {
        SongEntry e = new SongEntry();
        tunes.add(e);
    }

    public void decrementEntry(int index) {
        if (index <= 0 || index >= tunes.size()) {
            return;
        }
        SongEntry e = tunes.remove(index);
        index--;
        tunes.add(index, e);
    }

    public void incrementEntry(int index) {
        if (index < 0 || index >= tunes.size() - 1) {
            return;
        }
        SongEntry e = tunes.remove(index);
        index++;
        tunes.add(index, e);
    }

    public void deleteEntry(int index) {
        if (index < 0 || index >= tunes.size()) {
            return;
        }
        tunes.remove(index);
    }

    public int getMinDelay() {
        return minDelay;
    }

    public int getMaxDelay() {
        return maxDelay;
    }

    public boolean getPlayOnPlayerEntry() {
        return playOnPlayerEntry;
    }

    public boolean getIsRandom() {
        return random;
    }

    public void setMinDelay(int val) {
        minDelay = val;
    }

    public void setMaxDelay(int val) {
        maxDelay = val;
    }

    public void setPlayOnPlayerEntry(boolean val) {
        playOnPlayerEntry = val;
    }

    public void setRandom(boolean val) {
        random = val;
    }

    public void readFromNBT(NBTTagCompound tag) {
        minDelay = tag.getInteger("minDelay");
        maxDelay = tag.getInteger("maxDelay");
        random = tag.getBoolean("random");
        playOnPlayerEntry = tag.getBoolean("playerEntry");
        tunes.clear();
        SongEntry d;
        NBTTagList l = tag.getTagList("entries", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < l.tagCount(); i++) {
            d = new SongEntry();
            d.readFromNBT(l.getCompoundTagAt(i));
            tunes.add(d);
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setInteger("minDelay", minDelay);
        tag.setInteger("maxDelay", maxDelay);
        tag.setBoolean("random", random);
        tag.setBoolean("playerEntry", playOnPlayerEntry);
        NBTTagList l = new NBTTagList();
        for (SongEntry tune : tunes) {
            l.appendTag(tune.writeToNBT(new NBTTagCompound()));
        }
        tag.setTag("entries", l);
        return tag;
    }

    public static final class SongEntry {
        String name;
        float length;//length in seconds, used to determine when count down for next tune should start
        int volume;// percentage, as integer 0 = 0%, 100=100%, 150=150%
        boolean isRecord;

        public SongEntry() {
            name = "";
            length = 0;
            volume = 100;
        }

        public void setLength(float length) {
            this.length = length;
        }

        public void setName(String name) {
            this.name = name == null ? " " : name;
        }

        public void setVolume(int volume) {
            this.volume = volume;
        }

        public int volume() {
            return volume;
        }

        public String name() {
            return name;
        }

        public float length() {
            return length;
        }

        public void readFromNBT(NBTTagCompound tag) {
            name = "";
            if (tag.hasKey("name")) {
                name = tag.getString("name");
            }
            length = tag.getFloat("length");
            volume = tag.getInteger("volume");
        }

        public NBTTagCompound writeToNBT(NBTTagCompound tag) {
            if (name != null && !name.isEmpty()) {
                tag.setString("name", name);
            }
            tag.setFloat("length", length);
            tag.setInteger("volume", volume);
            return tag;
        }

        public int play(World world, int x, int y, int z){
            if(isRecord)
                world.playRecord(name, x, y, z);
            else
                world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, name, volume * 0.03F, 1);
            return (int) (length * 1200);//minutes(decimal) to ticks conversion
        }
    }


}