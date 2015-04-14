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
package net.shadowmage.ancientwarfare.structure.gates.types;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.gates.IGateType;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;
import net.shadowmage.ancientwarfare.structure.tile.TEGateProxy;

import java.util.HashMap;

public abstract class Gate implements IGateType {

    public static final Gate[] gateTypes = new Gate[16];


    public static final Gate basicWood = new GateBasicWood(0, "ancientwarfare:textures/model/structure/gate/gateWood1.png");
    public static final Gate basicIron = new GateBasicIron(1, "ancientwarfare:textures/model/structure/gate/gateIron1.png");

    public static final Gate singleWood = new GateSingleSlideWood(4, "ancientwarfare:textures/model/structure/gate/gateWood1.png");
    public static final Gate singleIron = new GateSingleIron(5, "ancientwarfare:textures/model/structure/gate/gateIron1.png");

    public static final Gate doubleWood = new GateDoubleSlideWood(8, "ancientwarfare:textures/model/structure/gate/gateWood1.png");
    public static final Gate doubleIron = new GateDoubleIron(9, "ancientwarfare:textures/model/structure/gate/gateIron1.png");

    public static final Gate rotatingBridge = new GateRotatingBridge(12, "ancientwarfare:textures/model/structure/gate/gateBridgeWood1.png");

    public static final HashMap<String, Integer> gateIDByName = new HashMap<String, Integer>();

    static {
        gateIDByName.put("gate.verticalWooden", 0);
        gateIDByName.put("gate.verticalIron", 1);
        gateIDByName.put("gate.singleWood", 4);
        gateIDByName.put("gate.singleIron", 5);
        gateIDByName.put("gate.doubleWood", 8);
        gateIDByName.put("gate.doubleIron", 9);
        gateIDByName.put("gate.drawbridge", 12);
    }

    protected final int globalID;
    protected String displayName = "";
    protected String tooltip = "";
    protected String iconTexture = "";
    protected int maxHealth = 40;
    protected int modelType = 0;

    protected boolean canSoldierInteract = true;

    protected float moveSpeed = 0.5f * 0.05f; ///1/2 block / second

    protected final ItemStack displayStack;

    protected final ResourceLocation textureLocation;
    protected IIcon itemIcon;

    /**
     *
     */
    public Gate(int id, String textureLocation) {
        this.globalID = id;
        if (id >= 0 && id < gateTypes.length && gateTypes[id] == null) {
            gateTypes[id] = this;
        }
        this.displayStack = new ItemStack(AWStructuresItemLoader.gateSpawner, 1, id);
        this.textureLocation = new ResourceLocation(textureLocation);
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        itemIcon = reg.registerIcon("ancientwarfare:structure/gates/" + iconTexture);
    }

    @Override
    public IIcon getIconTexture() {
        return itemIcon;
    }

    @Override
    public int getGlobalID() {
        return globalID;
    }

