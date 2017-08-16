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
package net.shadowmage.ancientwarfare.structure.block;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.tile.TEGateProxy;

import java.util.ArrayList;
import java.util.Random;

public final class BlockGateProxy extends BlockContainer {

    public BlockGateProxy() {
        super(Material.ROCK);
        this.setBlockTextureName("ancientwarfare:structure/gate_proxy");
        this.setCreativeTab(null);
        this.setResistance(2000.f);
        this.setHardness(5.f);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TEGateProxy();
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune){

    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return new ArrayList<ItemStack>();
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos){
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        TileEntity proxy = world.getTileEntity(pos);
        if(proxy instanceof TEGateProxy){
            return ((TEGateProxy) proxy).onBlockPicked(target);
        }
        return null;
    }

    @Override
    public int quantityDropped(Random par1Random) {
        return 0;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int face, float vecX, float vecY, float vecZ) {
        TileEntity proxy = world.getTileEntity(pos);
        return proxy instanceof TEGateProxy && ((TEGateProxy) proxy).onBlockClicked(player);
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        TileEntity proxy = world.getTileEntity(pos);
        if(proxy instanceof TEGateProxy){
            ((TEGateProxy) proxy).onBlockAttacked(player);
        }else if(player != null && player.capabilities.isCreativeMode){
            return super.removedByPlayer(world, player, x, y, z, false);
        }
        return false;
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        player.addExhaustion(0.025F);
    }

    @Override
    public void dropXpOnBlockBreak(World world, int x, int y, int z, int amount) {

    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        return false;
    }

    //Actually "can go through", for mob pathing
    @Override
    public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z){
        TileEntity proxy = world.getTileEntity(pos);
        if(proxy instanceof TEGateProxy && ((TEGateProxy)proxy).isGateClosed()){
            return false;
        }
        //Gate is probably open, Search identical neighbour
        if(world.getBlock(x - 1, y, z) == this) {
            return world.getBlock(x + 1, y, z) == this;
        }else if(world.getBlock(x, y, z - 1) == this) {
            return world.getBlock(x, y, z + 1) == this;
        }else if(world.getBlock(x + 1 , y, z) == this) {
            return world.getBlock(x - 1, y, z) == this;
        }else if(world.getBlock(x, y, z + 1) == this){
            return world.getBlock(x, y, z - 1) == this;
        }
        return true;
    }
}
