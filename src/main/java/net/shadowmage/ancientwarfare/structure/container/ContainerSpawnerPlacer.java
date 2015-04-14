package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketGui;
import net.shadowmage.ancientwarfare.structure.item.ItemSpawnerPlacer;

public class ContainerSpawnerPlacer extends ContainerBase {


    public String entityId;
    /**
     * all stored in tag as short
     */
    public int delay;
    public int minSpawnDelay;
    public int maxSpawnDelay;
    public int spawnCount;
    public int maxNearbyEntities;
    public int requiredPlayerRange;
    public int spawnRange;

    /**
     * par1NBTTagCompound.setString("EntityId", this.getEntityNameToSpawn());
     * par1NBTTagCompound.setShort("Delay", (short)this.spawnDelay);
     * par1NBTTagCompound.setShort("MinSpawnDelay", (short)this.minSpawnDelay);
     * par1NBTTagCompound.setShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
     * par1NBTTagCompound.setShort("SpawnCount", (short)this.spawnCount);
     * par1NBTTagCompound.setShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
     * par1NBTTagCompound.setShort("RequiredPlayerRange", (short)this.activatingRangeFromPlayer);
     * par1NBTTagCompound.setShort("SpawnRange", (short)this.spawnRange);
     */

    public ContainerSpawnerPlacer(EntityPlayer player, int x, int y, int z) {
        super(player);
        ItemStack stack = player.inventory.getCurrentItem();
        if (isInValid(stack)) {
            throw new IllegalArgumentException("Incorrect held item");
        }
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("spawnerData")) {
            entityId = "Pig";
            delay = 20;
            minSpawnDelay = 800;
            maxSpawnDelay = 800;
            spawnCount = 4;
            maxNearbyEntities = 6;
            requiredPlayerRange = 16;
            spawnRange = 4;
        } else {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag("spawnerData");
            entityId = tag.getString("EntityId");
            delay = tag.getShort("Delay");
            minSpawnDelay = tag.getShort("MinSpawnDelay");
            maxSpawnDelay = tag.getShort("MaxSpawnDelay");
            spawnCount = tag.getShort("SpawnCount");
            maxNearbyEntities = tag.getShort("MaxNearbyEntities");
            requiredPlayerRange = tag.getShort("RequiredPlayerRange");
            spawnRange = tag.getShort("SpawnRange");
            /**
             * TODO add input fields for 'custom mob data'
             */
        }
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("spawnerData")) {
            ItemStack stack = player.inventory.getCurrentItem();
            if (isInValid(stack)) {
                return;
            }
            stack.setTagInfo("spawnerData", tag.getCompoundTag("spawnerData"));
            detectAndSendChanges();
        }
    }

    /**
     * onGuiClose -- called from client-side to send stored data to server to imprint on item
     */
    public void sendDataToServer() {
        NBTTagCompound tag2 = new NBTTagCompound();
        NBTTagCompound tag = new NBTTagCompound();

        tag.setString("EntityId", this.entityId);
        tag.setShort("Delay", (short) this.delay);
        tag.setShort("MinSpawnDelay", (short) this.minSpawnDelay);
        tag.setShort("MaxSpawnDelay", (short) this.maxSpawnDelay);
        tag.setShort("SpawnCount", (short) this.spawnCount);
        tag.setShort("MaxNearbyEntities", (short) this.maxNearbyEntities);
        tag.setShort("RequiredPlayerRange", (short) this.requiredPlayerRange);
        tag.setShort("SpawnRange", (short) this.spawnRange);

        tag2.setTag("spawnerData", tag);
        PacketGui pkt = new PacketGui();
        pkt.packetData = tag2;
        NetworkHandler.sendToServer(pkt);
    }

    private boolean isInValid(ItemStack stack) {
        return stack == null || stack.getItem() == null || !(stack.getItem() instanceof ItemSpawnerPlacer);
    }
}
