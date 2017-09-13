package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings.EntitySpawnGroup;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings.EntitySpawnSettings;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockAdvancedSpawner extends ItemBlock implements IItemKeyInterface {

    public ItemBlockAdvancedSpawner(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("spawnerSettings")) {
            SpawnerSettings settings = SpawnerSettings.getDefaultSettings();
            NBTTagCompound defaultTag = new NBTTagCompound();
            settings.writeToNBT(defaultTag);
            stack.setTagInfo("spawnerSettings", defaultTag);
        }
        boolean val = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (!world.isRemote && val) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileAdvancedSpawner) {
                TileAdvancedSpawner tile = (TileAdvancedSpawner) te;
                SpawnerSettings settings = new SpawnerSettings();
                settings.readFromNBT(stack.getTagCompound().getCompoundTag("spawnerSettings"));
                tile.setSettings(settings);
            }
        }
        return val;
    }

    @Override
    public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key) {
        return key == ItemKey.KEY_0;
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        if (player.isSneaking()) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SPAWNER_ADVANCED_INVENTORY, 0, 0, 0);
        } else {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SPAWNER_ADVANCED, 0, 0, 0);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("spawnerSettings")) {
            tooltip.add(I18n.format("guistrings.corrupt_item"));
            return;
        }
        SpawnerSettings tooltipSettings = new SpawnerSettings();
        tooltipSettings.readFromNBT(stack.getTagCompound().getCompoundTag("spawnerSettings"));
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


    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs creativeTab, NonNullList<ItemStack> stackList) {
        if (creativeTab != AWStructuresItemLoader.structureTab) {
            return;
        }

        @Nonnull ItemStack stack = new ItemStack(this.getBlock());
        SpawnerSettings settings = SpawnerSettings.getDefaultSettings();
        NBTTagCompound defaultTag = new NBTTagCompound();
        settings.writeToNBT(defaultTag);
        stack.setTagInfo("spawnerSettings", defaultTag);
        stackList.add(stack);
    }

}
