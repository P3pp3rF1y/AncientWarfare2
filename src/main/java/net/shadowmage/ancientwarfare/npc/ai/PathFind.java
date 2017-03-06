package net.shadowmage.ancientwarfare.npc.ai;

import java.lang.reflect.Array;
import java.util.HashSet;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.npc.ai.PathFind.BlockAvoidEntry;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;

public class PathFind extends PathFinder{
    private static final int RAIL = 9, FENCE = 11;
    private final boolean doorAllowed;
    private final boolean closedPath;
    public PathFind(IBlockAccess world, boolean openDoor, boolean closedDoor, boolean water, boolean drown) {
        super(world, openDoor, closedDoor, water, drown);
        this.doorAllowed = openDoor;
        this.closedPath = closedDoor;
    }
    
    private static BlockAvoidEntry[] BLOCKS_TO_AVOID;
    
    /**
     * Internal implementation of creating a path from an entity to a point
     */
    @Override
    protected PathEntity createEntityPathTo(Entity entity, double targetX, double targetY, double targetZ, float maxDistance) {
        this.path.clearPath();
        this.pointMap.clearMap();
        boolean isPathingInWater = this.isPathingInWater;
        int startY = MathHelper.floor_double(entity.boundingBox.minY + 0.5D);

        if (this.canEntityDrown && entity.isInWater()) {
            startY = (int)entity.boundingBox.minY;
            for (Block block = this.worldMap.getBlock(MathHelper.floor_double(entity.posX), startY, MathHelper.floor_double(entity.posZ)); block == Blocks.flowing_water || block == Blocks.water; block = this.worldMap.getBlock(MathHelper.floor_double(entity.posX), startY, MathHelper.floor_double(entity.posZ)))
                ++startY;
            this.isPathingInWater = false;
        }
        
        int endX = MathHelper.floor_double(targetX - (double)(entity.width / 2.0F));
        int endY = MathHelper.floor_double(targetY);
        int endZ = MathHelper.floor_double(targetZ - (double)(entity.width / 2.0F));
        int unusedX = MathHelper.floor_float(entity.width + 1.0F);
        int unusedY = MathHelper.floor_float(entity.height + 1.0F);
        int unusedZ = MathHelper.floor_float(entity.width + 1.0F);
        
        int[] origin = getNearestClearPos(entity, startY);

        PathPoint pointStart = this.openPoint(origin[0], startY, origin[1]);
        PathPoint pointEnd = this.openPoint(endX, endY, endZ);
        PathPoint pointUnused = new PathPoint(unusedX, unusedY, unusedZ);
        PathEntity pathentity = this.addToPath(entity, pointStart, pointEnd, pointUnused, maxDistance);
        this.isPathingInWater = isPathingInWater;
        return pathentity;
    }
    
    private int[] getNearestClearPos(final Entity entity, final int startY) {
        int[] origin = new int[] {0, 0};
        
        // Vanilla way - checks the closest block to the entity's NW corner.
        // we won't bother with this, it's so unreliable
        /*
        origin[0] = MathHelper.floor_double(entity.boundingBox.minX);
        origin[1] = MathHelper.floor_double(entity.boundingBox.minZ);
        if (positionAllowsMovement(origin, startY))
            return origin;
        */
        // try rounding to nearest (this is usually good enough)
        origin[0] = (int) Math.round(entity.boundingBox.minX);
        origin[1] = (int) Math.round(entity.boundingBox.minZ);
        if (positionAllowsMovement(origin, startY))
            return origin;
        // try the NE corner
        origin[0] = (int) Math.round(entity.boundingBox.maxX);
        if (positionAllowsMovement(origin, startY))
            return origin;
        // try the SE corner
        origin[1] = (int) Math.round(entity.boundingBox.maxZ);
        if (positionAllowsMovement(origin, startY))
            return origin;
        // try the SW corner
        origin[0] = (int) Math.round(entity.boundingBox.minX);
        if (positionAllowsMovement(origin, startY))
            return origin;
        
        // Tough luck. Return vanilla values and fix it yourself! 
        origin[0] = MathHelper.floor_double(entity.boundingBox.minX);
        origin[1] = MathHelper.floor_double(entity.boundingBox.minZ);
        return origin;
    }

    private boolean positionAllowsMovement(final int[] origin, final int startY) {
        return !this.worldMap.getBlock(origin[0], startY, origin[1]).getMaterial().blocksMovement();
    }

    /**
     * Returns a mapped point or creates and adds one
     */
    private final PathPoint openPoint(int p_75854_1_, int p_75854_2_, int p_75854_3_) {
        int l = PathPoint.makeHash(p_75854_1_, p_75854_2_, p_75854_3_);
        PathPoint pathpoint = (PathPoint)this.pointMap.lookup(l);

        if (pathpoint == null)
        {
            pathpoint = new PathPoint(p_75854_1_, p_75854_2_, p_75854_3_);
            this.pointMap.addKey(l, pathpoint);
        }

        return pathpoint;
    }

