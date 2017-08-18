package net.shadowmage.ancientwarfare.structure.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings.EntitySpawnGroup;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings.EntitySpawnSettings;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;

import java.util.List;

public class ItemBlockAdvancedSpawner extends ItemBlock implements IItemKeyInterface {

    public ItemBlockAdvancedSpawner(Block p_i45328_1_) {
        super(p_i45328_1_);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("spawnerSettings")) {
            SpawnerSettings settings = SpawnerSettings.getDefaultSettings();
            NBTTagCompound defaultTag = new NBTTagCompound();
            settings.writeToNBT(defaultTag);
            stack.setTagInfo("spawnerSettings", defaultTag);
        }
        boolean val = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        List<String> list = (List<String>) par3List;
        if (!par1ItemStack.hasTagCompound() || !par1ItemStack.getTagCompound().hasKey("spawnerSettings")) {
            list.add(I18n.format("guistrings.corrupt_item"));
            return;
        }
        SpawnerSettings tooltipSettings = new SpawnerSettings();
        tooltipSettings.readFromNBT(par1ItemStack.getTagCompound().getCompoundTag("spawnerSettings"));
        List<EntitySpawnGroup> groups = tooltipSettings.getSpawnGroups();
        list.add(I18n.format("guistrings.spawner.group_count") + ": " + groups.size());
        EntitySpawnGroup group;
        for (int i = 0; i < groups.size(); i++) {
            group = groups.get(i);
            list.add(I18n.format("guistrings.spawner.group_number") + ": " + (i + 1) + " " + I18n.format("guistrings.spawner.group_weight") + ": " + group.getWeight());
            for (EntitySpawnSettings set : group.getEntitiesToSpawn()) {
                list.add("  " + I18n.format("guistrings.spawner.entity_type") + ": " + I18n.format(set.getEntityName()) + " " + set.getSpawnMin() + " to " + set.getSpawnMax() + " (" + (set.getSpawnTotal() < 0 ? "infinite" : set.getSpawnTotal()) + " total)");
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativeTab, List stackList) {
        ItemStack stack = new ItemStack(this.field_150939_a);
        SpawnerSettings settings = SpawnerSettings.getDefaultSettings();
        NBTTagCompound defaultTag = new NBTTagCompound();
        settings.writeToNBT(defaultTag);
        stack.setTagInfo("spawnerSettings", defaultTag);
        stackList.add(stack);
    }

}
