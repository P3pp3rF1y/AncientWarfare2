package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import java.lang.reflect.Array;
import java.util.HashSet;

public class BlockAndMeta { //TODO rename or move the only method
    public static IBlockState[] buildList(String listName, String[] blockListRaw) {
        HashSet<IBlockState> stateSet = new HashSet<>();

        AncientWarfareCore.log.info("Building " + listName + "...");
        
        for (String blockName : blockListRaw) {
            blockName = blockName.trim();
            if (!blockName.equals("")) {
                String[] blockId = blockName.split(":");
                if (Array.getLength(blockId) != 2 && Array.getLength(blockId) != 3 ) {
                    AncientWarfareCore.log.warn(" - Invalid block (bad length of " + Array.getLength(blockId) + "): " + blockName);
                    continue;
                }
                if (blockId[0] == null || blockId[1] == null) {
                    AncientWarfareCore.log.warn(" - Invalid block (parse/format error): " + blockName);
                    continue;
                }
                ResourceLocation registryName = new ResourceLocation(blockId[0] + ":" + blockId[1]);
                if (!Block.REGISTRY.containsKey(registryName)) {
                    AncientWarfareCore.log.warn(" - Skipping missing block: " + blockName);
                    continue;
                }
                Block block = Block.REGISTRY.getObject(registryName);
                int meta = -1;
                if (Array.getLength(blockId) == 3) {
                    try {
                        meta = Integer.parseInt(blockId[2]);
                        if (meta < 0 || meta > 15)
                            throw new NumberFormatException();
                    } catch (NumberFormatException e) {
                        AncientWarfareCore.log.warn(" - Meta value invalid : '" + blockId[2] + "', must be a number between 0 and 15");
                        continue;
                    }
                }
                stateSet.add(meta != -1 ? block.getStateFromMeta(meta) : block.getDefaultState());
            }
        }

        AncientWarfareCore.log.info("...added " + stateSet.size() + " blocks to " + listName);

        return stateSet.toArray(new IBlockState[stateSet.size()]);
    }
}
