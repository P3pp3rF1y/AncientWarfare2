package net.shadowmage.ancientwarfare.vehicle.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.vehicle.entity.AWVehicleEntityLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemVehicleSpawner extends Item {

    /*
     * TODO this can probably be removed in favor of modeled items
     */
    //private static HashMap<String, IIcon> regNameToIcon = new HashMap<>();

    public ItemVehicleSpawner(String regName) {
        this.setUnlocalizedName(regName);
        this.setHasSubtypes(true);
        setCreativeTab(AWVehicleItemLoader.vehicleTab);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        // TODO add info regarding vehicle type and any potentially stored stats therein
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab != AWVehicleItemLoader.vehicleTab) {
            return;
        }

        List<String> types = AWVehicleEntityLoader.getVehicleTypes();
        @Nonnull ItemStack stack;
        for (String t : types) {
            stack = new ItemStack(this);
            stack.setTagInfo("type", new NBTTagString(t));
            items.add(stack);
        }
    }

//    @Override
//    @SideOnly(Side.CLIENT)
//    public void registerIcons(IIconRegister reg) {
//        List<String> types = AWVehicleEntityLoader.getVehicleTypes();
//        for (String t : types) {
//            regNameToIcon.put(t, reg.registerIcon(AWVehicleEntityLoader.getIcon(t)));
//        }
//    }
//
//    @Override
//    public IIcon getIcon(ItemStack stack, int pass) {
//        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("type")) {
//            return regNameToIcon.get(stack.getTagCompound().getString("type"));
//        }
//        //TODO return a default placeholder Icon?
//        return super.getIcon(stack, pass);
//    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        // TODO lookup entity spawn type, spawn entity in world
        if(world.isRemote){
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        AWLog.logDebug("right click on spawner!!");
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("type")) {
            AWLog.logDebug("Invalid spawner item!!");
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        String type = stack.getTagCompound().getString("type");
        
        // TODO
        /*
        Entity e = AWEntityRegistry.createEntity(type, player.world);
        if (e != null) {
            e.setPosition(player.posX, player.posY, player.posZ);//TODO set position from player clicked-on target
            player.world.spawnEntity(e);
        }
        */
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
