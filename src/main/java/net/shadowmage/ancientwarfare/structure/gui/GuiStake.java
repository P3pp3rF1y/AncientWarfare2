package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiSelectFromList;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.container.ContainerStake;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GuiStake extends GuiContainerBase<ContainerStake> {
	private static final int SUBCONTROLS_INDENT = 24;

	private Checkbox showEntity;
	private Checkbox entityOnFire;

	public GuiStake(ContainerBase container) {
		super(container, 200, 200);
	}

	@Override
	public void initElements() {

	}

	@Override
	public void setupElements() {
		clearElements();
		int totalHeight = 8;

		showEntity = new Checkbox(8, totalHeight, 16, 16, "guistrings.stake.show_entity") {
			@Override
			public void onToggled() {
				if (checked()) {
					getContainer().getStake().setEntityName(new ResourceLocation("zombie"));
				} else {
					getContainer().getStake().resetEntityName();
				}
				refreshGui();
			}
		};
		addGuiElement(showEntity);
		showEntity.setChecked(getContainer().getStake().getRenderEntity().isPresent());

		totalHeight += 18;

		if (showEntity.checked()) {
			totalHeight = addEntityControls(totalHeight);
		}

		Checkbox burns = new Checkbox(8, totalHeight + 4, 16, 16, "guistrings.stake.burns") {
			@Override
			public void onToggled() {
				getContainer().getStake().setBurns(checked());
				super.onToggled();
			}
		};
		burns.setChecked(getContainer().getStake().burns());
		addGuiElement(burns);
	}

	private int addEntityControls(int totalHeight) {
		addGuiElement(new Button(SUBCONTROLS_INDENT, totalHeight, 160, 12, getContainer().getStake().getEntityName().toString()) {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiSelectFromList<>(GuiStake.this, getContainer().getStake().getEntityName(), ResourceLocation::toString,
						this::getEntityNames, s -> {
					setText(s.toString());
					getContainer().getStake().setEntityName(s);
				}));
				refreshGui();
			}

			private List<ResourceLocation> getEntityNames() {
				return ForgeRegistries.ENTITIES.getKeys().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
			}
		});

		totalHeight += 16;

		entityOnFire = new Checkbox(SUBCONTROLS_INDENT, totalHeight, 16, 16, "guistrings.stake.entity_on_fire") {
			@Override
			public void onToggled() {
				getContainer().getStake().setEntityOnFire(checked());
				super.onToggled();
			}
		};
		entityOnFire.setChecked(getContainer().getStake().isEntityOnFire());
		addGuiElement(entityOnFire);

		return totalHeight + 16;
	}

	@Override
	protected boolean onGuiCloseRequested() {
		getContainer().updateServer();
		BlockTools.notifyBlockUpdate(getContainer().getStake());
		getContainer().getStake().getWorld().checkLight(getContainer().getStake().getPos());
		return true;
	}
}
