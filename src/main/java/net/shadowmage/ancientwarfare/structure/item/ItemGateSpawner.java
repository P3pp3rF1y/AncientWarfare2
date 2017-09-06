/*
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.event.IBoxRenderer;
import net.shadowmage.ancientwarfare.structure.gates.types.Gate;

import java.util.List;

public class ItemGateSpawner extends Item implements IItemKeyInterface, IBoxRenderer {

    public ItemGateSpawner(String name) {
        this.setUnlocalizedName(name);
        this.setCreativeTab(AWStructuresItemLoader.structureTab);
        this.setMaxStackSize(1);
    }

    @Override
    public IIcon getIconFromDamage(int par1) {
        return Gate.getGateByID(par1).getIconTexture();
    }

    @Override
    public void registerIcons(IIconRegister par1IconRegister) {
        Gate.registerIconsForGates(par1IconRegister);
    }


    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        NBTTagCompound tag;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("AWGateInfo")) {
            tag = stack.getTagCompound().getCompoundTag("AWGateInfo");
        } else {
            tag = new NBTTagCompound();
        }
        if (tag.hasKey("pos1") && tag.hasKey("pos2")) {
            list.add(I18n.format("guistrings.gate.construct"));
        } else {
            String key = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_0);
            list.add(I18n.format("guistrings.gate.use_primary_item_key", key));
        }
        list.add(I18n.format("guistrings.gate.clear_item"));
    }


    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        Gate g;
        for (int i = 0; i < 16; i++) {
            g = Gate.getGateByID(i);
            if (g == null) {
                continue;
            }
            list.add(g.getDisplayStack());
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return "item." + Gate.getGateByID(par1ItemStack.getItemDamage()).getDisplayName();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            return stack;
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
            BlockPos pos1 = new BlockPos(tag.getCompoundTag("pos1"));
            BlockPos pos2 = new BlockPos(tag.getCompoundTag("pos2"));
            BlockPos avg = BlockTools.getAverageOf(pos1, pos2);
            int max = 10;
            if(pos1.x - pos2.x > max)
                max = pos1.x - pos2.x;
            else if(pos2.x - pos1.x > max)
                max = pos2.x - pos1.x;
            if(pos1.z - pos2.z > max)
                max = pos1.z - pos2.z;
            else if(pos2.z - pos1.z > max)
                max = pos2.z - pos1.z;
            if (player.getDistance(avg.x + 0.5, pos1.y + 0.5, avg.z + 0.5) > max && player.getDistance(avg.x + 0.5, pos2.y + 0.5, avg.z + 0.5) > max) {
                player.sendMessage(new TextComponentTranslation("guistrings.gate.too_far"));
                return stack;
            }
            if (!canSpawnGate(world, pos1, pos2)) {
                player.sendMessage(new TextComponentTranslation("guistrings.gate.exists"));
                return stack;
            }
            byte facing = (byte) BlockTools.getPlayerFacingFromYaw(player.rotationYaw);
            EntityGate entity = Gate.constructGate(world, pos1, pos2, Gate.getGateByID(stack.getItemDamage()), facing);
            if (entity != null) {
                entity.setOwnerName(player.getName());
                world.spawnEntityInWorld(entity);
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
                tag.removeTag("pos1");
                tag.removeTag("pos2");
                stack.setTagCompound(tag);
            } else {
                player.sendMessage(new TextComponentTranslation("guistrings.gate.need_to_clear"));
            }
        }
        return stack;
    }


    protected boolean canSpawnGate(World world, BlockPos pos1, BlockPos pos2) {
        BlockPos min = BlockTools.getMin(pos1, pos2);
        BlockPos max = BlockTools.getMax(pos1, pos2);
        AxisAlignedBB newGateBB = new AxisAlignedBB(min.x, min.y, min.z, max.x + 1, max.y + 1, max.z + 1);
        AxisAlignedBB oldGateBB;
        List<EntityGate> gates = world.getEntitiesWithinAABB(EntityGate.class, newGateBB);
        for (EntityGate gate : gates) {
            min = BlockTools.getMin(gate.pos1, gate.pos2);
            max = BlockTools.getMax(gate.pos1, gate.pos2);
            oldGateBB = new AxisAlignedBB(min.x, min.y, min.z, max.x + 1, max.y + 1, max.z + 1);
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
        BlockPos hit = BlockTools.getBlockClickedOn(player, player.world, true);
        if (hit == null) {
            return;
        }
        NBTTagCompound tag;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("AWGateInfo")) {
            tag = stack.getTagCompound().getCompoundTag("AWGateInfo");
        } else {
            tag = new NBTTagCompound();
        }
        if (!tag.hasKey("pos2")) {
            if (tag.hasKey("pos1")) {
                Gate g = Gate.getGateByID(stack.getItemDamage());
                if (g.arePointsValidPair(new BlockPos(tag.getCompoundTag("pos1")), hit)) {
                    tag.setTag("pos2", hit.writeToNBT(new NBTTagCompound()));
                    player.sendMessage(new TextComponentTranslation("guistrings.gate.set_pos_two"));
                } else {
                    player.sendMessage(new TextComponentTranslation("guistrings.gate.invalid_position"));
                }
            } else {
                tag.setTag("pos1", hit.writeToNBT(new NBTTagCompound()));
                player.sendMessage(new TextComponentTranslation("guistrings.gate.set_pos_one"));
            }
        }
        stack.setTagInfo("AWGateInfo", tag);
    }

    @Override
    public void renderBox(EntityPlayer player, ItemStack stack, float delta) {
        NBTTagCompound tag = stack.getTagCompound();
        BlockPos p1, p2;
        if (tag != null && tag.hasKey("AWGateInfo")) {
            tag = tag.getCompoundTag("AWGateInfo");
            if (tag.hasKey("pos1")) {
                p1 = new BlockPos(tag.getCompoundTag("pos1"));
                if (tag.hasKey("pos2")) {
                    p2 = new BlockPos(tag.getCompoundTag("pos2"));
                } else {
                    p2 = BlockTools.getBlockClickedOn(player, player.world, true);
                    if (p2 == null) {
                        return;
                    }
                }
            } else {
                p1 = BlockTools.getBlockClickedOn(player, player.world, true);
                if (p1 == null) {
                    return;
                }
                p2 = p1;
            }
        } else {
            p1 = BlockTools.getBlockClickedOn(player, player.world, true);
            if (p1 == null) {
                return;
            }
            p2 = p1;
        }
        Util.renderBoundingBox(player, BlockTools.getMin(p1, p2), BlockTools.getMax(p1, p2), delta);
    }
}
