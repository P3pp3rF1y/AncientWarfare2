package net.shadowmage.ancientwarfare.npc.item;

import com.google.common.collect.Multimap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class ItemShield extends ItemAWNPCBase {
    public static final UUID shieldID = UUID.fromString("CB3F55D3-564C-4F38-A497-9C13A33DB5CF");
    private final int armorValue;

    public ItemShield(String name, ToolMaterial material) {
        super(name);
        this.setFull3D();
        //this.setTextureName("ancientwarfare:npc/" + name);
        this.armorValue = material.getHarvestLevel() * 2 + 1;
        setMaxDamage(material.getMaxUses());
    }

    public int getArmorBonusValue() {
        return armorValue;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap map = super.getAttributeModifiers(slot, stack);
        map.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(shieldID, "Shield modifier", 0.5, 2));
        return map;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase attacked, EntityLivingBase attacker) {
        stack.damageItem(2, attacker);
        return true;
    }
}
