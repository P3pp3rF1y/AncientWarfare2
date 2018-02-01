/**
 * Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
 * This software is distributed under the terms of the GNU General Public License.
 * Please see COPYING for precise license information.
 * <p>
 * This file is part of Ancient Warfare.
 * <p>
 * Ancient Warfare is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Ancient Warfare is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */

package shadowmage.ancient_warfare.common.targeting;

/**
 * used by target/aggro entries and waypoints, to determine the 'type' of the target/point
 *
 * @author Shadowmage
 */
public enum TargetType {
	ATTACK,
	MOUNT,
	REPAIR,
	HEAL,
	FOLLOW,
	WANDER,
	PATROL,
	MOVE,
	SHELTER,
	FLEE,
	NONE,
	WORK,
	FARM_PLANT,
	FARM_HARVEST,
	BARN_BREED,
	BARN_CULL,
	BUILD_CLEAR,
	BUILD_PLACE,
	MINE_CLEAR,
	//
	MINE_LADDER,
	//for the central vertical shaft
	MINE_FILL,
	//for holes in the wall/roof/floor
	MINE_TORCH,
	//for some tunnel/branch lines
	MINE_CLEAR_RESOURCE,
	TREE_CHOP,
	TREE_PLANT,
	PICKUP,
	DELIVER,
	ATTACK_TILE,
	UPKEEP,
	COMMANDER,
	BARN_MILK,
	BARN_SHEAR,
	FISH_CATCH,
	CRAFT,
	FARM_BONEMEAL,
	TREE_BONEMEAL,
	FARM_TILL,
}
