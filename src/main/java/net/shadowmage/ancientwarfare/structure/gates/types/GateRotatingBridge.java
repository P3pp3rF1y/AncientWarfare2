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
package net.shadowmage.ancientwarfare.structure.gates.types;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.block.AWStructuresBlocks;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.entity.RotateBoundingBox;

public class GateRotatingBridge extends Gate {

    /*
     * @param id
     */
    public GateRotatingBridge(int id, String tex) {
        super(id, tex);
        setName("gateDrawbridge");
        this.moveSpeed = 1.f;
        this.canSoldierInteract = false;
        setVariant(Variant.WOOD_ROTATING);
    }

    @Override
    public void setCollisionBoundingBox(EntityGate gate) {
        if (gate.pos1 == null || gate.pos2 == null) {
            return;
        }
        BlockPos min = BlockTools.getMin(gate.pos1, gate.pos2);
        BlockPos max = BlockTools.getMax(gate.pos1, gate.pos2);
        if(gate.edgePosition == 0){
            gate.setEntityBoundingBox(new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1));
        } else if (gate.edgePosition < gate.edgeMax) {
            if(!(gate.getEntityBoundingBox() instanceof RotateBoundingBox)) {
                try {
                    ObfuscationReflectionHelper.setPrivateValue(Entity.class, gate, new RotateBoundingBox(gate.gateOrientation, min, max.add(1, 1, 1)), "boundingBox", "field_70121_D");
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
            if(gate.getEntityBoundingBox() instanceof RotateBoundingBox){
                ((RotateBoundingBox) gate.getEntityBoundingBox()).rotate(gate.getOpeningStatus() * getMoveSpeed());
            }
        } else {
            int heightAdj = max.getY() - min.getY();
            BlockPos pos3 = max.up(-heightAdj).offset(gate.gateOrientation, heightAdj);
            max = BlockTools.getMax(min, pos3).add(1, 1, 1);
            min = BlockTools.getMin(min, pos3);
            gate.setEntityBoundingBox(new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()));
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
//    boolean wideOnXAxis = gate.pos1.getX()!=gate.pos2.getX();
            BlockPos min = BlockTools.getMin(gate.pos1, gate.pos2);
            BlockPos max = BlockTools.getMax(gate.pos1, gate.pos2);
            int heightAdj = max.getY() - min.getY();
            BlockPos pos3 = max.up(-heightAdj).offset(gate.gateOrientation, heightAdj);
            max = BlockTools.getMax(min, pos3);
            min = BlockTools.getMin(min, pos3);
            Block id;
            for (int x = min.getX(); x <= max.getX(); x++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    BlockPos posToCheck = new BlockPos(x, min.getY(), z);
                    id = gate.world.getBlockState(posToCheck).getBlock();
                    if (!gate.world.isAirBlock(posToCheck) && id != AWStructuresBlocks.gateProxy) {
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
        boolean wideOnXAxis = min.getX() != max.getX();
        float width = wideOnXAxis ? max.getX() - min.getX() + 1 : max.getZ() - min.getZ() + 1;
        float xOffset = wideOnXAxis ? width * 0.5f : 0.5f;
        float zOffset = wideOnXAxis ? 0.5f : width * 0.5f;
        gate.pos1 = min;
        gate.pos2 = max;
        gate.edgeMax = 90.f;
        gate.setPosition(min.getX() + xOffset, min.getY(), min.getZ() + zOffset);
    }

    @Override
    public void onGateStartOpen(EntityGate gate) {
        if (gate.world.isRemote) {
            return;
        }
        BlockPos min = BlockTools.getMin(gate.pos1, gate.pos2).add(0, 1, 0);
        BlockPos max = BlockTools.getMax(gate.pos1, gate.pos2);
        removeBetween(gate.world, min, max);

//  int heightAdj = max.getY() - min.getY();
//  BlockPos pos3 = max.copy();
//  pos3.getY() = min.getY();
//  adjustBounds(pos3, heightAdj, gate.gateOrientation);  
//  BlockPos minTemp = min.copy();
//  min = BlockTools.getMin(min, pos3);    
//  max = BlockTools.getMax(minTemp, pos3);
//  for(int x = min.getX(); x <= max.getX(); x++)
//    {
//    for(int y = min.getY(); y <=max.getY(); y++)
//      {
//      for(int z = min.getZ(); z<= max.getZ(); z++)
//        {
//        id = gate.world.getBlock(x, y, z);
//        if(id==Blocks.AIR)
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
        int heightAdj = max.getY() - min.getY();
        BlockPos pos3 = max.up(-heightAdj).offset(gate.gateOrientation, heightAdj);
        max = BlockTools.getMax(min, pos3);
        min = BlockTools.getMin(min, pos3);
        placeBetween(gate, min, max);
    }

    @Override
    public void onGateStartClose(EntityGate gate) {
        BlockPos min = BlockTools.getMin(gate.pos1, gate.pos2);
        BlockPos max = BlockTools.getMax(gate.pos1, gate.pos2);
        boolean widestOnXAxis = gate.pos1.getX() != gate.pos2.getX();
        int heightAdj = max.getY() - min.getY();
        BlockPos pos3 = max.up(-heightAdj).offset(gate.gateOrientation, heightAdj);
        max = BlockTools.getMax(min, pos3);
        min = BlockTools.getMin(min, pos3);
        Block id;
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    if ((widestOnXAxis && z == gate.pos1.getZ()) || (!widestOnXAxis && x == gate.pos1.getX())) {
                        continue;
                    }
                    id = gate.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (id == AWStructuresBlocks.gateProxy) {
                        gate.world.setBlockToAir(new BlockPos(x, y, z));
                    }
                }
            }
        }
    }

    @Override
    public void onGateFinishClose(EntityGate gate) {
        super.onGateFinishClose(gate);
//  boolean widestOnXAxis = gate.pos1.getX() != gate.pos2.getX();
//  int heightAdj = max.getY() - min.getY();
//  BlockPos pos3 = max.copy();
//  pos3.getY() = min.getY();
//  adjustBounds(pos3, heightAdj, gate.gateOrientation);  
//  BlockPos minTemp = min.copy();
//  min = BlockTools.getMin(min, pos3);    
//  max = BlockTools.getMax(minTemp, pos3);
//  Block id;
//  for(int x = min.getX(); x <= max.getX(); x++)
//    {
//    for(int y = min.getY(); y <=max.getY(); y++)
//      {
//      for(int z = min.getZ(); z<= max.getZ(); z++)
//        {
//        if((widestOnXAxis && z==gate.pos1.getZ()) || (!widestOnXAxis && x==gate.pos1.getX()))
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
