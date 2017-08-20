/**
 Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.RayTraceResult.MovingObjectType;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

import java.util.List;

public class ItemSpawnerPlacer extends Item {

    public ItemSpawnerPlacer(String itemName) {
        this.setUnlocalizedName(itemName);
        this.setCreativeTab(AWStructuresItemLoader.structureTab);
        this.setTextureName("ancientwarfare:structure/" + itemName);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        list.add(I18n.format("guistrings.selected_mob") + ":");
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("spawnerData")) {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag("spawnerData");
            String mobID = tag.getString("EntityId");
            if (mobID.isEmpty()) {
                list.add(I18n.format("guistrings.no_selection"));
            } else {
                list.add(I18n.format("entity." + mobID + ".name"));
            }
        } else {
            list.add(I18n.format("guistrings.no_selection"));
        }
        list.add(EnumChatFormatting.RED + I18n.format("guistrings.spawner.warning_1"));
        list.add(EnumChatFormatting.RED + I18n.format("guistrings.spawner.warning_2"));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player == null || player.world == null || player.world.isRemote || stack == null) {
            return stack;
        }
        RayTraceResult mophit = getRayTraceResultFromPlayer(player.world, player, false);
        if (player.capabilities.isCreativeMode && player.isSneaking()) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SPAWNER, 0, 0, 0);
        } else if (mophit != null && mophit.typeOfHit == MovingObjectType.BLOCK) {
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("spawnerData")) {
                BlockPos hit = new BlockPos(mophit);
                if (player.world.setBlock(hit.x, hit.y, hit.z, Blocks.mob_spawner)) {
                    NBTTagCompound tag = stack.getTagCompound().getCompoundTag("spawnerData");
                    tag.setInteger("x", hit.x);
                    tag.setInteger("y", hit.y);
                    tag.setInteger("z", hit.z);
                    TileEntity te = player.world.getTileEntity(hit.x, hit.y, hit.z);
                    te.readFromNBT(tag);

                    if (!player.capabilities.isCreativeMode) {
                        stack.shrink(1);
                    }
                }
            } else {
                player.addChatComponentMessage(new TextComponentTranslation("guistrings.spawner.nodata"));
            }
        } else {
            player.addChatComponentMessage(new TextComponentTranslation("guistrings.spawner.noblock"));
        }
        return stack;
    }

}
