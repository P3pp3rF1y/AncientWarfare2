package net.shadowmage.ancientwarfare.core.proxy;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class CommonProxyBase {

    public void registerClient() {
        //NOOP for commonProxy
    }

    public EntityPlayer getClientPlayer() {
        //NOOP for commonProxy
        return null;
    }

    public EntityPlayer getFakePlayer(World world, String name) {
        if(name!=null) {
            EntityPlayer player = world.getPlayerEntityByName(name);
            if(player!=null){
                return player;
            }
            return FakePlayerFactory.get((WorldServer) world, new GameProfile(null, name));
        }
        return FakePlayerFactory.get((WorldServer) world, new GameProfile(null, "AncientWarfare"));
    }

    public boolean isKeyPressed(String keyName) {
        return false;
    }

    public void onConfigChanged() {

    }

    public World getWorld(int dimension) {
        return MinecraftServer.getServer().worldServerForDimension(dimension);
    }
}
