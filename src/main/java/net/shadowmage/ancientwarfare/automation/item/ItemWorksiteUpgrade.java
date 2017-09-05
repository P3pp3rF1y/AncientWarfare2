package net.shadowmage.ancientwarfare.automation.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.item.ItemBase;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;
import java.util.HashSet;

public class ItemWorksiteUpgrade extends ItemBase {

    public ItemWorksiteUpgrade() {
        this.setUnlocalizedName("worksite_upgrade");
        this.setCreativeTab(AWAutomationItemLoader.automationTab);
    }

    public static WorksiteUpgrade getUpgrade(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() != AWItems.worksiteUpgrade) {
            throw new RuntimeException("Cannot retrieve worksite upgrade type for: " + stack + ".  Null stack, or item, or mismatched item!");
        }
        return WorksiteUpgrade.values()[stack.getItemDamage()];
    }

    public static ItemStack getStack(WorksiteUpgrade upgrade) {
        return upgrade == null ? ItemStack.EMPTY : new ItemStack(AWItems.worksiteUpgrade, 1, upgrade.ordinal());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        @Nonnull ItemStack stack = player.getHeldItem(hand);

        if(world.isRemote){
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        BlockPos pos = BlockTools.getBlockClickedOn(player, world, false);
        if (pos != null) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof IWorkSite) {
                IWorkSite ws = (IWorkSite) te;
                WorksiteUpgrade upgrade = getUpgrade(stack);
                if (!ws.getValidUpgrades().contains(upgrade)) {
                    return new ActionResult<>(EnumActionResult.FAIL, stack);
                }
                HashSet<WorksiteUpgrade> wsug = new HashSet<>(ws.getUpgrades());
                if (wsug.contains(upgrade)) {
                    return new ActionResult<>(EnumActionResult.FAIL, stack);
                }
                for (WorksiteUpgrade ug : wsug) {
                    if (ug.exclusive(upgrade)) {
                        return new ActionResult<>(EnumActionResult.FAIL, stack);//exclusive upgrade present, exit early
                    }
                }
                for (WorksiteUpgrade ug : wsug) {
                    if (upgrade.overrides(ug)) {
                        InventoryTools.dropItemInWorld(player.world, getStack(ug), te.getPos());
                        ws.removeUpgrade(ug);
                    }
                }
                ws.addUpgrade(upgrade);
                stack.shrink(1);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
