package net.shadowmage.ancientwarfare.vehicle.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.vehicle.entity.AWVehicleEntityLoader;

import java.util.HashMap;
import java.util.List;

public class ItemVehicleSpawner extends Item {

    /*
     * TODO this can probably be removed in favor of modeled items
     */
    private static HashMap<String, IIcon> regNameToIcon = new HashMap<>();

    public ItemVehicleSpawner(String regName) {
        this.setUnlocalizedName(regName);
        this.setHasSubtypes(true);
        setCreativeTab(AWVehicleItemLoader.vehicleTab);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltipList, boolean displayDetailedInformation) {
        // TODO add info regarding vehicle type and any potentially stored stats therein
        super.addInformation(stack, player, tooltipList, displayDetailedInformation);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        List<String> types = AWVehicleEntityLoader.getVehicleTypes();
        @Nonnull ItemStack stack;
        for (String t : types) {
            stack = new ItemStack(item, 1);
            stack.setTagInfo("type", new NBTTagString(t));
            list.add(stack);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        List<String> types = AWVehicleEntityLoader.getVehicleTypes();
        for (String t : types) {
            regNameToIcon.put(t, reg.registerIcon(AWVehicleEntityLoader.getIcon(t)));
        }
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("type")) {
            return regNameToIcon.get(stack.getTagCompound().getString("type"));
        }
        //TODO return a default placeholder Icon?
        return super.getIcon(stack, pass);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        // TODO lookup entity spawn type, spawn entity in world
        if(world.isRemote){
            return stack;
        }
        AWLog.logDebug("right click on spawner!!");
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("type")) {
            AWLog.logDebug("Invalid spawner item!!");
            return stack;
        }
        String type = stack.getTagCompound().getString("type");
        
        // TODO
        /*
        Entity e = AWEntityRegistry.createEntity(type, player.worldObj);
        if (e != null) {
            e.setPosition(player.posX, player.posY, player.posZ);//TODO set position from player clicked-on target
            player.worldObj.spawnEntity(e);
        }
        */
        return stack;
    }
}
