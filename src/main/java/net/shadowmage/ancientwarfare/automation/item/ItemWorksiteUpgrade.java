package net.shadowmage.ancientwarfare.automation.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.item.ItemBase;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.HashSet;

public class ItemWorksiteUpgrade extends ItemBase implements IItemClickable {

    public ItemWorksiteUpgrade() {
        this.setUnlocalizedName("worksite_upgrade");
        this.setCreativeTab(AWAutomationItemLoader.automationTab);
    }

    public static WorksiteUpgrade getUpgrade(ItemStack stack) {
        if (stack == null || stack.getItem() != AWItems.worksiteUpgrade || stack.getItem() == null) {
            throw new RuntimeException("Cannot retrieve worksite upgrade type for: " + stack + ".  Null stack, or item, or mismatched item!");
        }
        return WorksiteUpgrade.values()[stack.getItemDamage()];
    }

    public static ItemStack getStack(WorksiteUpgrade upgrade) {
        return upgrade == null ? null : new ItemStack(AWItems.worksiteUpgrade, 1, upgrade.ordinal());
    }

    @Override
    public boolean onRightClickClient(EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public boolean cancelRightClick(EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public void onRightClick(EntityPlayer player, ItemStack stack) {
        BlockPosition pos = BlockTools.getBlockClickedOn(player, player.worldObj, false);
        if (pos != null) {
            TileEntity te = player.worldObj.getTileEntity(pos.x, pos.y, pos.z);
            if (te instanceof IWorkSite) {
                IWorkSite ws = (IWorkSite) te;
                WorksiteUpgrade upgrade = getUpgrade(stack);
                if (!ws.getValidUpgrades().contains(upgrade)) {
                    return;
                }
                HashSet<WorksiteUpgrade> wsug = new HashSet<WorksiteUpgrade>(ws.getUpgrades());
                if (wsug.contains(upgrade)) {
                    return;
                }
                for (WorksiteUpgrade ug : wsug) {
                    if (ug.exclusive(upgrade)) {
                        return;//exclusive upgrade present, exit early
                    }
                }
                for (WorksiteUpgrade ug : wsug) {
                    if (upgrade.overrides(ug)) {
                        InventoryTools.dropItemInWorld(player.worldObj, getStack(ug), te.xCoord, te.yCoord, te.zCoord);
                        ws.removeUpgrade(ug);
                    }
                }
                ws.addUpgrade(upgrade);
                stack.stackSize--;
                if (stack.stackSize <= 0) {
                    player.destroyCurrentEquippedItem();
                }
                player.openContainer.detectAndSendChanges();
            }
        }
    }

    @Override
    public boolean onLeftClickClient(EntityPlayer player, ItemStack stack) {
        return false;
    }

    @Override
    public boolean cancelLeftClick(EntityPlayer player, ItemStack stack) {
        return false;
    }

    @Override
    public void onLeftClick(EntityPlayer player, ItemStack stack) {
    }

}
