package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemBlockMeta extends ItemBlock
{

public ItemBlockMeta(Block block)
  {
  super(block);
  }

@Override
public int getMetadata(int itemDamage)
  {
  return itemDamage;
  }

}
