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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.event.IBoxRenderer;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateClient;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManagerClient;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;

import java.util.List;


public class ItemStructureBuilder extends Item implements IItemKeyInterface, IBoxRenderer {

    public ItemStructureBuilder(String itemName) {
        this.setUnlocalizedName(itemName);
        this.setCreativeTab(AWStructuresItemLoader.structureTab);
        this.setMaxStackSize(1);
        this.setTextureName("ancientwarfare:structure/" + itemName);
    }


    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        String structure = "guistrings.structure.no_selection";
        ItemStructureSettings viewSettings = ItemStructureSettings.getSettingsFor(stack);
        if (viewSettings.hasName()) {
            structure = viewSettings.name;
        }
        list.add(I18n.format("guistrings.current_structure") + " " + I18n.format(structure));
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
        return false;
    }

    @Override
    public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key) {
        return key == ItemKey.KEY_0;
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        if (player == null || player.world.isRemote) {
            return;
        }
        ItemStructureSettings buildSettings = ItemStructureSettings.getSettingsFor(stack);
        if (buildSettings.hasName()) {
            StructureTemplate template = StructureTemplateManager.INSTANCE.getTemplate(buildSettings.name);
            if (template == null) {
                player.sendMessage(new TextComponentTranslation("guistrings.template.not_found"));
                return;
            }
            BlockPos bpHit = BlockTools.getBlockClickedOn(player, player.world, true);
            if (bpHit == null) {
                return;
            }//no hit position, clicked on air
            StructureBuilder builder = new StructureBuilder(player.world, template, BlockTools.getPlayerFacingFromYaw(player.rotationYaw), bpHit.x, bpHit.y, bpHit.z);
            builder.instantConstruction();
            if (!player.capabilities.isCreativeMode) {
                int slot = player.inventory.currentItem;
                if (stack.getCount() == 1) {
                    player.inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
                } else {
                    stack.shrink(1);
                }
            }
        } else {
            player.sendMessage(new TextComponentTranslation("guistrings.structure.no_selection"));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!player.world.isRemote && !player.isSneaking() && player.capabilities.isCreativeMode) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_BUILDER, 0, 0, 0);
        }
        return stack;
    }

    @Override
    public void renderBox(EntityPlayer player, ItemStack stack, float delta) {
        ItemStructureSettings settings = ItemStructureSettings.getSettingsFor(stack);
        if (!settings.hasName()) {
            return;
        }
        String name = settings.name();
        StructureTemplateClient structure = StructureTemplateManagerClient.instance().getClientTemplate(name);
        if (structure == null) {
            return;
        }
        BlockPos hit = BlockTools.getBlockClickedOn(player, player.world, true);
        int face = BlockTools.getPlayerFacingFromYaw(player.rotationYaw);
        if (hit == null) {
            return;
        }
        StructureBB bb = new StructureBB(hit.x, hit.y, hit.z, face, structure.xSize, structure.ySize, structure.zSize, structure.xOffset, structure.yOffset, structure.zOffset);
        Util.renderBoundingBox(player, bb.min, bb.max, delta);
    }
}
