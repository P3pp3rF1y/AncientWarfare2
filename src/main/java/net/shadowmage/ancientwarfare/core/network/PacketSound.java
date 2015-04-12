package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class PacketSound extends PacketBase {

    double x, y, z;
    String sound;

    public PacketSound(double x, double y, double z, String sound) {

    }

    public PacketSound() {

    }

    @Override
    protected void writeToStream(ByteBuf data) {
        data.writeDouble(x);
        data.writeDouble(y);
        data.writeDouble(z);
        byte[] b = sound.getBytes();
        data.writeShort(b.length);
        data.writeBytes(b);
    }

    @Override
    protected void readFromStream(ByteBuf data) {
        x = data.readDouble();
        y = data.readDouble();
        z = data.readDouble();
        short len = data.readShort();
        byte[] datas = new byte[len];
        data.readBytes(datas);
        sound = new String(datas);
    }

    @Override
    protected void execute(EntityPlayer player) {
        player.worldObj.playSound(x, y, z, sound, 1.f, 1.f, false);
    }

}
