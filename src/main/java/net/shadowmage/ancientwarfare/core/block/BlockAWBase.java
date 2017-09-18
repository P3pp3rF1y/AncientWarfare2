package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public abstract class BlockAWBase extends Block {
    public BlockAWBase(Material material, String modID, String regName) {
        super(material);
        setUnlocalizedName(regName);
        setRegistryName(new ResourceLocation(modID, regName));
    }
}
