package net.shadowmage.ancientwarfare.npc.item;

import com.google.common.collect.Multimap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class ItemShield extends Item {
    public static final UUID shieldID = UUID.fromString("CB3F55D3-564C-4F38-A497-9C13A33DB5CF");
    private final int armorValue;

    public ItemShield(String name, ToolMaterial material) {
        setUnlocalizedName(name);
        setCreativeTab(AWNpcItemLoader.npcTab);
        this.setFull3D();
        this.setTextureName("ancientwarfare:npc/" + name);
        this.armorValue = material.getHarvestLevel() * 2 + 1;
        setMaxDamage(material.getMaxUses());
    }

    public int getArmorBonusValue() {
        return armorValue;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Multimap getAttributeModifiers(ItemStack stack) {
        Multimap map = super.getAttributeModifiers(stack);
        map.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(shieldID, "Shield modifier", 0.5, 2));
        return map;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase attacked, EntityLivingBase attacker) {
        stack.damageItem(2, attacker);
        return true;
    }
}
