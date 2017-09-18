package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.material.Material;
import net.shadowmage.ancientwarfare.automation.AncientWarfareAutomation;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.core.block.BlockAWBase;

public abstract class BlockAWAutomationBase extends BlockAWBase {

    public BlockAWAutomationBase(Material material, String regName) {
        super(material, AncientWarfareAutomation.modID, regName);
        this.setCreativeTab(AWAutomationItemLoader.automationTab);
    }
}
