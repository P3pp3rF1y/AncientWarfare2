package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

import java.util.Set;

public class ItemConstructionToolLakes extends Item {

    public ItemConstructionToolLakes(String itemName) {
        this.setUnlocalizedName(itemName);
        this.setCreativeTab(AWStructuresItemLoader.structureTab);
        this.setTextureName("ancientwarfare:structure/" + "construction_tool");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if(world.isRemote){
            return stack;
        }
        BlockPos pos = BlockTools.getBlockClickedOn(player, world, true);
        if (pos == null) {
            return stack;
        }
        Block block = player.world.getBlock(pos.x, pos.y, pos.z);
        if (!block.isAir(player.world, pos.x, pos.y, pos.z)) {
            return stack;
        }
        FloodFillPathfinder pf = new FloodFillPathfinder(player.world, pos.x, pos.y, pos.z, block, 0, false, true);
        Set<BlockPos> blocks = pf.doFloodFill();
        for (BlockPos p : blocks) {
            player.world.setBlock(p.x, p.y, p.z, Blocks.FLOWING_WATER);
        }
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        return stack;
    }

}
