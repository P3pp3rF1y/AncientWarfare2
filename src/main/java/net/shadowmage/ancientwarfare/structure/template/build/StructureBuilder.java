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
package net.shadowmage.ancientwarfare.structure.template.build;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.TemplateRule;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntity;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;

public class StructureBuilder implements IStructureBuilder {

    protected int minX;
    protected int minZ;
    protected int maxX;
    protected int maxZ;
    protected StructureTemplate template;
    protected World world;
    protected BlockPos buildOrigin;
    protected EnumFacing buildFace;
    protected int turns;
    protected int maxPriority = 4;
    protected int currentPriority;//current build priority...may not be needed anymore?
    protected int currentX, currentY, currentZ;//coords in template
    protected int destXSize, destYSize, destZSize;
    protected BlockPos destination;

    protected StructureBB bb;

    private boolean isFinished = false;

    public StructureBuilder(World world, StructureTemplate template, EnumFacing face, BlockPos pos, StructureBB bb) {
        this(world, template, face, pos, bb, 0, 0, template.xSize, template.zSize);
    }
    public StructureBuilder(World world, StructureTemplate template, EnumFacing face, BlockPos pos) {
        this(world, template, face, pos, new StructureBB(pos, face, template));
    }

    public StructureBuilder(World world, StructureTemplate template, EnumFacing face, BlockPos buildKey, StructureBB bb, int minX, int minZ, int maxX, int maxZ) {
        this.world = world;
        this.template = template;
        this.buildFace = face;
        this.bb = bb;
        buildOrigin = buildKey;
        destination = BlockPos.ORIGIN;
        currentX = currentY = currentZ = 0;
        destXSize = template.xSize;
        destYSize = template.ySize;
        destZSize = template.zSize;
        currentPriority = 0;

        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;

        turns = ((face.getHorizontalIndex() + 2) % 4);
        int swap;
        for (int i = 0; i < turns; i++) {
            swap = destXSize;
            destXSize = destZSize;
            destZSize = swap;
        }
        /*
         * initialize the first target destination so that the structure is ready to start building when called on to build
         */
        incrementDestination();
    }

    public StructureTemplate getTemplate() {
        return template;
    }

    public StructureBB getBoundingBox() {
        return bb;
    }

    protected StructureBuilder() {
        destination = BlockPos.ORIGIN;
        buildOrigin = BlockPos.ORIGIN;
    }

    public void instantConstruction() {
        try {
            while (!this.isFinished()) {
                TemplateRule rule = template.getRuleAt(currentX, currentY, currentZ);
                placeCurrentPosition(rule);
                increment();
            }
        } catch (Exception e) {
            TemplateRule rule = template.getRuleAt(currentX, currentY, currentZ);
            throw new RuntimeException("Caught exception while constructing template blocks: " + rule, e);
        }
        this.placeEntities();
    }

    protected void placeEntities() {
        TemplateRuleEntity[] rules = template.getEntityRules();
        for (TemplateRuleEntity rule : rules) {
            if (rule == null || !isPositionInLimits(rule.getPosition())) {
                continue;
            }
            destination = BlockTools.rotateInArea(rule.getPosition(), template.xSize, template.zSize, turns).add(bb.min);
            try {
                rule.handlePlacement(world, turns, destination, this);
            } catch (StructureBuildingException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isPositionInLimits(BlockPos position) {
        int x = position.getX();
        int z = position.getZ();

        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }

    /*
     * should be called by template-rules to handle block-placement in the world.
     * Handles village-block swapping during world-gen, and chunk-insert for blocks
     * with priority > 0
     */
    @Override
    public void placeBlock(BlockPos pos, Block block, int meta, int priority) {
        if (pos.getY() <= 0 || pos.getY() >= world.getHeight()) {
            return;
        }
        IBlockState state = block.getStateFromMeta(meta);
        int updateFlag = state.canProvidePower() ? 3 : 2;
        world.setBlockState(pos, state, updateFlag);
        //TODO this used to be more complicated for perf reasons - look into whether needs to be recreated
//        Chunk chunk = world.getChunkFromBlockCoords(x, z);
//        ExtendedBlockStorage stc = chunk.getBlockStorageArray()[y >> 4];
//        if (stc == null)//A block in a void subchunk
//        {
//            if(block != Blocks.AIR)//Not changing anything
//                world.setBlockState(pos, block.getStateFromMeta(meta), 2);//using flag=2 -- no block update, but still send to clients (should help with issues of things popping off)
//        } else {//unsurprisingly, direct chunk access is 2X faster than going through the world =\
//            int cx = x & 15; //bitwise-and to scrub all bits above 15
//            int cz = z & 15; //bitwise-and to scrub all bits above 15
//            chunk.removeTileEntity(cx, y, cz);
//            stc.func_150818_a(cx, y & 15, cz, block);
//            stc.setExtBlockMetadata(cx, y & 15, cz, meta);
//            if (block.hasTileEntity(block.getStateFromMeta(meta))) {
//                TileEntity te = block.createTileEntity(world, meta);
//                if(te != null) {
//                    chunk.func_150812_a(cx, y, cz, te);//set TE in chunk data
//                    world.addTileEntity(te);//add TE to world added/loaded TE list
//                }
//            }
//            BlockTools.notifyBlockUpdate(world, pos);
//            //TODO clean this up to send own list of block-changes, not rely upon vanilla to send changes. (as the client-side of this lags to all hell)
//        }
    }

    protected void placeCurrentPosition(TemplateRule rule) {
        if (rule == null) {
            if(currentPriority == 0) {
                placeAir();
            }
        }
        else if (rule.shouldPlaceOnBuildPass(world, turns, destination, currentPriority)) {
            this.placeRule(rule);
        }
    }

    protected boolean increment() {
        if (isFinished) {
            return false;
        }
        if (incrementPosition()) {
            incrementDestination();
        } else {
            this.isFinished = true;
        }
        return !isFinished;
    }

    protected void placeAir() {
        if (!template.getValidationSettings().isPreserveBlocks()) {
            template.getValidationSettings().handleClearAction(world, destination, template, bb);
        }
    }

    protected void placeRule(TemplateRule rule) {
        if (destination.getY() <= 0) {
            return;
        }
        try {
            rule.handlePlacement(world, turns, destination, this);
        } catch (StructureBuildingException e) {
            e.printStackTrace();
        }
    }

    protected void incrementDestination() {
        destination = BlockTools.rotateInArea(new BlockPos(currentX, currentY, currentZ), template.xSize, template.zSize, turns).add(bb.min);
    }

    /*
     * return true if could increment position
     * return false if template is finished
     */
    protected boolean incrementPosition() {
        currentX++;
        if (currentX > maxX) {
            currentX = minX;
            currentZ++;
            if (currentZ > maxZ) {
                currentZ = minZ;
                currentY++;
                if (currentY >= template.ySize) {
                    currentY = 0;
                    currentPriority++;
                    if (currentPriority > maxPriority) {
                        currentPriority = 0;
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public float getPercentDoneWithPass() {
        float max = template.xSize * template.zSize * template.ySize;
        float current = currentY * (template.xSize * template.zSize);//add layers done
        current += currentZ * template.xSize;//add rows done
        current += currentX;//add blocks done
        return current / max;
    }

    public int getPass() {
        return currentPriority;
    }

    public int getMaxPasses() {
        return maxPriority;
    }

}