    @Override
    public int getModelType() {
        return modelType;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getTooltip() {
        return tooltip;
    }

    @Override
    public ItemStack getConstructingItem() {
        return new ItemStack(AWStructuresItemLoader.gateSpawner, 1, this.globalID);
    }

    @Override
    public ItemStack getDisplayStack() {
        return displayStack;
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    @Override
    public float getMoveSpeed() {
        return moveSpeed;
    }

    @Override
    public ResourceLocation getTexture() {
        return textureLocation;
    }

    @Override
    public boolean canActivate(EntityGate gate, boolean open) {
        return true;
    }

    @Override
    public boolean canSoldierActivate() {
        return canSoldierInteract;
    }

    public static String getGateNameFor(EntityGate gate) {
        int id = gate.getGateType().getGlobalID();
        return getGateNameFor(id);
    }

    public static String getGateNameFor(int id) {
        int gateID;
        for (String key : gateIDByName.keySet()) {
            gateID = gateIDByName.get(key);
            if (gateID == id) {
                return key;
            }
        }
        return "gate.verticalWooden";
    }

    public static Gate getGateByName(String name) {
        if (gateIDByName.containsKey(name)) {
            return getGateByID(gateIDByName.get(name));
        }
        return basicWood;
    }

    public static Gate getGateByID(int id) {
        if (id >= 0 && id < gateTypes.length) {
            return gateTypes[id];
        }
        return basicWood;
    }


    @Override
    public void onUpdate(EntityGate ent) {

    }

    @Override
    public void setCollisionBoundingBox(EntityGate gate) {
        if (gate.pos1 == null || gate.pos2 == null) {
            return;
        }
        BlockPosition min = BlockTools.getMin(gate.pos1, gate.pos2);
        BlockPosition max = BlockTools.getMax(gate.pos1, gate.pos2);
        if (gate.edgePosition > 0) {
            gate.boundingBox.setBounds(min.x, max.y + 0.5d, min.z, max.x + 1, max.y + 1, max.z + 1);
        } else {
            gate.boundingBox.setBounds(min.x, min.y, min.z, max.x + 1, max.y + 1, max.z + 1);
        }
    }

    @Override
    public boolean arePointsValidPair(BlockPosition pos1, BlockPosition pos2) {
        return pos1.x == pos2.x || pos1.z == pos2.z;
    }

    @Override
    public void setInitialBounds(EntityGate gate, BlockPosition pos1, BlockPosition pos2) {
        BlockPosition min = BlockTools.getMin(pos1, pos2);
        BlockPosition max = BlockTools.getMax(pos1, pos2);
        boolean wideOnXAxis = min.x != max.x;
        float width = wideOnXAxis ? max.x - min.x + 1 : max.z - min.z + 1;
        float xOffset = wideOnXAxis ? width * 0.5f : 0.5f;
        float zOffset = wideOnXAxis ? 0.5f : width * 0.5f;
        gate.pos1 = min;
        gate.pos2 = max;
        gate.edgeMax = max.y - min.y + 1;
        gate.setPosition(min.x + xOffset, min.y, min.z + zOffset);
    }

    @Override
    public void onGateStartOpen(EntityGate gate) {
        if (gate.worldObj.isRemote) {
            return;
        }
        Block id;
        BlockPosition min = BlockTools.getMin(gate.pos1, gate.pos2);
        BlockPosition max = BlockTools.getMax(gate.pos1, gate.pos2);
        for (int x = min.x; x <= max.x; x++) {
            for (int y = min.y; y <= max.y; y++) {
                for (int z = min.z; z <= max.z; z++) {
                    id = gate.worldObj.getBlock(x, y, z);
                    if (id == AWBlocks.gateProxy) {
                        gate.worldObj.setBlockToAir(x, y, z);
                    }
                }
            }
        }
    }

    @Override
    public void onGateFinishOpen(EntityGate gate) {

    }

    @Override
    public void onGateStartClose(EntityGate gate) {

    }

    @Override
    public void onGateFinishClose(EntityGate gate) {
        if (gate.worldObj.isRemote) {
            return;
        }
        BlockPosition min = BlockTools.getMin(gate.pos1, gate.pos2);
        BlockPosition max = BlockTools.getMax(gate.pos1, gate.pos2);
        for (int x = min.x; x <= max.x; x++) {
            for (int y = min.y; y <= max.y; y++) {
                for (int z = min.z; z <= max.z; z++) {
                    if (gate.worldObj.isAirBlock(x, y, z)) {
                        if (gate.worldObj.setBlock(x, y, z, AWBlocks.gateProxy)) {
                            TileEntity te = gate.worldObj.getTileEntity(x, y, z);
                            if (te instanceof TEGateProxy) {
                                ((TEGateProxy) te).setOwner(gate);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @return a fully setup gate, or null if chosen spawn position is invalid (blocks in the way)
     */
    public static EntityGate constructGate(World world, BlockPosition pos1, BlockPosition pos2, Gate type, byte facing) {
        BlockPosition min = BlockTools.getMin(pos1, pos2);
        BlockPosition max = BlockTools.getMax(pos1, pos2);
        for (int x = min.x; x <= max.x; x++) {
            for (int y = min.y; y <= max.y; y++) {
                for (int z = min.z; z <= max.z; z++) {
                    if (!world.isAirBlock(x, y, z)) {
                        AWLog.logDebug("could not create gate for non-air block at: " + x + "," + y + "," + z + " block: " + world.getBlock(x, y, z));
                        return null;
                    }
                }
            }
        }

        if (pos1.x == pos2.x) {
            if (facing == 0 || facing == 2) {
                facing++;
                facing %= 4;
            }
        } else if (pos1.z == pos2.z) {
            if (facing == 1 || facing == 3) {
                facing++;
                facing %= 4;
            }
        }

        EntityGate ent = new EntityGate(world);
        ent.setGateType(type);
        ent.setHealth(type.getMaxHealth());
        ent.gateOrientation = facing;
        type.setInitialBounds(ent, pos1, pos2);
        type.onGateFinishClose(ent);
        return ent;
    }

    public static ItemStack getItemToConstruct(int type) {
        return getGateByID(type).getConstructingItem();
    }

    public static ItemStack getItemToConstruct(String typeName) {
        return getGateByName(typeName).getConstructingItem();
    }

    public static void registerIconsForGates(IIconRegister reg) {
        for (IGateType t : gateTypes) {
            if (t == null) {
                continue;
            }
            t.registerIcons(reg);
        }
    }

}
