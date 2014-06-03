package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * had to freaking duplicate all of the private crap from the base field.<br>
 * why does MC have to have a ONLY private crap in a class that could GREATLY use some extension?<br>
 * @author Shadowmage
 *
 */
public class NpcNavigator extends PathNavigate
{

private EntityLiving entity;
private World world;
/** The PathEntity being followed. */
private PathEntity currentFollowingPath;
private double currentPathMoveSpeed;
/**
 * The number of blocks (extra) +/- in each axis that get pulled out as cache for the pathfinder's search space
 */
private IAttributeInstance pathfindRange;
private boolean shouldAvoidSun;
/** Time, in number of ticks, following the current path */
private int totalPathTickCount;
/**
 * The time when the last position check was done (to detect successful movement)
 */
private int ticksAtLastPosition;
/**
 * Coordinates of the entity's position last time a check was done (part of monitoring getting 'stuck')
 */
private Vec3 lastPositionCheck = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
/**
 * Specifically, if a wooden door block is even considered to be passable by the pathfinder
 */
private boolean canPassWoodDoors = true;
/** If door blocks are considered passable even when closed */
private boolean canPassClosedWoodDoors;
/** If water blocks are avoided (at least by the pathfinder) */
private boolean shouldNpcAvoidWater;
/**
 * If the entity can swim. Swimming AI enables this and the pathfinder will also cause the entity to swim straight
 * upwards when underwater
 */
private boolean canNpcSwim;

public NpcNavigator(EntityLiving par1EntityLiving, World par2World)
  {
  super(par1EntityLiving, par2World);
  this.entity = par1EntityLiving;
  this.world = par2World;
  this.pathfindRange = par1EntityLiving.getEntityAttribute(SharedMonsterAttributes.followRange);
  }

@Override
public void setAvoidsWater(boolean par1)
  {
  this.shouldNpcAvoidWater = par1;
  }

@Override
public boolean getAvoidsWater()
  {
  return this.shouldNpcAvoidWater;
  }

@Override
public void setBreakDoors(boolean par1)
  {
  this.canPassClosedWoodDoors = par1;
  }

/**
 * Sets if the entity can enter open doors
 */
@Override
public void setEnterDoors(boolean par1)
  {
  this.canPassWoodDoors = par1;
  }

/**
 * Returns true if the entity can break doors, false otherwise
 */
@Override
public boolean getCanBreakDoors()
  {
  return this.canPassClosedWoodDoors;
  }

/**
 * Sets if the path should avoid sunlight
 */
@Override
public void setAvoidSun(boolean par1)
  {
  this.shouldAvoidSun = par1;
  }

/**
 * Sets the speed
 */
@Override
public void setSpeed(double par1)
  {
  this.currentPathMoveSpeed = par1;
  }

/**
 * Sets if the entity can swim
 */
@Override
public void setCanSwim(boolean par1)
  {
  this.canNpcSwim = par1;
  }

/**
 * Gets the maximum distance that the path finding will search in.
 */
@Override
public float getPathSearchRange()
  {
  return (float)this.pathfindRange.getAttributeValue();
  }

/**
 * Returns the path to the given coordinates
 */
@Override
public PathEntity getPathToXYZ(double par1, double par3, double par5)
  {
  EntityLiving e = (EntityLiving) (this.entity.ridingEntity instanceof EntityLiving? this.entity.ridingEntity : this.entity);
  return !this.canNavigate() ? null : this.world.getEntityPathToXYZ(e, MathHelper.floor_double(par1), (int)par3, MathHelper.floor_double(par5), this.getPathSearchRange(), this.canPassWoodDoors, this.canPassClosedWoodDoors, this.shouldNpcAvoidWater, this.canNpcSwim);
  }

/**
 * Try to find and set a path to XYZ. Returns true if successful.
 */
@Override
public boolean tryMoveToXYZ(double par1, double par3, double par5, double par7)
  {
  PathEntity pathentity = this.getPathToXYZ((double)MathHelper.floor_double(par1), (double)((int)par3), (double)MathHelper.floor_double(par5));
  return this.setPath(pathentity, par7);
  }

/**
 * Returns the path to the given EntityLiving
 */
@Override
public PathEntity getPathToEntityLiving(Entity par1Entity)
  {
  EntityLiving e = (EntityLiving) (this.entity.ridingEntity instanceof EntityLiving? this.entity.ridingEntity : this.entity);
  return !this.canNavigate() ? null : this.world.getPathEntityToEntity(e, par1Entity, this.getPathSearchRange(), this.canPassWoodDoors, this.canPassClosedWoodDoors, this.shouldNpcAvoidWater, this.canNpcSwim);
  }

/**
 * Try to find and set a path to EntityLiving. Returns true if successful.
 */
@Override
public boolean tryMoveToEntityLiving(Entity par1Entity, double par2)
  {
  PathEntity pathentity = this.getPathToEntityLiving(par1Entity);
  return pathentity != null ? this.setPath(pathentity, par2) : false;
  }

/**
 * sets the active path data if path is 100% unique compared to old path, checks to adjust path for sun avoiding
 * ents and stores end coords
 */
@Override
public boolean setPath(PathEntity par1PathEntity, double par2)
  {
  if (par1PathEntity == null)
    {
    this.currentFollowingPath = null;
    return false;
    }
  else
    {
    if (!par1PathEntity.isSamePath(this.currentFollowingPath))
      {
      this.currentFollowingPath = par1PathEntity;
      }
    if (this.shouldAvoidSun)
      {
      this.removeSunnyPath();
      }
    if (this.currentFollowingPath.getCurrentPathLength() == 0)
      {
      return false;
      }
    else
      {
      this.currentPathMoveSpeed = par2;
      Vec3 vec3 = this.getEntityPosition();
      this.ticksAtLastPosition = this.totalPathTickCount;
      this.lastPositionCheck.xCoord = vec3.xCoord;
      this.lastPositionCheck.yCoord = vec3.yCoord;
      this.lastPositionCheck.zCoord = vec3.zCoord;
      return true;
      }
    }
  }

/**
 * gets the actively used PathEntity
 */
@Override
public PathEntity getPath()
  {
  return this.currentFollowingPath;
  }

@Override
public void onUpdateNavigation()
  {
  this.totalPathTickCount++;
  if(!this.noPath())
    {
    if(this.canNavigate())
      {
      this.pathFollow();
      }
    if(!this.noPath())
      {
      Vec3 vec3 = this.currentFollowingPath.getPosition(this.entity);
      if(vec3 != null)
        {
        this.entity.getMoveHelper().setMoveTo(vec3.xCoord, vec3.yCoord, vec3.zCoord, this.currentPathMoveSpeed);
        }
      }
    }
  }

/**
 * If null path or reached the end
 */
@Override
public boolean noPath()
  {
  return this.currentFollowingPath == null || this.currentFollowingPath.isFinished();
  }

