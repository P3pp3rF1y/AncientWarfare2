package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.structure.item.ItemSpawnerPlacer;

import javax.annotation.Nonnull;

public class ContainerSpawnerPlacer extends ContainerBase {

    public String entityId;
    /*
     * all stored in tag as short
     */
    public int delay;
    public int minSpawnDelay;
    public int maxSpawnDelay;
    public int spawnCount;
    public int maxNearbyEntities;
    public int requiredPlayerRange;
    public int spawnRange;

    public ContainerSpawnerPlacer(EntityPlayer player, int x, int y, int z) {
        super(player);
        @Nonnull ItemStack stack = EntityTools.getItemFromEitherHand(player, ItemSpawnerPlacer.class);
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
            /*
             * TODO add input fields for 'custom mob data'
             */
        }
        addPlayerSlots();
        removeSlots();
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("spawnerData")) {
            @Nonnull ItemStack stack = EntityTools.getItemFromEitherHand(player, ItemSpawnerPlacer.class);
            if (isInValid(stack)) {
                return;
            }
            stack.setTagInfo("spawnerData", tag.getCompoundTag("spawnerData"));
            detectAndSendChanges();
        }
    }

    /*
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
        sendDataToServer(tag2);
    }

    private boolean isInValid(ItemStack stack) {
        return stack.isEmpty() || !(stack.getItem() instanceof ItemSpawnerPlacer);
    }
}
