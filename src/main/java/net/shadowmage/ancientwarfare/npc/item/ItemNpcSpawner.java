package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.npc.entity.AWNPCEntityLoader;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemNpcSpawner extends Item {

    public ItemNpcSpawner() {
        this.setCreativeTab(AWNpcItemLoader.npcTab);
        //this.setTextureName("ancientwarfare:npc/spawner_miner");
        this.setUnlocalizedName("npc_spawner");
        this.setRegistryName(new ResourceLocation(AncientWarfareCore.modID, "npc_spawner"));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("guistrings.npc.spawner.right_click_to_place"));
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
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(world.isRemote){
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        BlockPos hit = BlockTools.getBlockClickedOn(player, player.world, true);
        if (hit == null) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        NpcBase npc = createNpcFromItem(player.world, stack);
        if (npc != null) {
            npc.setOwner(player);
            npc.setPosition(hit.getX() + 0.5d, hit.getY(), hit.getZ() + 0.5d);
            npc.setHomeAreaAtCurrentPosition();
            player.world.spawnEntity(npc);
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    /*
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
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                npc.setItemStackToSlot(slot, ItemStack.EMPTY);
            }
            npc.readAdditionalItemData(stack.getTagCompound().getCompoundTag("npcStoredData"));
        }
        return npc;
    }

    /*
     * return an itemstack of npc spawner item that contains the data to spawn the input npc<br>
     * npc type, subtype, equipment, levels health, food value, and owner will be stored.
     */
    public static ItemStack getSpawnerItemForNpc(NpcBase npc) {
        String type = npc.getNpcType();
        String sub = npc.getNpcSubType();
        @Nonnull ItemStack stack = getStackForNpcType(type, sub);
        NBTTagCompound tag = new NBTTagCompound();
        npc.writeAdditionalItemData(tag);
        stack.setTagInfo("npcStoredData", tag);
        return stack;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab != AWNpcItemLoader.npcTab) {
            return;
        }
        AWNPCEntityLoader.getSpawnerSubItems(items);
    }

    public static ItemStack getStackForNpcType(String type, String npcSubtype) {
        @Nonnull ItemStack stack = new ItemStack(AWItems.npcSpawner);
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

/*
    */
/*
     * Npc type 'name' is full npc-type -- type.subtype
     *//*

    public void addNpcType(String name, String icon) {
        iconNames.put(name, icon);
    }
*/

/*
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
*/

}
