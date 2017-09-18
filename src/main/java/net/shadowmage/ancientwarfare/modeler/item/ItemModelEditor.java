package net.shadowmage.ancientwarfare.modeler.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.item.ItemAWBase;
import net.shadowmage.ancientwarfare.modeler.AncientWarfareModeler;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModelEditor extends ItemAWBase {

    public static final CreativeTabs editorTab = new CreativeTabs("tabs.editor") {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(Items.STICK);
        }
    };

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("guistrings.modeler.right_click_to_open"));
    }

    public ItemModelEditor(String regName) {
        super(AncientWarfareModeler.modID, regName);
        setCreativeTab(editorTab);
        //.setTextureName("ancientwarfare:modeler/editor_opener");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote) {
            AncientWarfareModeler.proxy.openGui(player);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}
