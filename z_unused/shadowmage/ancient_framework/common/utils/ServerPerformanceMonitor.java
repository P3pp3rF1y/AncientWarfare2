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
package shadowmage.ancient_framework.common.utils;

import java.util.EnumSet;

import shadowmage.ancient_framework.common.config.AWConfig;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ServerPerformanceMonitor implements ITickHandler
{

long[] tickTimes = new long[20];
long[] tickIntervals = new long[20];
long[] pathTickTimes = new long[20];
long[] npcTickTimes = new long[20];
long[] civicTickTimes = new long[20];
long[] vehicleTickTimes = new long[20];

int index = 0;
long startTime = System.nanoTime();
long prevStartTime = System.nanoTime();


public static long pathFindTimeThisTick = 0;
public static long npcTickTimeThisTick = 0;
public static long civicTickTimeThisTick = 0;
public static long vehicleTickTimeThisTick = 0;

public static long tickTime;
public static long tickPerSecond;
public static long pathTimeAverage;
public static long npcTimeAverage;
public static long civicTimeAverage;
public static long vehicleTimeAverage;

@Override
public void tickStart(EnumSet<TickType> type, Object... tickData)
  {
  if(!AWConfig.enableServerPerformanceMonitor){return;}
  if(index==20)
    {
    index = 0;    
    }  
  this.count();
  prevStartTime = startTime;
  startTime = System.nanoTime();
  this.tickIntervals[index] = startTime-prevStartTime;   
  }

@Override
public void tickEnd(EnumSet<TickType> type, Object... tickData)
  {  
  if(!AWConfig.enableServerPerformanceMonitor){return;}
  tickTimes[index] = System.nanoTime() - startTime;
  pathTickTimes[index] = pathFindTimeThisTick;
  npcTickTimes[index] = npcTickTimeThisTick;
  civicTickTimes[index] = civicTickTimeThisTick;
  vehicleTickTimes[index] = vehicleTickTimeThisTick;
  pathFindTimeThisTick = 0;
  npcTickTimeThisTick = 0;
  civicTickTimeThisTick = 0;
  vehicleTickTimeThisTick = 0;
  index++;
  }

public void count()
  {
  if(!AWConfig.enableServerPerformanceMonitor){return;}
  long total = 0;
//  long totalInterval = 0;
  long totalPathTime = 0;
  long totalNpcTime = 0;
  long totalCivicTime = 0;
  long totalVehicleTime = 0;
  for(int i = 0; i < 20; i++)
    {
    total += this.tickTimes[i];
//    totalInterval += this.tickIntervals[i];
    totalPathTime += this.pathTickTimes[i];
    totalNpcTime += this.npcTickTimes[i];
    totalCivicTime += this.civicTickTimes[i];
    totalVehicleTime += this.vehicleTickTimes[i];
    }   
  long avg = total/20;
//  long avgInterval = totalInterval/20;
  long tms = (avg/1000000)+1;
//  long tmsI = (avgInterval/1000000)+1; 
  int tps = (int)(1000/tms);  
  tickTime = avg;
  tickPerSecond = tps;
  pathTimeAverage = totalPathTime / 20;
  npcTimeAverage = totalNpcTime/20;
  civicTimeAverage = totalCivicTime / 20;
  vehicleTimeAverage = totalVehicleTime / 20;  
  }

public static void addPathfindingTime(long time)
  {
  pathFindTimeThisTick += time;
  }

public static void addNpcTickTime(long time)
  {
  npcTickTimeThisTick += time;
  }

public static void addCivicTickTime(long time)
  {
  civicTickTimeThisTick += time;
  }

public static void addVehicleTickTime(long time)
  {
  vehicleTickTimeThisTick += time;
  }

@Override
public EnumSet<TickType> ticks()
  {
  return EnumSet.of(TickType.SERVER);
  }

@Override
public String getLabel()
  {
  return "AW.TPS";
  }

}
