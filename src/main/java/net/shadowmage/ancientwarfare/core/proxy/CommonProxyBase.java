package net.shadowmage.ancientwarfare.core.proxy;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.UUID;

public class CommonProxyBase {

    public void addClientRegistrar(IClientRegistrar registrar) {
        //NOOP for commonProxy
    }

    public void preInit() {

    }

    public void init() {

    }

    public EntityPlayer getClientPlayer() {
        //NOOP for commonProxy
        return null;
    }

    public EntityPlayer getFakePlayer(World world, String name, UUID id) {
        EntityPlayer player;
        if(id!=null) {
            player = world.getPlayerEntityByUUID(id);
            if(player!=null)
                return player;
        }
        if(name!=null) {
            player = world.getPlayerEntityByName(name);
            if(player!=null){
                return player;
            }
            return FakePlayerFactory.get((WorldServer) world, new GameProfile(id, name));
        }
        return FakePlayerFactory.get((WorldServer) world, new GameProfile(id, "AncientWarfare"));
    }

    public boolean isKeyPressed(String keyName) {
        return false;
    }

    public void onConfigChanged() {

    }
}
