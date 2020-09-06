package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

public class ItemBardInstrument extends ItemBaseNPC {

	private final String[] instrumentNames = new String[] {"lute", "flute", "harp", "drum"};

	public ItemBardInstrument(String regName) {
		super(regName);
		setHasSubtypes(true);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab)) {
			return;
		}

		for (int i = 0; i < instrumentNames.length; i++) {
			items.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return super.getUnlocalizedName(par1ItemStack) + "." + instrumentNames[par1ItemStack.getItemDamage()];
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote) {
			int meta = stack.getItemDamage();
			SoundEvent s;
			s = SoundEvents.BLOCK_NOTE_BASEDRUM;
			if (meta == 0) {
				s = SoundEvents.BLOCK_NOTE_BASS;
			} else if (meta == 1) {
				s = SoundEvents.BLOCK_NOTE_FLUTE;
			} else if (meta == 2) {
				s = SoundEvents.BLOCK_NOTE_HARP;
			}
			world.playSound(null, player.posX + 0.5, player.posY + 0.5, player.posZ + 0.5, s, SoundCategory.PLAYERS, 2.0F, 1.0F);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase living, ItemStack stack) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this, "npc", false, meta -> "variant=" + instrumentNames[meta]);
	}
}
