package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.item.ItemBlockBase;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings.EntitySpawnGroup;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings.EntitySpawnSettings;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockAdvancedSpawner extends ItemBlockBase implements IItemKeyInterface {
	private static final String SPAWNER_SETTINGS_TAG = "spawnerSettings";

	public ItemBlockAdvancedSpawner(Block block) {
		super(block);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		//noinspection ConstantConditions
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(SPAWNER_SETTINGS_TAG)) {
			SpawnerSettings settings = SpawnerSettings.getDefaultSettings();
			NBTTagCompound defaultTag = new NBTTagCompound();
			settings.writeToNBT(defaultTag);
			stack.setTagInfo(SPAWNER_SETTINGS_TAG, defaultTag);
		}
		boolean val = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
		if (!world.isRemote && val) {
			WorldTools.getTile(world, pos, TileAdvancedSpawner.class).ifPresent(t -> {
				SpawnerSettings settings = new SpawnerSettings();
				settings.readFromNBT(stack.getTagCompound().getCompoundTag(SPAWNER_SETTINGS_TAG));
				t.setSettings(settings);
			});
		}
		return val;
	}

	@Override
	public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		return altFunction == ItemAltFunction.ALT_FUNCTION_1;
	}

	@Override
	public void onKeyAction(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		if (player.isSneaking()) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SPAWNER_ADVANCED_INVENTORY, 0, 0, 0);
		} else {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SPAWNER_ADVANCED, 0, 0, 0);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
		//noinspection ConstantConditions
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(SPAWNER_SETTINGS_TAG)) {
			tooltip.add(I18n.format("guistrings.corrupt_item"));
			return;
		}
		SpawnerSettings tooltipSettings = new SpawnerSettings();
		tooltipSettings.readFromNBT(stack.getTagCompound().getCompoundTag(SPAWNER_SETTINGS_TAG));
		List<EntitySpawnGroup> groups = tooltipSettings.getSpawnGroups();
		tooltip.add(I18n.format("guistrings.spawner.group_count") + ": " + groups.size());
		EntitySpawnGroup group;
		for (int i = 0; i < groups.size(); i++) {
			group = groups.get(i);
			tooltip.add(I18n.format("guistrings.spawner.group_number") + ": " + (i + 1) + " " + I18n.format("guistrings.spawner.group_weight") + ": " + group.getWeight());
			for (EntitySpawnSettings set : group.getEntitiesToSpawn()) {
				tooltip.add("  " + I18n.format("guistrings.spawner.entity_type") + ": " + I18n.format(set.getEntityName()) + " " + set.getSpawnMin() + " to " + set.getSpawnMax() + " (" + (set.getSpawnTotal() < 0 ? "infinite" : set.getSpawnTotal()) + " total)");
			}
		}
	}
}
