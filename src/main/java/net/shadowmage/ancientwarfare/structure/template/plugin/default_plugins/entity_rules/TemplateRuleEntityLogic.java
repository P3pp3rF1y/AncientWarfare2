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
package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.entity_rules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuildingException.EntityPlacementException;

import javax.annotation.Nonnull;

public class TemplateRuleEntityLogic extends TemplateRuleVanillaEntity {

    public NBTTagCompound tag = new NBTTagCompound();

    NonNullList<ItemStack> inventory;
    NonNullList<ItemStack> equipment = NonNullList.withSize(EntityEquipmentSlot.values().length, ItemStack.EMPTY);

    public TemplateRuleEntityLogic() {
    }

    public TemplateRuleEntityLogic(World world, Entity entity, int turns, int x, int y, int z) {
        super(world, entity, turns, x, y, z);
        entity.writeToNBT(tag);
        if (entity instanceof EntityLiving)//handles villagers / potentially other living npcs with inventories
        {
            tag.removeTag("Equipment");
            EntityLiving living = (EntityLiving) entity;
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                equipment.set(slot.ordinal(), living.getItemStackFromSlot(slot).isEmpty() ? ItemStack.EMPTY : living.getItemStackFromSlot(slot).copy());
            }
        }
        if (entity instanceof IInventory)//handles minecart-chests
        {
            tag.removeTag("Items");
            IInventory eInv = (IInventory) entity;
            this.inventory = NonNullList.withSize(eInv.getSizeInventory(), ItemStack.EMPTY);
            for (int i = 0; i < eInv.getSizeInventory(); i++) {
                this.inventory.set(i, eInv.getStackInSlot(i).isEmpty() ? ItemStack.EMPTY : eInv.getStackInSlot(i).copy());
            }
        }
        tag.removeTag("UUIDMost");
        tag.removeTag("UUIDLeast");
    }

    @Override
    public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) throws EntityPlacementException {
        Entity e = createEntity(world, turns, pos, builder);
        world.spawnEntity(e);
    }

    protected Entity createEntity(World world, int turns, BlockPos pos, IStructureBuilder builder) throws EntityPlacementException {
        Entity e = EntityList.createEntityByIDFromName(registryName, world);
        if (e == null) {
            throw new EntityPlacementException("Could not create entity for name: " + registryName.toString() + " Entity skipped during structure creation.\n" +
                    "Entity data: " + tag);
        }
        NBTTagList list = new NBTTagList();
        list.appendTag(new NBTTagDouble(pos.getX() + BlockTools.rotateFloatX(xOffset, zOffset, turns)));
        list.appendTag(new NBTTagDouble(pos.getY()));
        list.appendTag(new NBTTagDouble(pos.getZ() + BlockTools.rotateFloatZ(xOffset, zOffset, turns)));
        tag.setTag("Pos", list);
        e.readFromNBT(tag);
        if (equipment != null && e instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) e;
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                living.setItemStackToSlot(slot, equipment.get(slot.ordinal()).isEmpty() ? ItemStack.EMPTY : equipment.get(slot.ordinal()).copy());
            }
        }
        if (inventory != null && e instanceof IInventory) {
            IInventory eInv = (IInventory) e;
            for (int i = 0; i < inventory.size() && i < eInv.getSizeInventory(); i++) {
                eInv.setInventorySlotContents(i, inventory.get(i).isEmpty() ? ItemStack.EMPTY : inventory.get(i).copy());
            }
        }
        e.rotationYaw = (rotation + 90.f * turns) % 360.f;
        return e;
    }

    @Override
    public void writeRuleData(NBTTagCompound tag) {
        super.writeRuleData(tag);
        tag.setTag("entityData", this.tag);

        if (inventory != null) {
            NBTTagCompound invData = new NBTTagCompound();
            invData.setInteger("length", inventory.size());
            NBTTagCompound itemTag;
            NBTTagList list = new NBTTagList();
            @Nonnull ItemStack stack;
            for (int i = 0; i < inventory.size(); i++) {
                stack = inventory.get(i);
                if (stack.isEmpty()) {
                    continue;
                }
                itemTag = stack.writeToNBT(new NBTTagCompound());
                itemTag.setInteger("slot", i);
                list.appendTag(itemTag);
            }
            invData.setTag("inventoryContents", list);
            tag.setTag("inventoryData", invData);
        }
        if (equipment != null) {
            NBTTagCompound invData = new NBTTagCompound();
            NBTTagCompound itemTag;
            NBTTagList list = new NBTTagList();
            @Nonnull ItemStack stack;
            for (int i = 0; i < equipment.size(); i++) {
                stack = equipment.get(i);
                if (stack.isEmpty()) {
                    continue;
                }
                itemTag = stack.writeToNBT(new NBTTagCompound());
                itemTag.setInteger("slot", i);
                list.appendTag(itemTag);
            }
            invData.setTag("equipmentContents", list);
            tag.setTag("equipmentData", invData);
        }
    }

    @Override
    public void parseRuleData(NBTTagCompound tag) {
        super.parseRuleData(tag);
        this.tag = tag.getCompoundTag("entityData");
        if (tag.hasKey("inventoryData")) {
            NBTTagCompound inventoryTag = tag.getCompoundTag("inventoryData");
            int length = inventoryTag.getInteger("length");
            inventory = NonNullList.withSize(length, ItemStack.EMPTY);
            NBTTagCompound itemTag;
            NBTTagList list = tag.getTagList("inventoryContents", Constants.NBT.TAG_COMPOUND);
            int slot;
            @Nonnull ItemStack stack;
            for (int i = 0; i < list.tagCount(); i++) {
                itemTag = list.getCompoundTagAt(i);
                stack = new ItemStack(itemTag);
                if (!stack.isEmpty()) {
                    slot = itemTag.getInteger("slot");
                    inventory.set(slot, stack);
                }
            }
        }
        if (tag.hasKey("equipmentData")) {
            NBTTagCompound inventoryTag = tag.getCompoundTag("equipmentData");
            NBTTagCompound itemTag;
            NBTTagList list = inventoryTag.getTagList("equipmentContents", Constants.NBT.TAG_COMPOUND);
            int slot;
            @Nonnull ItemStack stack;
            for (int i = 0; i < list.tagCount(); i++) {
                itemTag = list.getCompoundTagAt(i);
                stack = new ItemStack(itemTag);
                if (!stack.isEmpty()) {
                    slot = itemTag.getInteger("slot");
                    equipment.set(slot, stack);
                }
            }
        }
    }

}