    /**
     * Checks if an entity collides with blocks at a position.
     * Returns 1 if clear, 0 for colliding with any solid block,
     * -1 for water(if avoiding water) but otherwise clear, -2 for lava, -3 for fence, -4 for closed trapdoor,
     * 2 if otherwise clear except for open trapdoor or water(if not avoiding)
     */
    @Override
    public int getVerticalOffset(Entity entity, int posX, int posY, int posZ, PathPoint target){
        boolean trapOrWater = false;

        for (int l = posX; l < posX + target.xCoord; ++l)
        {
            for (int i1 = posY; i1 < posY + target.yCoord; ++i1)
            {
                for (int j1 = posZ; j1 < posZ + target.zCoord; ++j1)
                {
                    Block block = entity.worldObj.getBlock(l, i1, j1);

                    if (block.getMaterial() != Material.air)
                    {
                        if (block instanceof BlockTrapDoor)
                        {
                            trapOrWater = true;
                        }
                        else if (block.getMaterial() == Material.water)
                        {
                            if (isPathingInWater)
                            {
                                return -1;
                            }
                            trapOrWater = true;
                        }
                        else
                        {
                            if (!doorAllowed && isDoor(block))
                            {
                                return 0;
                            }
                        }

                        int k1 = block.getRenderType();
                        if (k1 == RAIL)
                        {
                            int j2 = MathHelper.floor_double(entity.posX);
                            int l1 = MathHelper.floor_double(entity.posY);
                            int i2 = MathHelper.floor_double(entity.posZ);
                            if (entity.worldObj.getBlock(j2, l1, i2).getRenderType() != RAIL && entity.worldObj.getBlock(j2, l1 - 1, i2).getRenderType() != RAIL)
                            {
                                return -3;
                            }
                        }
                        else if (!block.getBlocksMovement(entity.worldObj, l, i1, j1) && (!closedPath || !isDoor(block)))
                        {
                            if (k1 == FENCE || block instanceof BlockFenceGate || block instanceof BlockWall)
                            {
                                return -3;
                            }
                            if (block instanceof BlockTrapDoor)
                            {
                                return -4;
                            }
                            if (block.getMaterial() != Material.lava)
                            {
                                return 0;
                            }
                            if (!entity.handleLavaMovement())
                            {
                                return -2;
                            }
                        }
                    } else {
                        block = entity.worldObj.getBlock(l, i1 - 1, j1);
                        if (AWNPCStatics.pathfinderAvoidFences) {
                            if (block.getRenderType() == FENCE || block instanceof BlockFence || block instanceof BlockWall) {
                                return -2;
                            }
                        }
                        if (AWNPCStatics.pathfinderAvoidChests) {
                            if (block.getRenderType() == 22 || block instanceof BlockChest) {
                                return -2;
                            }
                        }
                        if (BLOCKS_TO_AVOID == null)
                            blockBlacklistInit();

                        if (blockBlacklistContains(block, entity.worldObj.getBlockMetadata(l, i1, j1))) {
                            return -2;
                        }
                    }
                }
            }
        }

        return trapOrWater ? 2 : 1;
    }

    private void blockBlacklistInit() {
        HashSet<BlockAvoidEntry> blocksToAvoid = new HashSet<BlockAvoidEntry>();
        AncientWarfareCore.log.info("Building pathfinding block exclusion custom list...");
        String[] avoidList = AWNPCStatics.pathfinderAvoidCustom.split(";");
        for (String blockName : avoidList) {
            blockName = blockName.trim();
            if (!blockName.equals("")) {
                String[] blockId = blockName.split(":");
                if (Array.getLength(blockId) != 2 && Array.getLength(blockId) != 3 ) {
                    AncientWarfareCore.log.warn(" - Invalid block (bad length of " + Array.getLength(blockId) + "): " + blockName);
                    continue;
                }
                if (blockId[0] == null || blockId[1] == null) {
                    AncientWarfareCore.log.warn(" - Invalid block (parse/format error): " + blockName);
                    continue;
                }
                Block block = GameRegistry.findBlock(blockId[0], blockId[1]);
                if (block == null) {
                    AncientWarfareCore.log.warn(" - Block not found: " + blockName);
                    continue;
                }
                int meta = -1;
                if (Array.getLength(blockId) == 3) {
                    try {
                        meta = Integer.parseInt(blockId[2]);
                        if (meta < 0 || meta > 15)
                            throw new NumberFormatException();
                    } catch (NumberFormatException e) {
                        AncientWarfareCore.log.warn(" - Meta value invalid : '" + blockId[2] + "', must be a number between 0 and 15");
                        continue;
                    }
                }
                blocksToAvoid.add(new BlockAvoidEntry(block, meta));
            }
        }
        
        BLOCKS_TO_AVOID = blocksToAvoid.toArray(new BlockAvoidEntry[0]);
        AncientWarfareCore.log.info("...added " + BLOCKS_TO_AVOID.length + " blocks to pathfinding blacklist");
    }
    
    private boolean blockBlacklistContains(Block block, int meta) {
        for (BlockAvoidEntry blockAvoidEntry : BLOCKS_TO_AVOID) {
            if (blockAvoidEntry.block == block) {
                if (blockAvoidEntry.meta == -1)
                    return true;
                if (blockAvoidEntry.meta == meta)
                    return true;
            }
        }
        return false;
    }

    private boolean isDoor(Block block) {
        return block instanceof BlockDoor || block instanceof BlockFenceGate;
    }
    
    public class BlockAvoidEntry {
        protected final Block block;
        protected final int meta;
        
        public BlockAvoidEntry (Block block, int meta) {
            this.block = block;
            this.meta = meta;
        }
    }
}
