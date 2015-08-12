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
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;

import java.util.List;

public class ItemHammer extends Item implements IItemKeyInterface {

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
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase living) {
        if (block.getBlockHardness(world, x, y, z) != 0) {
            stack.damageItem(2, living);
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
            list.add(StatCollector.translateToLocal("guistrings.core.hammer.work_mode"));
        } else {
            list.add(StatCollector.translateToLocal("guistrings.core.hammer.rotate_mode"));
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
        player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "random.click", 0.3F, 0.6F);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if(world.isRemote){
            return stack;
        }
        MovingObjectPosition hit = getMovingObjectPositionFromPlayer(world, player, false);
        if (hit == null) {
            return stack;
        }
        boolean mode = false;
        if (stack.hasTagCompound()) {
            mode = stack.getTagCompound().getBoolean("workMode");
        } else {
            stack.setTagCompound(new NBTTagCompound());
        }
        if (mode) {
            TileEntity te = world.getTileEntity(hit.blockX, hit.blockY, hit.blockZ);
            if (te instanceof IWorkSite && ((IWorkSite) te).hasWork()) {
                ((IWorkSite) te).addEnergyFromPlayer(player);
                playSound(world, hit, "tile.piston.in");
            } else {
                Block block = world.getBlock(hit.blockX, hit.blockY, hit.blockZ);
                if(!block.isAir(world, hit.blockX, hit.blockY, hit.blockZ))
                    playBlockSound(world, hit, block);
            }
        } else {
            Block block = world.getBlock(hit.blockX, hit.blockY, hit.blockZ);
            if(!block.isAir(world, hit.blockX, hit.blockY, hit.blockZ)) {
                if (block.rotateBlock(world, hit.blockX, hit.blockY, hit.blockZ, ForgeDirection.getOrientation(hit.sideHit)))
                    playSound(world, hit, "tile.piston.out");
                else
                    playBlockSound(world, hit, block);
            }
        }
        return stack;
    }

    private void playSound(World world, MovingObjectPosition hit, String sound){
        world.playSoundEffect(hit.blockX, hit.blockY, hit.blockZ, sound, 0.2F, world.rand.nextFloat() * 0.15F + 0.6F);
    }

    private void playBlockSound(World world, MovingObjectPosition hit, Block block){
        if(block.stepSound != null){
            world.playSoundEffect(hit.blockX, hit.blockY, hit.blockZ, block.stepSound.func_150496_b(), block.stepSound.getVolume() * 0.5F, block.stepSound.getPitch() * 0.8F);
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
}
