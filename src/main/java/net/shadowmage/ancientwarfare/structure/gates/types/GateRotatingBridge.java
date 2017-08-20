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

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.entity.RotateBoundingBox;

public class GateRotatingBridge extends Gate {

    /**
     * @param id
     */
    public GateRotatingBridge(int id, String tex) {
        super(id, tex);
        setName("gateDrawbridge");
        this.moveSpeed = 1.f;
//  this.texture = "gateBridgeWood1.png";
        this.canSoldierInteract = false;
        setIcon("gateWoodRotating");
    }

    @Override
    public void setCollisionBoundingBox(EntityGate gate) {
        if (gate.pos1 == null || gate.pos2 == null) {
            return;
        }
        BlockPos min = BlockTools.getMin(gate.pos1, gate.pos2);
        BlockPos max = BlockTools.getMax(gate.pos1, gate.pos2);
        if(gate.edgePosition == 0){
            gate.boundingBox.setBounds(min.x, min.y, min.z, max.x + 1, max.y + 1, max.z + 1);
        } else if (gate.edgePosition < gate.edgeMax) {
            if(!(gate.boundingBox instanceof RotateBoundingBox)) {
                try {
                    ObfuscationReflectionHelper.setPrivateValue(Entity.class, gate, new RotateBoundingBox(gate.gateOrientation, min, max.offset(1, 1, 1)), "boundingBox", "field_70121_D");
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
            if(gate.boundingBox instanceof RotateBoundingBox){
                ((RotateBoundingBox) gate.boundingBox).rotate(gate.getOpeningStatus() * getMoveSpeed());
            }
        } else {
            int heightAdj = max.y - min.y;
            BlockPos pos3 = max.moveUp(-heightAdj).moveForward(gate.gateOrientation, heightAdj);
            max = BlockTools.getMax(min, pos3).offset(1, 1, 1);
            min = BlockTools.getMin(min, pos3);
            gate.boundingBox.setBounds(min.x, min.y, min.z, max.x, max.y, max.z);
        }
    }

    @Override
    public boolean canActivate(EntityGate gate, boolean open) {
        if (gate.pos1 == null || gate.pos2 == null) {
            return false;
        }
        if (!open) {
            return super.canActivate(gate, false);
        } else {
//    boolean wideOnXAxis = gate.pos1.x!=gate.pos2.x;  
            BlockPos min = BlockTools.getMin(gate.pos1, gate.pos2);
            BlockPos max = BlockTools.getMax(gate.pos1, gate.pos2);
            int heightAdj = max.y - min.y;
            BlockPos pos3 = max.moveUp(-heightAdj).moveForward(gate.gateOrientation, heightAdj);
            max = BlockTools.getMax(min, pos3);
            min = BlockTools.getMin(min, pos3);
            Block id;
            for (int x = min.x; x <= max.x; x++) {
                for (int z = min.z; z <= max.z; z++) {
                    id = gate.world.getBlock(x, min.y, z);
                    if (!id.isAir(gate.world, x, min.y, z) && id != AWBlocks.gateProxy) {
                        return false;
                    }

                }
            }
            return true;
        }
    }

    @Override
    public void setInitialBounds(EntityGate gate, BlockPos pos1, BlockPos pos2) {
        BlockPos min = BlockTools.getMin(pos1, pos2);
        BlockPos max = BlockTools.getMax(pos1, pos2);
        boolean wideOnXAxis = min.x != max.x;
        float width = wideOnXAxis ? max.x - min.x + 1 : max.z - min.z + 1;
        float xOffset = wideOnXAxis ? width * 0.5f : 0.5f;
        float zOffset = wideOnXAxis ? 0.5f : width * 0.5f;
        gate.pos1 = min;
        gate.pos2 = max;
        gate.edgeMax = 90.f;
        gate.setPosition(min.x + xOffset, min.y, min.z + zOffset);
    }

    @Override
    public void onGateStartOpen(EntityGate gate) {
        if (gate.world.isRemote) {
            return;
        }
        BlockPos min = BlockTools.getMin(gate.pos1, gate.pos2).offset(0, 1, 0);
        BlockPos max = BlockTools.getMax(gate.pos1, gate.pos2);
        removeBetween(gate.world, min, max);

//  int heightAdj = max.y - min.y;
//  BlockPos pos3 = max.copy();
//  pos3.y = min.y;
//  adjustBounds(pos3, heightAdj, gate.gateOrientation);  
//  BlockPos minTemp = min.copy();
//  min = BlockTools.getMin(min, pos3);    
//  max = BlockTools.getMax(minTemp, pos3);
//  for(int x = min.x; x <= max.x; x++)
//    {
//    for(int y = min.y; y <=max.y; y++)
//      {
//      for(int z = min.z; z<= max.z; z++)
//        {
//        id = gate.world.getBlock(x, y, z);
//        if(id==Blocks.air)
//          {
//          gate.world.setBlock(x, y, z, AWStructuresItemLoader.gateProxy);
//          TileEntity te = gate.world.getTileEntity(x, y, z);
//          if(te!=null && te instanceof TEGateProxy)
//            {
//            TEGateProxy teg = (TEGateProxy)te;
//            teg.setOwner(gate);
//            }
//          }
//        }
//      }
//    }
    }

    @Override
    public void onGateFinishOpen(EntityGate gate) {
        BlockPos min = BlockTools.getMin(gate.pos1, gate.pos2);
        BlockPos max = BlockTools.getMax(gate.pos1, gate.pos2);
        int heightAdj = max.y - min.y;
        BlockPos pos3 = max.moveUp(-heightAdj).moveForward(gate.gateOrientation, heightAdj);
        max = BlockTools.getMax(min, pos3);
        min = BlockTools.getMin(min, pos3);
        placeBetween(gate, min, max);
    }

    @Override
    public void onGateStartClose(EntityGate gate) {
        BlockPos min = BlockTools.getMin(gate.pos1, gate.pos2);
        BlockPos max = BlockTools.getMax(gate.pos1, gate.pos2);
        boolean widestOnXAxis = gate.pos1.x != gate.pos2.x;
        int heightAdj = max.y - min.y;
        BlockPos pos3 = max.moveUp(-heightAdj).moveForward(gate.gateOrientation, heightAdj);
        max = BlockTools.getMax(min, pos3);
        min = BlockTools.getMin(min, pos3);
        Block id;
        for (int x = min.x; x <= max.x; x++) {
            for (int y = min.y; y <= max.y; y++) {
                for (int z = min.z; z <= max.z; z++) {
                    if ((widestOnXAxis && z == gate.pos1.z) || (!widestOnXAxis && x == gate.pos1.x)) {
                        continue;
                    }
                    id = gate.world.getBlock(x, y, z);
                    if (id == AWBlocks.gateProxy) {
                        gate.world.setBlockToAir(x, y, z);
                    }
                }
            }
        }
    }

    @Override
    public void onGateFinishClose(EntityGate gate) {
        super.onGateFinishClose(gate);
//  boolean widestOnXAxis = gate.pos1.x != gate.pos2.x;
//  int heightAdj = max.y - min.y;
//  BlockPos pos3 = max.copy();
//  pos3.y = min.y;
//  adjustBounds(pos3, heightAdj, gate.gateOrientation);  
//  BlockPos minTemp = min.copy();
//  min = BlockTools.getMin(min, pos3);    
//  max = BlockTools.getMax(minTemp, pos3);
//  Block id;
//  for(int x = min.x; x <= max.x; x++)
//    {
//    for(int y = min.y; y <=max.y; y++)
//      {
//      for(int z = min.z; z<= max.z; z++)
//        {
//        if((widestOnXAxis && z==gate.pos1.z) || (!widestOnXAxis && x==gate.pos1.x))
//          {
//          continue;
//          }
//        id = gate.world.getBlock(x, y, z);
//        if(id==AWStructuresItemLoader.gateProxy)
//          {
//          gate.world.setBlockToAir(x, y, z);
//          }
//        }
//      }
//    }
    }

}
