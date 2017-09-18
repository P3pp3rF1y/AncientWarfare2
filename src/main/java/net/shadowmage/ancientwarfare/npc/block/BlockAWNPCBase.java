package net.shadowmage.ancientwarfare.npc.block;

import net.minecraft.block.material.Material;
import net.shadowmage.ancientwarfare.core.block.BlockAWBase;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.item.AWNPCItemLoader;

public class BlockAWNPCBase extends BlockAWBase {
    public BlockAWNPCBase(Material material, String regName) {
        super(material, AncientWarfareNPC.modID, regName);
        this.setCreativeTab(AWNPCItemLoader.npcTab);
    }
}
