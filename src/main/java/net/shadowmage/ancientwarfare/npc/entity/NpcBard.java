package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.interfaces.ISinger;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.SongPlayData;
import net.shadowmage.ancientwarfare.npc.ai.*;
import net.shadowmage.ancientwarfare.npc.ai.owned.*;

public class NpcBard extends NpcPlayerOwned implements ISinger {

    SongPlayData tuneData = new SongPlayData();

    public NpcBard(World par1World) {
        super(par1World);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(0, new NpcAIDoor(this, true));
        this.tasks.addTask(0, (horseAI = new NpcAIPlayerOwnedRideHorse(this)));
        this.tasks.addTask(2, new NpcAIFollowPlayer(this));
        this.tasks.addTask(2, new NpcAIPlayerOwnedFollowCommand(this));
        this.tasks.addTask(3, new NpcAIFleeHostiles(this));
        this.tasks.addTask(3, new NpcAIPlayerOwnedAlarmResponse(this));
        this.tasks.addTask(4, new NpcAIPlayerOwnedGetFood(this));
        this.tasks.addTask(5, new NpcAIPlayerOwnedIdleWhenHungry(this));

        this.tasks.addTask(7, new NpcAIMoveHome(this, 50F, 3F, 30F, 3F));
        this.tasks.addTask(8, new NpcAISing(this));

        //post-100 -- used by delayed shared tasks (look at random stuff, wander)
        this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(102, new NpcAIWander(this));
        this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
    }

    @Override
    public SongPlayData getSongs() {
        return tuneData;
    }

    @Override
    public String getNpcSubType() {
        return "";
    }

    @Override
    public String getNpcType() {
        return "bard";
    }

    @Override
    public boolean hasAltGui() {
        return true;
    }

    @Override
    public void openAltGui(EntityPlayer player) {
        NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_BARD, getEntityId(), 0, 0);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        tuneData.readFromNBT(tag.getCompoundTag("tuneData"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setTag("tuneData", tuneData.writeToNBT(new NBTTagCompound()));
    }

}
