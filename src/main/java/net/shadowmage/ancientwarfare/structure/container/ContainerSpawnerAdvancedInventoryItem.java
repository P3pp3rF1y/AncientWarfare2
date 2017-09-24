package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockAdvancedSpawner;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings;

import javax.annotation.Nonnull;

public class ContainerSpawnerAdvancedInventoryItem extends ContainerSpawnerAdvancedInventoryBase {

    public ContainerSpawnerAdvancedInventoryItem(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);

        settings = new SpawnerSettings();
        @Nonnull ItemStack item = EntityTools.getItemFromEitherHand(player, ItemBlockAdvancedSpawner.class);
        if (item.isEmpty() || !item.hasTagCompound() || !item.getTagCompound().hasKey("spawnerSettings")) {
            throw new IllegalArgumentException("stack cannot be null, and must have tag compounds!!");
        }
        settings.readFromNBT(item.getTagCompound().getCompoundTag("spawnerSettings"));
        inventory = settings.getInventory();

        this.addSettingsInventorySlots();
        this.addPlayerSlots(8, 70, 8);
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("spawnerSettings")) {
            @Nonnull ItemStack item = EntityTools.getItemFromEitherHand(player, ItemBlockAdvancedSpawner.class);
            item.setTagInfo("spawnerSettings", tag.getCompoundTag("spawnerSettings"));
        }
    }

}
