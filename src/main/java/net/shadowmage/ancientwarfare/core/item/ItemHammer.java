package net.shadowmage.ancientwarfare.core.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;

import javax.annotation.Nullable;
import java.util.List;

public class ItemHammer extends ItemBaseCore implements IItemKeyInterface {

    double attackDamage = 5.d;

    private ToolMaterial material;

    public ItemHammer(String regName, ToolMaterial material) {
        super(regName);
        //this.setTextureName("ancientwarfare:core/" + regName);
        attackDamage = 4.f + material.getAttackDamage();
        material = material;
        maxStackSize = 1;
        setMaxDamage(material.getMaxUses());
        setHarvestLevel("hammer", material.getHarvestLevel());
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
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String key = InputHandler.instance.getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_0);
        tooltip.add(I18n.format("guistrings.core.hammer.use_primary_item_key", key));
        //noinspection ConstantConditions
        if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("workMode")) {
            tooltip.add(I18n.format("guistrings.core.hammer.work_mode"));
        } else {
            tooltip.add(I18n.format("guistrings.core.hammer.rotate_mode"));
        }
    }

    @Override
    public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key) {
        return key == ItemKey.KEY_0;
    }

    @Override
    public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key) {
        if (player.world.isRemote) {
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
        player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 0.3F, 0.6F);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(world.isRemote){
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        RayTraceResult hit = rayTrace(world, player, false);
        if (hit == null) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        boolean mode = false;
        if (stack.hasTagCompound()) {
            mode = stack.getTagCompound().getBoolean("workMode");
        } else {
            stack.setTagCompound(new NBTTagCompound());
        }
        if (mode) {
            TileEntity te = world.getTileEntity(hit.getBlockPos());
            if (te instanceof IWorkSite && ((IWorkSite) te).hasWork()) {
                ((IWorkSite) te).addEnergyFromPlayer(player);
                playSound(world, hit, SoundEvents.BLOCK_PISTON_CONTRACT);
            } else {
                if(!world.isAirBlock(hit.getBlockPos()))
                    playBlockSound(world, hit, world.getBlockState(hit.getBlockPos()));
            }
        } else {
            if(!world.isAirBlock(hit.getBlockPos())) {
                IBlockState state = world.getBlockState(hit.getBlockPos());
                if (state.getBlock().rotateBlock(world, hit.getBlockPos(), hit.sideHit))
                    playSound(world, hit, SoundEvents.BLOCK_PISTON_EXTEND);
                else
                    playBlockSound(world, hit, state);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private void playSound(World world, RayTraceResult hit, SoundEvent sound){
        world.playSound(null, hit.getBlockPos(), sound, SoundCategory.BLOCKS, 0.2F, world.rand.nextFloat() * 0.15F + 0.6F);
    }

    private void playBlockSound(World world, RayTraceResult hit, IBlockState state){
        SoundType sound = state.getBlock().getSoundType(state, world, hit.getBlockPos(), null);
        if(sound != null){
            world.playSound(null, hit.getBlockPos(), sound.getPlaceSound(), SoundCategory.BLOCKS, sound.getVolume() * 0.5F, sound.getPitch() * 0.8F);
        }
    }

    /*
     * Returns True is the item is renderer in full 3D when hold.
     */
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return true;
    }
}
