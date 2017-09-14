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
package net.shadowmage.ancientwarfare.structure.api;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException.TemplateRuleParsingException;

import java.util.List;

public abstract class TemplateRuleBlock extends TemplateRule {

    /*
     * Called by reflection
     * @param world
     * @param x
     * @param y
     * @param z
     * @param block
     * @param meta
     * @param turns
     */
    public TemplateRuleBlock(World world, BlockPos pos, Block block, int meta, int turns) {

    }
    /*
     * Called by reflection
     */
    public TemplateRuleBlock() {

    }

    /*
     * should this rule be re-used in the template for the passed in block/meta parameters?
     * common things to check are simple block ID / meta combinations.
     * keep in mind you must rotate the passed in meta if you wish to compare it with the meta stored in your rule (you did normalize to north-oriented on construction, right?)
     * more complex blocks may check the tile-entity for specific data
     *
     * @param meta  -- pure meta as from world.getblockMetaData
     * @param turns -- 90' clockwise turns needed for proper orientation from normalized template orientation
     * @return true if this rule can handle the input block
     */
    public abstract boolean shouldReuseRule(World world, Block block, int meta, int turns, BlockPos pos);

    @Override
    public final void parseRule(int ruleNumber, List<String> lines) throws TemplateRuleParsingException {
        super.parseRule(ruleNumber, lines);
    }

}
