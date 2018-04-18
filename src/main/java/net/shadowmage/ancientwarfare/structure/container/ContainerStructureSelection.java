package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureBuilder;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureBuilderWorldGen;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureSettings;

public class ContainerStructureSelection extends ContainerStructureSelectionBase {

	private ItemStructureSettings buildSettings;

	public ContainerStructureSelection(EntityPlayer player, int x, int y, int z) {
		super(player);
		buildSettings = ItemStructureSettings.getSettingsFor(EntityTools.getItemFromEitherHand(player, ItemStructureBuilder.class, ItemStructureBuilderWorldGen.class));
		structureName = buildSettings.hasName() ? buildSettings.name() : null;
		addPlayerSlots();
		removeSlots();
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (!player.world.isRemote && tag.hasKey("structName")) {
			ItemStack stack = EntityTools.getItemFromEitherHand(player, ItemStructureBuilder.class, ItemStructureBuilderWorldGen.class);
			buildSettings = ItemStructureSettings.getSettingsFor(stack);
			buildSettings.setName(tag.getString("structName"));
			ItemStructureSettings.setSettingsFor(stack, buildSettings);
		}
	}

}
