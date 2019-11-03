package net.shadowmage.ancientwarfare.structure.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.util.Constants;

import java.util.List;
import java.util.Optional;

public class PotionHelper {
	private static final String SHOW_PARTICLES_TAG = "ShowParticles";
	private static final String CURATIVE_ITEMS_TAG = "CurativeItems";

	private PotionHelper() {}

	public static NBTTagCompound writeCustomPotionEffectToNBT(PotionEffect potionEffect) {
		NBTTagCompound nbt = new NBTTagCompound();
		//noinspection ConstantConditions
		nbt.setString("RegistryName", potionEffect.getPotion().getRegistryName().toString());
		nbt.setByte("Amplifier", (byte) potionEffect.getAmplifier());
		nbt.setInteger("Duration", potionEffect.getDuration());
		nbt.setBoolean("Ambient", potionEffect.getIsAmbient());
		nbt.setBoolean(SHOW_PARTICLES_TAG, potionEffect.doesShowParticles());
		writeCurativeItems(potionEffect, nbt);
		return nbt;
	}

	private static void writeCurativeItems(PotionEffect potionEffect, NBTTagCompound nbt) {
		NBTTagList list = new NBTTagList();
		for (ItemStack stack : potionEffect.getCurativeItems()) {
			list.appendTag(stack.writeToNBT(new NBTTagCompound()));
		}
		nbt.setTag(CURATIVE_ITEMS_TAG, list);
	}

	public static Optional<PotionEffect> readCustomPotionEffectFromNBT(NBTTagCompound nbt) {
		String registryName = nbt.getString("RegistryName");
		Potion potion = Potion.getPotionFromResourceLocation(registryName);

		if (potion == null) {
			return Optional.empty();
		} else {
			int j = nbt.getByte("Amplifier");
			int k = nbt.getInteger("Duration");
			boolean flag = nbt.getBoolean("Ambient");
			boolean flag1 = true;

			if (nbt.hasKey(SHOW_PARTICLES_TAG, 1)) {
				flag1 = nbt.getBoolean(SHOW_PARTICLES_TAG);
			}

			return Optional.of(readCurativeItems(new PotionEffect(potion, k, j < 0 ? 0 : j, flag, flag1), nbt));
		}
	}

	private static PotionEffect readCurativeItems(PotionEffect effect, NBTTagCompound nbt) {
		if (nbt.hasKey(CURATIVE_ITEMS_TAG, Constants.NBT.TAG_LIST)) {
			List<ItemStack> items = new java.util.ArrayList<>();
			NBTTagList list = nbt.getTagList(CURATIVE_ITEMS_TAG, Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < list.tagCount(); i++) {
				items.add(new ItemStack(list.getCompoundTagAt(i)));
			}
			effect.setCurativeItems(items);
		}

		return effect;
	}
}
