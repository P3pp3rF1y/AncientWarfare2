package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public abstract class BlockBase extends Block {
    public BlockBase(Material material, String modID, String regName) {
        super(material);
        setUnlocalizedName(regName);
        setRegistryName(new ResourceLocation(modID, regName));
    }
}
