package net.shadowmage.ancientwarfare.core.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

import javax.annotation.Nullable;
import java.util.List;

public class ItemQuill extends Item {

    double attackDamage = 5.d;
    ToolMaterial material;

    public ItemQuill(String regName, ToolMaterial material) {
        this.material = material;
        this.setUnlocalizedName(regName);
        this.setRegistryName(new ResourceLocation(AncientWarfareCore.modID, regName));
        //this.setTextureName("ancientwarfare:core/" + regName);
        this.attackDamage = 1.f + material.getDamageVsEntity();
        this.maxStackSize = 1;
        this.setMaxDamage(material.getMaxUses());
        this.setCreativeTab(AWCoreBlockLoader.coreTab);
        this.setHarvestLevel("quill", material.getHarvestLevel());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("guistrings.core.quill.work_mode"));
    }

    public ToolMaterial getMaterial() {
        return material;
    }

    /*
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    @Override
    public int getItemEnchantability() {
        return this.material.getEnchantability();
    }

    /*
     * Return whether this item is repairable in an anvil.
     */
    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return this.material.getRepairItemStack() == par2ItemStack || super.getIsRepairable(par1ItemStack, par2ItemStack);
    }

    /*
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    @Override
    public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase) {
        par1ItemStack.damageItem(1, par3EntityLivingBase);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if (state.getBlockHardness(world, pos) != 0) {
            stack.damageItem(2, entityLiving);
        }
        return true;
    }

    /*
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.attackDamage, 0));
        return multimap;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if(world.isRemote){
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        BlockPos pos = BlockTools.getBlockClickedOn(player, world, false);
        if (pos != null) {
            TileEntity te = player.world.getTileEntity(pos);
            if (te instanceof IWorkSite && ((IWorkSite) te).getWorkType() == IWorkSite.WorkType.RESEARCH) {
                IWorkSite teResearchStation = (IWorkSite) te;
                if (teResearchStation.hasWork()) {
                    teResearchStation.addEnergyFromPlayer(player);
                    stack.damageItem(1, player);
                    //TODO add chat message
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

}
