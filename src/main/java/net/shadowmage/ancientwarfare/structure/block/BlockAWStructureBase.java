package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.material.Material;
import net.shadowmage.ancientwarfare.core.block.BlockAWBase;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructures;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;

public class BlockAWStructureBase extends BlockAWBase {
    public BlockAWStructureBase(Material material, String regName) {
        super(material, AncientWarfareStructures.modID, regName);
        setCreativeTab(AWStructuresItemLoader.structureTab);
    }
}
