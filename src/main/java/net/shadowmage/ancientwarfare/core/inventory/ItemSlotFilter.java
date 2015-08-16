package net.shadowmage.ancientwarfare.core.inventory;

import com.google.common.base.Predicate;
import net.minecraft.item.ItemStack;

public abstract class ItemSlotFilter implements Predicate<ItemStack> {
    public static final Predicate<ItemStack> FALSE = new ItemSlotFilter() {
        @Override
        public boolean apply(ItemStack input) {
            return false;
        }
    };

    @Override
    public boolean equals(Object object){
        return this == object;
    }
}
