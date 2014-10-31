package net.shadowmage.ancientwarfare.vehicle.ballistics;

import net.minecraft.util.MathHelper;
import net.shadowmage.ancientwarfare.core.util.Trig;

public class TrajectoryPlotter
{

/**
 * 
 * @param dx delta X (diff between launch and target position)
 * @param dy delta Y (diff between launch and target position)
 * @param dz delta Z (diff between launch and target position)
 * @param angle the specified launch angle (single, non-adjustable)
 * @param maxPower the specified max launch power
 * @return power needed to hit target, or 0 if beyond maxPower value
 */
public static final double getPowerFor(double dx, double dy, double dz, float angle, float maxPower)
  {
  double len = MathHelper.sqrt_double(dx*dx+dz*dz);
  double pow = iterativeSpeedFinder(len, dy, angle, 5);
  if(pow<=maxPower){return pow;}
  return 0;//TODO what to return for an invalid power setting?
  }

public static final double getAngleFor(double dx, double dy, double dz, float minAngle, float maxAngle, float velocity)
  {
  double len = MathHelper.sqrt_double(dx*dx+dz*dz);
  double[] angles = getLaunchAngleToHit(len, dy, velocity);
  if(angles[0]>=minAngle && angles[0]<=maxAngle){return angles[0];}
  else if(angles[1]>=minAngle && angles[1]<=maxAngle){return angles[1];}
  return 0;//TODO what to return for an invalid angle?
  }

/**
 * 
 * @param x input hit x (horizontal distance)
 * @param y input hit y (vertical distance)
 * @param v velocity per second
 * @param g gravity per second
 * @return an array containing the two launch angles that can hit the specified position
 */
private static double[] getLaunchAngleToHit(double x, double y, double v)
  {
  double v2 = v*v;
  double v4 = v*v*v*v;
  double x2 = x*x;  
  double sqRtVal = Math.sqrt(v4 - Trig.GRAVITY * (Trig.GRAVITY*x2 + 2*y*v2));  
  double h = v2 + sqRtVal;
  double l = v2 - sqRtVal;  
  h /= Trig.GRAVITY*x;
  l /= Trig.GRAVITY*x;  
  h = Trig.toDegrees((float) Math.atan(h));
  l = Trig.toDegrees((float) Math.atan(l));  
  return new double[]{h, l};  
  }

private static double iterativeSpeedFinder(double x, double y, float angle, int maxIterations)
  {  
  angle = 90-angle;
  float bestVelocity = 0.f;
  float velocityIncrement = 10.f;
  float testVelocity = 10.f;
  float gravityTick = 9.81f *0.05f*0.05f;
  float posX = 0;
  float posY = 0;
  float motX = 0;
  float motY = 0;
  float hitX = 0;
  boolean hitGround = true;
  float hitDiffX;
  float hitDiffY;            
  float hitPercent;
  for(int iter = 0; iter < maxIterations; iter++)
    {
    //reset pos
    //calc initial motion from input angle and current testVelocity
    hitGround = true;
    posX = 0.f;
    posY = 0.f;
    motX = Trig.sinDegrees(angle)*testVelocity*0.05f;
    motY = Trig.cosDegrees(angle)*testVelocity*0.05f;   
    while(motY>=0 || posY >= y)
      {
      //move
      //check hit
      //apply gravity if not hit
      posX+=motX;
      posY+=motY;
      if(posX>x)
        {
        hitGround = false;
        break;//missile went too far
        }     
      motY-=gravityTick;        
      }    
    if(hitGround)//if break was triggered by going negative on y axis, get a more precise hit vector
      {
      motY+=gravityTick;
      hitDiffX = motX - posX;
      hitDiffY = motY - posY;            
      hitPercent = (float) ((y - posY) / hitDiffY);
      hitX = posX + hitDiffX * hitPercent;
      } 
    if(hitGround && hitX < x)// hit was not far enough, increase power
      {
      bestVelocity = testVelocity;
      testVelocity += velocityIncrement;        
      }
    else//it was too far, go back to previous power, decrease increment, increase by new increment
      {
      testVelocity -= velocityIncrement;
      bestVelocity = testVelocity;
      velocityIncrement *= 0.5f;
      testVelocity +=velocityIncrement;
      } 
    } 
  return bestVelocity;
  }

/**
 * TODO
 * gets the max effective range of the unit at a given firing angle
 * @param angle
 * @return
 */
public static double getMaxRange(float angle, float v)
  {
  return 0;
  }

/**
 * TODO test this -- likely faster than iterative finder, but I remember seeing this one before and it being useless for some reason..
 * http://gamedev.stackexchange.com/questions/17467/calculating-velocity-needed-to-hit-target-in-parabolic-arc
 * @param x
 * @param y
 * @param o
 * @return
 */
private static double getLaunchPower(double x, double y, double o)
  {
  double g = Trig.GRAVITY;
  double v = (Math.sqrt(g) * Math.sqrt(x) * Math.sqrt((Math.tan(o)*Math.tan(o))+1)) / Math.sqrt(2 * Math.tan(o) - (2 * g * y) / x); // velocity
  return v;
  }

}
