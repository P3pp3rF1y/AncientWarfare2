package net.shadowmage.ancientwarfare.core.inventory;


public class InventoryBackpack extends InventoryBasic {

    public InventoryBackpack(int size) {
        super(size);
    }

    @Override
    public String toString() {
        return "Backpack size: " + getSizeInventory();
    }
}
