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

public static int fishFarmRescanTicks = 200;
public static int animalFarmRescanTicks = 200;

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
