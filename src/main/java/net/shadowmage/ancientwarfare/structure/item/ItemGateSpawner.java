/**
 Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
 This software is distributed under the terms of the GNU General Public License.
 Please see COPYING for precise license information.

 This file is part of Ancient Warfare.

 Ancient Warfare is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Ancient Warfare is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.gates.types.Gate;

import java.util.List;

public class ItemGateSpawner extends Item implements IItemKeyInterface, IItemClickable {

    /**
     * @param itemID
     * @param hasSubTypes
     */
    public ItemGateSpawner(String name) {
        this.setUnlocalizedName(name);
        this.setCreativeTab(AWStructuresItemLoader.structureTab);
        this.setMaxStackSize(1);
    }

    @Override
    public boolean cancelRightClick(EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public boolean cancelLeftClick(EntityPlayer player, ItemStack stack) {
        return false;
    }

    @Override
    public IIcon getIconFromDamage(int par1) {
        return Gate.getGateByID(par1).getIconTexture();
    }

    @Override
    public void registerIcons(IIconRegister par1IconRegister) {
        Gate.registerIconsForGates(par1IconRegister);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        NBTTagCompound tag = null;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("AWGateInfo")) {
            tag = stack.getTagCompound().getCompoundTag("AWGateInfo");
        } else {
            tag = new NBTTagCompound();
        }
        if (tag.hasKey("pos1") && tag.hasKey("pos2")) {
            list.add(StatCollector.translateToLocal("guistrings.gate.construct"));
            list.add(StatCollector.translateToLocal("guistrings.gate.clear_item"));
        } else if (tag.hasKey("pos1")) {
            String key = InputHandler.instance().getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_0);
            list.add(StatCollector.translateToLocalFormatted("guistrings.gate.use_primary_item_key", key));
            list.add(StatCollector.translateToLocal("guistrings.gate.clear_item"));
        } else {
            String key = InputHandler.instance().getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_0);
            list.add(StatCollector.translateToLocalFormatted("guistrings.gate.use_primary_item_key", key));
            list.add(StatCollector.translateToLocal("guistrings.gate.clear_item"));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List p_150895_3_) {
        Gate g;
        for (int i = 0; i < 16; i++) {
            g = Gate.getGateByID(i);
            if (g == null) {
                continue;
            }
            p_150895_3_.add(g.getDisplayStack());
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return "item." + Gate.getGateByID(par1ItemStack.getItemDamage()).getDisplayName();
    }

    @Override
    public void onRightClick(EntityPlayer player, ItemStack stack) {
        World world = player.worldObj;
        if (world.isRemote) {
            return;
        }
        NBTTagCompound tag;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("AWGateInfo")) {
            tag = stack.getTagCompound().getCompoundTag("AWGateInfo");
        } else {
            tag = new NBTTagCompound();
        }
        if (player.isSneaking()) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        } else if (tag.hasKey("pos1") && tag.hasKey("pos2")) {
            byte facing = (byte) BlockTools.getPlayerFacingFromYaw(player.rotationYaw);
            BlockPosition pos1 = new BlockPosition(tag.getCompoundTag("pos1"));
            BlockPosition pos2 = new BlockPosition(tag.getCompoundTag("pos2"));
            BlockPosition avg = BlockTools.getAverageOf(pos1, pos2);
            if (player.getDistance(avg.x + 0.5d, pos1.y, avg.z + 0.5d) > 10) {
                player.addChatMessage(new ChatComponentTranslation("guistrings.gate.too_far"));
                return;
            }
            if (!canSpawnGate(world, pos1, pos2)) {
                player.addChatMessage(new ChatComponentTranslation("guistrings.gate.exists"));
                return;
            }
            EntityGate entity = Gate.constructGate(world, pos1, pos2, Gate.getGateByID(stack.getItemDamage()), facing);
            if (entity != null) {
                entity.setOwnerName(player.getCommandSenderName());
                world.spawnEntityInWorld(entity);
                if (!player.capabilities.isCreativeMode) {
                    ItemStack item = player.getHeldItem();
                    if (item != null) {
                        item.stackSize--;
                        if (item.stackSize <= 0) {
                            player.setCurrentItemOrArmor(0, null);
                        }
                    }
                }
                tag.removeTag("pos1");
                tag.removeTag("pos2");
                stack.setTagCompound(tag);
            } else {
                player.addChatMessage(new ChatComponentTranslation("guistrings.gate.need_to_clear"));
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected boolean canSpawnGate(World world, BlockPosition pos1, BlockPosition pos2) {
        BlockPosition min = BlockTools.getMin(pos1, pos2);
        BlockPosition max = BlockTools.getMax(pos1, pos2);
        AxisAlignedBB newGateBB = AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x + 1, max.y + 1, max.z + 1);
        AxisAlignedBB oldGateBB = null;
        List<EntityGate> gates = world.getEntitiesWithinAABB(EntityGate.class, newGateBB);
        for (EntityGate gate : gates) {
            min = BlockTools.getMin(gate.pos1, gate.pos2);
            max = BlockTools.getMax(gate.pos1, gate.pos2);
            oldGateBB = AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x + 1, max.y + 1, max.z + 1);
            if (oldGateBB.intersectsWith(newGateBB)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key) {
        return key == ItemKey.KEY_0;
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        BlockPosition hit = BlockTools.getBlockClickedOn(player, player.worldObj, true);
        if (hit == null) {
            return;
        }
        NBTTagCompound tag;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("AWGateInfo")) {
            tag = stack.getTagCompound().getCompoundTag("AWGateInfo");
        } else {
            tag = new NBTTagCompound();
        }
        if (tag.hasKey("pos1") && tag.hasKey("pos2")) {
            /**
             * do nothing, wait for right click for build order
             */
        } else if (tag.hasKey("pos1")) {
            Gate g = Gate.getGateByID(stack.getItemDamage());
            if (g.arePointsValidPair(new BlockPosition(tag.getCompoundTag("pos1")), hit)) {
                tag.setTag("pos2", hit.writeToNBT(new NBTTagCompound()));
                player.addChatMessage(new ChatComponentTranslation("guistrings.gate.set_pos_two"));
            } else {
                player.addChatMessage(new ChatComponentTranslation("guistrings.gate.invalid_position"));
            }
        } else {
            tag.setTag("pos1", hit.writeToNBT(new NBTTagCompound()));
            player.addChatMessage(new ChatComponentTranslation("guistrings.gate.set_pos_one"));
        }
        stack.setTagInfo("AWGateInfo", tag);
    }

    @Override
    public boolean onRightClickClient(EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public boolean onLeftClickClient(EntityPlayer player, ItemStack stack) {
        return false;
    }

    @Override
    public void onLeftClick(EntityPlayer player, ItemStack stack) {

    }

}
