package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public abstract class ItemSlotFilter implements Predicate<ItemStack> {
    public static final Predicate<ItemStack> FALSE = new ItemSlotFilter() {
        @Override
        public boolean test(@Nullable ItemStack input) {
            return false;
        }
    };

    @Override
    public boolean equals(Object object){
        return this == object;
    }
}
