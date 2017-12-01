/*
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

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.structure.gui.GuiSpawnerPlacer;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSpawnerPlacer extends ItemBaseStructure {

    public ItemSpawnerPlacer(String name) {
        super(name);
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(I18n.format("guistrings.selected_mob") + ":");
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("spawnerData")) {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag("spawnerData");
            String mobID = tag.getString("EntityId");
            if (mobID.isEmpty()) {
                tooltip.add(I18n.format("guistrings.no_selection"));
            } else {
                tooltip.add(I18n.format("entity." + mobID + ".name"));
            }
        } else {
            tooltip.add(I18n.format("guistrings.no_selection"));
        }
        tooltip.add(TextFormatting.RED + I18n.format("guistrings.spawner.warning_1"));
        tooltip.add(TextFormatting.RED + I18n.format("guistrings.spawner.warning_2"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.world.isRemote) {
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        if (stack.isEmpty()) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        RayTraceResult traceResult = rayTrace(player.world, player, false);
        if (player.capabilities.isCreativeMode && player.isSneaking()) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SPAWNER, 0, 0, 0);
        } else if (traceResult != null && traceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("spawnerData")) {
                if (player.world.setBlockState(traceResult.getBlockPos(), Blocks.MOB_SPAWNER.getDefaultState())) {
                    NBTTagCompound tag = stack.getTagCompound().getCompoundTag("spawnerData"); //TODO may make more sense to just update spawner specific tags instead of everything
                    tag.setString("id", Blocks.MOB_SPAWNER.getRegistryName().toString());
                    tag.setInteger("x", traceResult.getBlockPos().getX());
                    tag.setInteger("y", traceResult.getBlockPos().getY());
                    tag.setInteger("z", traceResult.getBlockPos().getZ());
                    TileEntity te = player.world.getTileEntity(traceResult.getBlockPos());
                    te.readFromNBT(tag);

                    if (!player.capabilities.isCreativeMode) {
                        stack.shrink(1);
                    }
                }
            } else {
                player.sendMessage(new TextComponentTranslation("guistrings.spawner.nodata"));
            }
        } else {
            player.sendMessage(new TextComponentTranslation("guistrings.spawner.noblock"));
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void registerClient() {
        super.registerClient();

        NetworkHandler.registerGui(NetworkHandler.GUI_SPAWNER, GuiSpawnerPlacer.class);
    }
}
