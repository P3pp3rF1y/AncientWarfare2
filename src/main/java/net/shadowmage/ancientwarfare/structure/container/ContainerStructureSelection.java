package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureSettings;

public class ContainerStructureSelection extends ContainerStructureSelectionBase {

    ItemStructureSettings buildSettings;

    public ContainerStructureSelection(EntityPlayer player, int x, int y, int z) {
        super(player);
        ItemStack stack = player.getHeldItem();
        buildSettings = ItemStructureSettings.getSettingsFor(stack);
        structureName = buildSettings.hasName() ? buildSettings.name() : null;
        addPlayerSlots();
        removeSlots();
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (!player.worldObj.isRemote && tag.hasKey("structName")) {
            buildSettings = ItemStructureSettings.getSettingsFor(player.getHeldItem());
            buildSettings.setName(tag.getString("structName"));
            ItemStructureSettings.setSettingsFor(player.getHeldItem(), buildSettings);
        }
    }

}
