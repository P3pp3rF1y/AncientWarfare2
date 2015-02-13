package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureSettings;

public class ContainerStructureSelection extends ContainerStructureSelectionBase {

    ItemStructureSettings buildSettings;

    public ContainerStructureSelection(EntityPlayer player, int x, int y, int z) {
        super(player);
        ItemStack stack = player.inventory.getCurrentItem();
        buildSettings = ItemStructureSettings.getSettingsFor(stack);
        structureName = buildSettings.hasName() ? buildSettings.name() : null;
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (!player.worldObj.isRemote && tag.hasKey("structName")) {
            buildSettings = ItemStructureSettings.getSettingsFor(player.inventory.getCurrentItem());
            buildSettings.setName(tag.getString("structName"));
            ItemStructureSettings.setSettingsFor(player.inventory.getCurrentItem(), buildSettings);
        }
    }

}
