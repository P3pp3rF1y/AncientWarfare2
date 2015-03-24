package net.shadowmage.ancientwarfare.core.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.tile.TileResearchStation;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

import java.util.List;

public class ItemQuill extends Item implements IItemClickable {

    double attackDamage = 5.d;
    ToolMaterial material;

    public ItemQuill(String regName, ToolMaterial material) {
        this.material = material;
        this.setUnlocalizedName(regName);
        this.setTextureName("ancientwarfare:core/" + regName);
        this.attackDamage = 1.f + material.getDamageVsEntity();
        this.maxStackSize = 1;
        this.setMaxDamage(material.getMaxUses());
        this.setCreativeTab(AWCoreBlockLoader.coreTab);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        list.add(StatCollector.translateToLocal("guistrings.core.quill.work_mode_1"));
        list.add(StatCollector.translateToLocal("guistrings.core.quill.work_mode_2"));
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

    @Override
    public boolean onRightClickClient(EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public void onRightClick(EntityPlayer player, ItemStack stack) {
        BlockPosition pos = BlockTools.getBlockClickedOn(player, player.worldObj, false);
        if (pos != null) {
            TileEntity te = player.worldObj.getTileEntity(pos.x, pos.y, pos.z);
            if (te instanceof IWorkSite && ((IWorkSite) te).getWorkType() == IWorkSite.WorkType.RESEARCH) {
                IWorkSite ters = (IWorkSite) te;
                if (ters.hasWork()) {
                    Team team = ters.getTeam();
                    if ((team!=null && team.isSameTeam(player.getTeam())) || (te instanceof IOwnable && player.getCommandSenderName().equals(((IOwnable)te).getOwnerName()))) {
                        ters.addEnergyFromPlayer(player);
                        stack.damageItem(1, player);
                        //TODO add chat message
                    }
                }
            }
        }
    }

    @Override
    public boolean onLeftClickClient(EntityPlayer player, ItemStack stack) {
        return false;
    }

    @Override
    public void onLeftClick(EntityPlayer player, ItemStack stack) {
    }

}
