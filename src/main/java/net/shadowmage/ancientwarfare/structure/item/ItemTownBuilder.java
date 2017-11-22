package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.structure.town.WorldTownGenerator;

public class ItemTownBuilder extends ItemBaseStructure implements IItemKeyInterface {

    public ItemTownBuilder(String name) {
        super(name);
        setMaxStackSize(1);
        //TODO make texture (uses structure builder one currently)
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return false;
    }

    @Override
    public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key) {
        return key == ItemKey.KEY_0;
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        if (player == null || player.world.isRemote) {
            return;
        }
        long t1 = System.nanoTime();
        WorldTownGenerator.INSTANCE.attemptGeneration(player.world, MathHelper.floor(player.posX), MathHelper.floor(player.posZ));
        long t2 = System.nanoTime();
        AWLog.logDebug("Total Town gen nanos (incl. validation): " + (t2 - t1));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
    }//TODO open town-type selection GUI

}
