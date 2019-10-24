package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.structure.util.PotionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LootSettings {
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

		ret.setBoolean("hasLoot", hasLoot);
		if (lootTableName != null) {
			ret.setString("lootTableName", lootTableName.toString());
			ret.setInteger("lootRolls", lootRolls);
		}
		ret.setBoolean("splashPotion", splashPotion);
		if (!effects.isEmpty()) {
			NBTTagList effectList = new NBTTagList();
			for (PotionEffect potioneffect : effects) {
				effectList.appendTag(PotionHelper.writeCustomPotionEffectToNBT(potioneffect));
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
		lootSettings.hasLoot = nbt.getBoolean("hasLoot");
		lootSettings.lootTableName = new ResourceLocation(nbt.getString("lootTableName"));
		lootSettings.lootRolls = nbt.getInteger("lootRolls");
		lootSettings.splashPotion = nbt.getBoolean("splashPotion");
		NBTTagList effectList = nbt.getTagList("effects", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < effectList.tagCount(); i++) {
			PotionHelper.readCustomPotionEffectFromNBT(effectList.getCompoundTagAt(i)).ifPresent(lootSettings.effects::add);
		}
		lootSettings.spawnEntity = nbt.getBoolean("spawnEntity");
		lootSettings.entity = new ResourceLocation(nbt.getString("entity"));
		lootSettings.entityNBT = nbt.getCompoundTag("entityNBT");

		return lootSettings;
	}

	public void transferToContainer(ISpecialLootContainer container) {
		LootSettings applicableSettings = new LootSettings();

		if (hasLoot()) {
			applicableSettings.setHasLoot(true);
			applicableSettings.setLootTableName(lootTableName);
			applicableSettings.setLootRolls(lootRolls);
		}
		if (splashPotion) {
			applicableSettings.setSplashPotion(true);
			applicableSettings.setEffects(effects);
		}
		if (spawnEntity) {
			applicableSettings.setSpawnEntity(true);
			applicableSettings.setEntity(entity);
			applicableSettings.setEntityNBT(entityNBT);
		}

		container.setLootSettings(applicableSettings);
	}

	public LootSettings transferFromContainer(ISpecialLootContainer te) {
		LootSettings lootSettings = te.getLootSettings();

		if (lootSettings.hasLoot) {
			hasLoot = true;
			lootTableName = lootSettings.lootTableName;
			lootRolls = lootSettings.lootRolls;
		} else {
			hasLoot = false;
		}

		if (lootSettings.splashPotion) {
			splashPotion = true;
			effects = lootSettings.effects;
		} else {
			splashPotion = false;
		}

		if (lootSettings.spawnEntity) {
			spawnEntity = true;
			entity = lootSettings.entity;
			entityNBT = lootSettings.entityNBT;
		} else {
			spawnEntity = false;
		}

		return this;
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

	public void setSpawnEntity(boolean spawnEntity) {
		this.spawnEntity = spawnEntity;
	}

	public void setEffects(List<PotionEffect> effects) {
		this.effects = effects;
	}

	public void setEntity(ResourceLocation entity) {
		this.entity = entity;
	}

	public void setEntityNBT(NBTTagCompound entityNBT) {
		this.entityNBT = entityNBT;
	}

	public List<PotionEffect> getEffects() {
		return effects;
	}

	public boolean getSpawnEntity() {
		return spawnEntity;
	}

	public NBTTagCompound getEntityNBT() {
		return entityNBT;
	}

	public ResourceLocation getEntity() {
		return entity;
	}

	public boolean hasLootToSpawn() {
		return spawnEntity || splashPotion || hasLoot;
	}
}