 /**
  * sets active PathEntity to null
  */
@Override
 public void clearPathEntity()
   {
   this.currentFollowingPath = null;
   }

private void pathFollow()
  {
  Vec3 vec3 = this.getEntityPosition();
  int i = this.currentFollowingPath.getCurrentPathLength();

  for(int j = this.currentFollowingPath.getCurrentPathIndex(); j < this.currentFollowingPath.getCurrentPathLength(); ++j)
    {
    if(this.currentFollowingPath.getPathPointFromIndex(j).yCoord != (int)vec3.yCoord)
      {
      i = j;
      break;
      }
    }
  float f = this.entity.width * this.entity.width;
  int k;
  for(k = this.currentFollowingPath.getCurrentPathIndex(); k < i; ++k)
    {
    if(vec3.squareDistanceTo(this.currentFollowingPath.getVectorFromIndex(this.entity, k)) < (double)f)
      {
      this.currentFollowingPath.setCurrentPathIndex(k + 1);
      }
    }
  k = MathHelper.ceiling_float_int(this.entity.width);
  int l = (int)this.entity.height + 1;
  int i1 = k;
  for(int j1 = i - 1; j1 >= this.currentFollowingPath.getCurrentPathIndex(); --j1)
    {
    if(this.isDirectPathBetweenPoints(vec3, this.currentFollowingPath.getVectorFromIndex(this.entity, j1), k, l, i1))
      {
      this.currentFollowingPath.setCurrentPathIndex(j1);
      break;
      }
    }
  if(this.totalPathTickCount - this.ticksAtLastPosition > 100)
    {
    if (vec3.squareDistanceTo(this.lastPositionCheck) < 2.25D)
      {
      this.clearPathEntity();
      }

    this.ticksAtLastPosition = this.totalPathTickCount;
    this.lastPositionCheck.xCoord = vec3.xCoord;
    this.lastPositionCheck.yCoord = vec3.yCoord;
    this.lastPositionCheck.zCoord = vec3.zCoord;
    }
  }

