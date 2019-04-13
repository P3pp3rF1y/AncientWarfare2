package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LootSettings {
	private static final String BLOCK_STACK_TAG = "blockStack";
	private boolean placeBlock = false;
	private ItemStack blockStack = ItemStack.EMPTY;
	private boolean hasLoot = false;
	private ResourceLocation lootTableName = null;
	private int lootRolls = 1;
	private boolean splashPotion = false;
	private List<PotionEffect> effects = new ArrayList<>();
	private boolean spawnEntity = false;
	private ResourceLocation entity = null;
	private NBTTagCompound entityNBT = new NBTTagCompound();

	public NBTTagCompound serializeNBT() {
		NBTTagCompound ret = new NBTTagCompound();

		ret.setBoolean("placeBlock", placeBlock);
		if (blockStack != ItemStack.EMPTY) {
			ret.setTag(BLOCK_STACK_TAG, blockStack.writeToNBT(new NBTTagCompound()));
		}
		ret.setBoolean("hasLoot", hasLoot);
		if (lootTableName != null) {
			ret.setString("lootTableName", lootTableName.toString());
			ret.setInteger("lootRolls", lootRolls);
		}
		ret.setBoolean("splashPotion", splashPotion);
		if (!effects.isEmpty()) {
			NBTTagList effectList = new NBTTagList();
			for (PotionEffect potioneffect : effects) {
				effectList.appendTag(potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
			}
			ret.setTag("effects", effectList);
		}
		ret.setBoolean("spawnEntity", spawnEntity);
		if (entity != null) {
			ret.setString("entity", entity.toString());
			if (!entityNBT.hasNoTags()) {
				ret.setTag("entityNBT", entityNBT);
			}
		}

		return ret;
	}

	public static LootSettings deserializeNBT(NBTTagCompound nbt) {
		LootSettings lootSettings = new LootSettings();
		lootSettings.placeBlock = nbt.getBoolean("placeBlock");
		lootSettings.blockStack = nbt.hasKey(BLOCK_STACK_TAG) ? new ItemStack(nbt.getCompoundTag(BLOCK_STACK_TAG)) : ItemStack.EMPTY;
		lootSettings.hasLoot = nbt.getBoolean("hasLoot");
		lootSettings.lootTableName = new ResourceLocation(nbt.getString("lootTableName"));
		lootSettings.lootRolls = nbt.getInteger("lootRolls");
		lootSettings.splashPotion = nbt.getBoolean("splashPotion");
		NBTTagList effectList = nbt.getTagList("effects", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < effectList.tagCount(); i++) {
			lootSettings.effects.add(PotionEffect.readCustomPotionEffectFromNBT(effectList.getCompoundTagAt(i)));
		}
		lootSettings.spawnEntity = nbt.getBoolean("spawnEntity");
		lootSettings.entity = new ResourceLocation(nbt.getString("entity"));
		lootSettings.entityNBT = nbt.getCompoundTag("entityNBT");

		return lootSettings;
	}

	public boolean hasLoot() {
		return hasLoot;
	}

	public Optional<ResourceLocation> getLootTableName() {
		return Optional.ofNullable(lootTableName);
	}

	public void removeLoot() {
		hasLoot = false;
		lootTableName = null;
		lootRolls = 1;
	}

	public int getLootRolls() {
		return lootRolls;
	}

	public void setLootTableName(ResourceLocation lootTableName) {
		this.lootTableName = lootTableName;
	}

	public void setLootRolls(int lootRolls) {
		this.lootRolls = lootRolls;
	}

	public void setPlaceBlock(boolean placeBlock) {
		this.placeBlock = placeBlock;
	}

	public boolean getPlaceBlock() {
		return placeBlock;
	}

	public void setHasLoot(boolean hasLoot) {
		this.hasLoot = hasLoot;
	}

	public boolean getHasLoot() {
		return hasLoot;
	}

	public void setSplashPotion(boolean splashPotion) {
		this.splashPotion = splashPotion;
	}

	public boolean getSplashPotion() {
		return splashPotion;
	}

	public void setBlockStack(ItemStack blockStack) {
		this.blockStack = blockStack;
	}

	public ItemStack getBlockStack() {
		return blockStack;
	}
}
