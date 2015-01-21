package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureSettings;

public class ContainerStructureSelection extends ContainerStructureSelectionBase {

    ItemStructureSettings buildSettings = new ItemStructureSettings();

    public ContainerStructureSelection(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        ItemStack stack = player.inventory.getCurrentItem();
        ItemStructureSettings.getSettingsFor(stack, buildSettings);
        structureName = buildSettings.hasName() ? buildSettings.name() : null;
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (!player.worldObj.isRemote && tag.hasKey("structName")) {
            ItemStructureSettings.getSettingsFor(player.inventory.getCurrentItem(), buildSettings);
            buildSettings.setName(tag.getString("structName"));
            ItemStructureSettings.setSettingsFor(player.inventory.getCurrentItem(), buildSettings);
        }
    }

}
