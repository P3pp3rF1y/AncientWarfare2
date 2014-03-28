package net.shadowmage.ancientwarfare.core.inventory;

public enum InventorySide
{
TOP("guistrings.inventory.side.top"),
BOTTOM("guistrings.inventory.side.bottom"),
FRONT("guistrings.inventory.side.front"),
REAR("guistrings.inventory.side.rear"),
LEFT("guistrings.inventory.side.left"),
RIGHT("guistrings.inventory.side.right"),
NONE("guistrings.inventory.side.none");

private final String regName;

InventorySide(String transKey)
  {
  regName = transKey;
  }

public String getTranslationKey()
  {
  return regName;
  }


}
