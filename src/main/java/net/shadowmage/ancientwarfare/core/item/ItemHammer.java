package net.shadowmage.ancientwarfare.core.item;

import com.google.common.collect.Multimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;

import java.util.List;

public class ItemHammer extends Item implements IItemKeyInterface, IItemClickable {

    double attackDamage = 5.d;

    private ToolMaterial material;

    public ItemHammer(String regName, ToolMaterial material) {
        this.setUnlocalizedName(regName);
        this.setCreativeTab(AWCoreBlockLoader.coreTab);
        this.setTextureName("ancientwarfare:core/" + regName);
        this.attackDamage = 4.f + material.getDamageVsEntity();
        this.material = material;
        this.maxStackSize = 1;
        this.setMaxDamage(material.getMaxUses());
        this.setHarvestLevel("hammer", material.getHarvestLevel());
    }

    @Override
    public boolean cancelRightClick(EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public boolean cancelLeftClick(EntityPlayer player, ItemStack stack) {
        return false;
    }

    public ToolMaterial getMaterial() {
        return material;
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    @Override
    public int getItemEnchantability() {
        return this.material.getEnchantability();
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return this.material.func_150995_f() == par2ItemStack.getItem() || super.getIsRepairable(par1ItemStack, par2ItemStack);
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    @Override
    public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase) {
        par1ItemStack.damageItem(1, par3EntityLivingBase);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack p_150894_1_, World p_150894_2_, Block p_150894_3_, int p_150894_4_, int p_150894_5_, int p_150894_6_, EntityLivingBase p_150894_7_) {
        if ((double) p_150894_3_.getBlockHardness(p_150894_2_, p_150894_4_, p_150894_5_, p_150894_6_) != 0.0D) {
            p_150894_1_.damageItem(2, p_150894_7_);
        }
        return true;
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    @SuppressWarnings({"unchecked", "deprecation", "rawtypes"})
    @Override
    public Multimap getItemAttributeModifiers() {
        Multimap multimap = super.getItemAttributeModifiers();
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", this.attackDamage, 0));
        return multimap;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        String key = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_0);
        list.add(StatCollector.translateToLocalFormatted("guistrings.core.hammer.use_primary_item_key", key));
        if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("workMode")) {
            list.add(StatCollector.translateToLocal("guistrings.core.hammer.work_mode_1"));
            list.add(StatCollector.translateToLocal("guistrings.core.hammer.work_mode_2"));
        } else {
            list.add(StatCollector.translateToLocal("guistrings.core.hammer.rotate_mode_1"));
            list.add(StatCollector.translateToLocal("guistrings.core.hammer.rotate_mode_2"));
        }
    }

    @Override
    public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key) {
        return key == ItemKey.KEY_0;
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        if (player.worldObj.isRemote) {
            return;
        }
        boolean mode = false;
        if (stack.hasTagCompound()) {
            mode = stack.getTagCompound().getBoolean("workMode");
        } else {
            stack.setTagCompound(new NBTTagCompound());
        }
        mode = !mode;
        stack.getTagCompound().setBoolean("workMode", mode);
        player.addChatMessage(new ChatComponentTranslation("guistrings.automation.work_mode_change"));
    }

    @Override
    public void onRightClick(EntityPlayer player, ItemStack stack) {
        MovingObjectPosition hit = getMovingObjectPositionFromPlayer(player.worldObj, player, false);
        if (hit == null) {
            return;
        }
        boolean mode = false;
        if (stack.hasTagCompound()) {
            mode = stack.getTagCompound().getBoolean("workMode");
        } else {
            stack.setTagCompound(new NBTTagCompound());
        }
        if (mode) {
            TileEntity te = player.worldObj.getTileEntity(hit.blockX, hit.blockY, hit.blockZ);
            if (te instanceof IWorkSite) {
                if (((IWorkSite) te).hasWork()) {
                    ((IWorkSite) te).addEnergyFromPlayer(player);
                }
                player.addChatMessage(new ChatComponentTranslation("guistrings.automation.doing_player_work"));
            } else {
                player.addChatMessage(new ChatComponentTranslation("guistrings.automation.wrong_hammer_mode"));
            }
        } else {
            Block block = player.worldObj.getBlock(hit.blockX, hit.blockY, hit.blockZ);
            if (block == null) {
                return;
            }
            player.addChatMessage(new ChatComponentTranslation("guistrings.automation.rotating_block"));
            block.rotateBlock(player.worldObj, hit.blockX, hit.blockY, hit.blockZ, ForgeDirection.getOrientation(hit.sideHit));
        }
    }

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return true;
    }

    @Override
    public boolean onLeftClickClient(EntityPlayer player, ItemStack stack) {
        return false;
    }

    @Override
    public boolean onRightClickClient(EntityPlayer player, ItemStack stack) {
        MovingObjectPosition hit = getMovingObjectPositionFromPlayer(player.worldObj, player, false);
        return hit != null;
            return false;
        }
    }

    @Override
    public void onLeftClick(EntityPlayer player, ItemStack stack) {

    }

}
