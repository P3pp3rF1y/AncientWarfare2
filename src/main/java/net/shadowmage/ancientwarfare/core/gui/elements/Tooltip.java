package net.shadowmage.ancientwarfare.core.gui.elements;

public abstract class Tooltip
{

private GuiElement owner;

public Tooltip(GuiElement owner)
  {
  this.owner = owner;
  }

public abstract void renderTooltip(int mouseX, int mouseY, float partialTick);

}