 private Vec3 getEntityPosition()
   {
   return this.world.getWorldVec3Pool().getVecFromPool(this.entity.posX, (double)this.getPathableYPos(), this.entity.posZ);
   }

 /**
  * Gets the safe pathing Y position for the entity depending on if it can path swim or not
  */
 private int getPathableYPos()
   {
   if(this.entity.isInWater() && this.canNpcSwim)
     {
     int i = (int)this.entity.boundingBox.minY;
     Block block = this.world.getBlock(MathHelper.floor_double(this.entity.posX), i, MathHelper.floor_double(this.entity.posZ));
     int j = 0;

     do
       {
       if(block != Blocks.flowing_water && block != Blocks.water)
         {
         return i;
         }

       ++i;
       block = this.world.getBlock(MathHelper.floor_double(this.entity.posX), i, MathHelper.floor_double(this.entity.posZ));
       ++j;
       }
     while(j <= 16);

     return (int)this.entity.boundingBox.minY;
     }
   else
     {
     return (int)(this.entity.boundingBox.minY + 0.5D);
     }
   }

 /**
  * If on ground or swimming and can swim
  */
 private boolean canNavigate()
   {
   return this.entity.onGround || this.canNpcSwim && this.isInLiquid() || this.entity.ridingEntity!=null;
   }

 /**
  * Returns true if the entity is in water or lava, false otherwise
  */
 private boolean isInLiquid()
   {
   return this.entity.isInWater() || this.entity.handleLavaMovement();
   }

 /**
  * Trims path data from the end to the first sun covered block
  */
 private void removeSunnyPath()
   {
   if (!this.world.canBlockSeeTheSky(MathHelper.floor_double(this.entity.posX), (int)(this.entity.boundingBox.minY + 0.5D), MathHelper.floor_double(this.entity.posZ)))
     {
     for (int i = 0; i < this.currentFollowingPath.getCurrentPathLength(); ++i)
       {
       PathPoint pathpoint = this.currentFollowingPath.getPathPointFromIndex(i);

       if (this.world.canBlockSeeTheSky(pathpoint.xCoord, pathpoint.yCoord, pathpoint.zCoord))
         {
         this.currentFollowingPath.setCurrentPathLength(i - 1);
         return;
         }
       }
     }
   }

