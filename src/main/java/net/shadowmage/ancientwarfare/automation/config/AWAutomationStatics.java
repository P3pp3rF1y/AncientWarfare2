/**
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
package net.shadowmage.ancientwarfare.automation.config;

import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.config.ModConfiguration;

public class AWAutomationStatics extends ModConfiguration
{

/**
 * If true, eligible worksites will send their work-target tiles to clients.<br>
 * Potentially network intensive, should only be left enabled on small servers or in single-player.<br>
 */
public static boolean sendWorkToClients = true;

/**
 * If true, mechanical workers will consume fuel placed into their input slot.<br>
 * If false, you will not even be able to open the mechanical worker GUI.<br>
 */
public static boolean enableMechanicalWorkerFuelUse = true;

/**
 * How many energy units are produced by each worker 'work' tick<br>
 * These units are equivalent to BuildCraft MJ and used interchangeably 
 */
public static int energyPerWorkUnit = 50;//equivalent to MJ

/**
 * How often is a worksite allowed to do a forced rescan of its work-bounds?<br>
 */
public static int automationWorkerRescanTicks = 200;

/**
 * Travel time per block when sending/receiving items using the mailbox system<br>
 * Distances are calculated as a floating point distance and rounded to the nearest whole<br>
 */
public static int mailboxTimePerBlock = 20;

/**
 * Travel time for mail using mailboxes when items are being sent/received in different dimensions
 */
public static int mailboxTimeForDimension = 1200;

public AWAutomationStatics(Configuration config)
  {
  super(config);
  }

@Override
public void initializeCategories()
  {
  
  }

@Override
public void initializeValues()
  {
  this.config.save();
  }

}
