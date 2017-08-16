package net.shadowmage.ancientwarfare.npc.item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;

import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.npc.entity.AWNPCEntityLoader;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.util.HashMap;
import java.util.List;

public class ItemNpcSpawner extends Item {
    /**
     * npc names are type.subtype :: resource-location
     */
    private HashMap<String, String> iconNames = new HashMap<String, String>();

    private HashMap<String, IIcon> iconMap = new HashMap<String, IIcon>();

    public ItemNpcSpawner() {
        this.setCreativeTab(AWNpcItemLoader.npcTab);
        this.setTextureName("ancientwarfare:npc/spawner_miner");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        list.add(StatCollector.translateToLocal("guistrings.npc.spawner.right_click_to_place"));
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        String npcName = getNpcType(par1ItemStack);
        if (npcName != null) {
            String npcSub = getNpcSubtype(par1ItemStack);
            if (!npcSub.isEmpty()) {
                npcName = npcName + "." + npcSub;
            }
            return "entity.AncientWarfareNpc." + npcName;
        }
        return super.getUnlocalizedName(par1ItemStack);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if(world.isRemote){
            return stack;
        }
        BlockPosition hit = BlockTools.getBlockClickedOn(player, player.worldObj, true);
        if (hit == null) {
            return stack;
        }
        NpcBase npc = createNpcFromItem(player.worldObj, stack);
        if (npc != null) {
            npc.setOwner(player);
            npc.setPosition(hit.x + 0.5d, hit.y, hit.z + 0.5d);
            npc.setHomeAreaAtCurrentPosition();
            player.worldObj.spawnEntityInWorld(npc);
            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return stack;
    }

    /**
     * create an NPC from the input item stack if valid, else return null<br>
     * npc will have type, subtype, equipment, levels, health, food and owner set from item.
     */
    public static NpcBase createNpcFromItem(World world, ItemStack stack) {
        String type = getNpcType(stack);
        if (type == null) {
            return null;
        }
        String subType = getNpcSubtype(stack);
        NpcBase npc = AWNPCEntityLoader.createNpc(world, type, subType);
        if (npc == null) {
            return null;
        }
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("npcStoredData")) {
            for (int i = 0; i < 5; i++) {
                npc.setCurrentItemOrArmor(i, null);
            }
            npc.readAdditionalItemData(stack.getTagCompound().getCompoundTag("npcStoredData"));
        }
        return npc;
    }

    /**
     * return an itemstack of npc spawner item that contains the data to spawn the input npc<br>
     * npc type, subtype, equipment, levels health, food value, and owner will be stored.
     */
    public static ItemStack getSpawnerItemForNpc(NpcBase npc) {
        String type = npc.getNpcType();
        String sub = npc.getNpcSubType();
        ItemStack stack = getStackForNpcType(type, sub);
        NBTTagCompound tag = new NBTTagCompound();
        npc.writeAdditionalItemData(tag);
        stack.setTagInfo("npcStoredData", tag);
        return stack;
    }

    @SuppressWarnings("rawtypes")
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        AWNPCEntityLoader.getSpawnerSubItems(list);
    }

    public static ItemStack getStackForNpcType(String type, String npcSubtype) {
        ItemStack stack = new ItemStack(AWItems.npcSpawner);
        stack.setTagInfo("npcType", new NBTTagString(type));
        stack.setTagInfo("npcSubtype", new NBTTagString(npcSubtype));
        return stack;
    }

    public static String getNpcType(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("npcType")) {
            return stack.getTagCompound().getString("npcType");
        }
        return null;
    }

    public static String getNpcSubtype(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("npcSubtype")) {
            return stack.getTagCompound().getString("npcSubtype");
        }
        return "";
    }

    /**
     * Npc type 'name' is full npc-type -- type.subtype
     */
    public void addNpcType(String name, String icon) {
        iconNames.put(name, icon);
    }

    @Override
    public void registerIcons(IIconRegister par1IconRegister) {
        super.registerIcons(par1IconRegister);
        IIcon icon;
        for (String name : iconNames.keySet()) {
            icon = par1IconRegister.registerIcon(iconNames.get(name));
            iconMap.put(name, icon);
        }
    }

    @Override
    public IIcon getIconIndex(ItemStack stack) {
        String type = getNpcType(stack);
        String sub = getNpcSubtype(stack);
        if (type != null) {
            if (!sub.isEmpty()) {
                type = type + "." + sub;
            }
            if (iconMap.containsKey(type)) {
                return iconMap.get(type);
            }
        }
        return super.getIconIndex(stack);
    }

}