 /**
  * Returns true when an entity of specified size could safely walk in a straight line between the two points. Args:
  * pos1, pos2, entityXSize, entityYSize, entityZSize
  */
 private boolean isDirectPathBetweenPoints(Vec3 par1Vec3, Vec3 par2Vec3, int par3, int par4, int par5)
   {
   int l = MathHelper.floor_double(par1Vec3.xCoord);
   int i1 = MathHelper.floor_double(par1Vec3.zCoord);
   double d0 = par2Vec3.xCoord - par1Vec3.xCoord;
   double d1 = par2Vec3.zCoord - par1Vec3.zCoord;
   double d2 = d0 * d0 + d1 * d1;

   if(d2 < 1.0E-8D)
     {
     return false;
     }
   else
     {
     double d3 = 1.0D / Math.sqrt(d2);
     d0 *= d3;
     d1 *= d3;
     par3 += 2;
     par5 += 2;

     if(!this.isSafeToStandAt(l, (int)par1Vec3.yCoord, i1, par3, par4, par5, par1Vec3, d0, d1))
       {
       return false;
       }
     else
       {
       par3 -= 2;
       par5 -= 2;
       double d4 = 1.0D / Math.abs(d0);
       double d5 = 1.0D / Math.abs(d1);
       double d6 = (double)(l * 1) - par1Vec3.xCoord;
       double d7 = (double)(i1 * 1) - par1Vec3.zCoord;

       if (d0 >= 0.0D)
         {
         ++d6;
         }

       if (d1 >= 0.0D)
         {
         ++d7;
         }

       d6 /= d0;
       d7 /= d1;
       int j1 = d0 < 0.0D ? -1 : 1;
       int k1 = d1 < 0.0D ? -1 : 1;
       int l1 = MathHelper.floor_double(par2Vec3.xCoord);
       int i2 = MathHelper.floor_double(par2Vec3.zCoord);
       int j2 = l1 - l;
       int k2 = i2 - i1;

       do{
         if (j2 * j1 <= 0 && k2 * k1 <= 0)
           {
           return true;
           }

         if (d6 < d7)
           {
           d6 += d4;
           l += j1;
           j2 = l1 - l;
           }
         else
           {
           d7 += d5;
           i1 += k1;
           k2 = i2 - i1;
           }
         }
       while(this.isSafeToStandAt(l, (int)par1Vec3.yCoord, i1, par3, par4, par5, par1Vec3, d0, d1));

       return false;
       }
     }
   }

 /**
  * Returns true when an entity could stand at a position, including solid blocks under the entire entity. Args:
  * xOffset, yOffset, zOffset, entityXSize, entityYSize, entityZSize, originPosition, vecX, vecZ
  */
 private boolean isSafeToStandAt(int par1, int par2, int par3, int par4, int par5, int par6, Vec3 par7Vec3, double par8, double par10)
   {
   int k1 = par1 - par4 / 2;
   int l1 = par3 - par6 / 2;

   if (!this.isPositionClear(k1, par2, l1, par4, par5, par6, par7Vec3, par8, par10))
     {
     return false;
     }
   else
     {
     for (int i2 = k1; i2 < k1 + par4; ++i2)
       {
       for (int j2 = l1; j2 < l1 + par6; ++j2)
         {
         double d2 = (double)i2 + 0.5D - par7Vec3.xCoord;
         double d3 = (double)j2 + 0.5D - par7Vec3.zCoord;

         if (d2 * par8 + d3 * par10 >= 0.0D)
           {
           Block block = this.world.getBlock(i2, par2 - 1, j2);
           Material material = block.getMaterial();

           if (material == Material.air)
             {
             return false;
             }

           if (material == Material.water && !this.entity.isInWater())
             {
             return false;
             }

           if (material == Material.lava)
             {
             return false;
             }
           }
         }
       }

     return true;
     }
   }

 /**
  * Returns true if an entity does not collide with any solid blocks at the position. Args: xOffset, yOffset,
  * zOffset, entityXSize, entityYSize, entityZSize, originPosition, vecX, vecZ
  */
 private boolean isPositionClear(int par1, int par2, int par3, int par4, int par5, int par6, Vec3 par7Vec3, double par8, double par10)
   {
   for (int k1 = par1; k1 < par1 + par4; ++k1)
     {
     for (int l1 = par2; l1 < par2 + par5; ++l1)
       {
       for (int i2 = par3; i2 < par3 + par6; ++i2)
         {
         double d2 = (double)k1 + 0.5D - par7Vec3.xCoord;
         double d3 = (double)i2 + 0.5D - par7Vec3.zCoord;

         if (d2 * par8 + d3 * par10 >= 0.0D)
           {
           Block block = this.world.getBlock(k1, l1, i2);

           if (!block.getBlocksMovement(this.world, k1, l1, i2))
             {
             return false;
             }
           }
         }
       }
     }

   return true;
   }
}
