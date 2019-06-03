package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiSelectFromList;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcBase;
import net.shadowmage.ancientwarfare.npc.container.ISkinSettingsContainer;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.skin.NpcSkinManager;
import net.shadowmage.ancientwarfare.npc.skin.NpcSkinSettings;

import java.util.List;
import java.util.stream.Collectors;

public class GuiSkinSelection extends GuiContainerBase {
	private final GuiContainerBase<? extends ContainerNpcBase<NpcBase>> parent;
	private Checkbox defaultType;
	private Checkbox playerType;
	private Checkbox npcType;
	private NpcSkinSettings skinSettings;
	private ISkinSettingsContainer skinSettingsContainer;
	private int previewTop = 0;

	private static final int SUBCONTROLS_INDENT = 24;
	private int mouseX;
	private int mouseY;

	private int playerSkinUpdateCooldown = 0;
	private boolean updatePlayerSkin = false;

	public GuiSkinSelection(GuiContainerBase<? extends ContainerNpcBase<NpcBase>> parent, ISkinSettingsContainer skinSettingsContainer) {
		super(new ContainerBase(parent.getContainer().player), 200, 240);
		this.parent = parent;
		this.skinSettings = skinSettingsContainer.getSkinSettings();
		this.skinSettingsContainer = skinSettingsContainer;
	}

	@Override
	public void setupElements() {
		clearElements();

		ySize = calculateYSize();
		guiTop = (this.height - this.ySize) / 2;

		int startHeight = 8;
		defaultType = new Checkbox(8, startHeight, 12, 12, "gui.ancientwarfarenpc.skin_selection.default") {
			@Override
			public void onToggled() {
				super.onToggled();
				if (checked()) {
					skinSettings.setSkinType(NpcSkinSettings.SkinType.DEFAULT);
					playerType.setChecked(false);
					npcType.setChecked(false);
					refreshGui();
				} else {
					setChecked(true);
				}
			}
		};
		defaultType.setChecked(skinSettings.getSkinType() == NpcSkinSettings.SkinType.DEFAULT);
		addGuiElement(defaultType);
		startHeight += 16;

		playerType = new Checkbox(8, startHeight, 12, 12, "gui.ancientwarfarenpc.skin_selection.player") {
			@Override
			public void onToggled() {
				super.onToggled();
				if (checked()) {
					skinSettings.setSkinType(NpcSkinSettings.SkinType.PLAYER);
					defaultType.setChecked(false);
					npcType.setChecked(false);
					refreshGui();
				} else {
					setChecked(true);
				}
			}
		};
		playerType.setChecked(skinSettings.getSkinType() == NpcSkinSettings.SkinType.PLAYER);
		addGuiElement(playerType);
		startHeight += 16;

		if (playerType.checked()) {
			startHeight = addPlayerElements(startHeight);
		}

		npcType = new Checkbox(8, startHeight, 12, 12, "gui.ancientwarfarenpc.skin_selection.npc_type") {
			@Override
			public void onToggled() {
				super.onToggled();
				if (checked()) {
					skinSettings.setSkinType(NpcSkinSettings.SkinType.NPC_TYPE);
					playerType.setChecked(false);
					defaultType.setChecked(false);
					refreshGui();
				} else {
					setChecked(true);
				}
			}
		};
		npcType.setChecked(skinSettings.getSkinType() == NpcSkinSettings.SkinType.NPC_TYPE);
		addGuiElement(npcType);
		startHeight += 16;

		if (npcType.checked()) {
			startHeight = addNpcTypeElements(startHeight);
		}

		if (!defaultType.checked()) {
			Checkbox alex = new Checkbox(8, startHeight, 16, 16, "gui.ancientwarfarenpc.skin_selection.alex_model") {
				@Override
				public void onToggled() {
					super.onToggled();
					skinSettings.setAlexModel(checked());
					refreshGui();
				}
			};
			alex.setChecked(skinSettings.isAlexModel());
			addGuiElement(alex);

			startHeight += 20;
		}
		previewTop = startHeight;
	}

	private int calculateYSize() {
		switch (skinSettings.getSkinType()) {
			case NPC_TYPE:
				return skinSettings.getNpcTypeName().isEmpty() || skinSettings.isRandom() ? 250 : 266;
			case PLAYER:
				return 230;
			case DEFAULT:
			default:
				return 194;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		int posX = guiLeft + xSize / 2;
		int posY = guiTop + previewTop + 8 + 100;

		int backgroundWidth = xSize - 16;
		int backgroundHeigth = 130;
		processPlayerSkinUpdate();
		RenderTools.renderColoredQuad(posX - backgroundWidth / 2, guiTop + previewTop, backgroundWidth, backgroundHeigth, 0, 0, 0);
		drawEntityOnScreen(posX, posY, 35, posX - mouseX, posY - 50 - mouseY, parent.getContainer().entity);
	}

	private void processPlayerSkinUpdate() {
		if (playerSkinUpdateCooldown > 0) {
			playerSkinUpdateCooldown--;
			return;
		}
		if (!updatePlayerSkin) {
			return;
		}
		updatePlayerSkin = false;
		skinSettingsContainer.handleNpcSkinUpdate();
		playerSkinUpdateCooldown = 40;
	}

	private int addNpcTypeElements(int startHeight) {
		addGuiElement(new Button(SUBCONTROLS_INDENT, startHeight, 150, 12,
				skinSettings.getNpcTypeName().isEmpty() ? "gui.ancientwarfarenpc.skin_selection.select_npc_type" : skinSettings.getNpcTypeName()) {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiSelectFromList<>(GuiSkinSelection.this, skinSettings.getNpcTypeName(), s -> s,
						this::getNpcTypes, s -> {
					setText(s);
					skinSettings.setNpcTypeName(s);
					skinSettings.resetNpcTypeSkin();
					refreshGui();
				}));
			}

			private List<String> getNpcTypes() {
				return NpcSkinManager.getSkinTypes().stream().sorted().collect(Collectors.toList());
			}
		});

		startHeight += 16;

		Checkbox random = new Checkbox(SUBCONTROLS_INDENT, startHeight, 16, 16, "gui.ancientwarfarenpc.skin_selection.random") {
			@Override
			public void onToggled() {
				super.onToggled();
				skinSettings.setRandom(checked());
				refreshGui();
			}
		};
		random.setChecked(skinSettings.isRandom());
		addGuiElement(random);

		startHeight += 20;

		if (!skinSettings.getNpcTypeName().isEmpty() && !skinSettings.isRandom()) {
			addGuiElement(new Button(SUBCONTROLS_INDENT, startHeight, 150, 12,
					skinSettings.getNpcTypeSkin().map(rl -> rl.toString().replace("ancientwarfare:skinpack/", "").replace(".png", "")).orElse("gui.ancientwarfarenpc.skin_selection.select_npc_skin")) {
				@Override
				protected void onPressed() {
					Minecraft.getMinecraft().displayGuiScreen(new GuiSelectFromList<>(GuiSkinSelection.this,
							skinSettings.getNpcTypeSkin().orElse(null), rl -> rl == null ? "" : rl.toString().replace("ancientwarfare:skinpack/", "").replace(".png", ""),
							this::getNpcTypes, rl -> {
						setText(rl.toString());
						skinSettings.setNpcTypeSkin(rl);
					}));
				}

				private List<ResourceLocation> getNpcTypes() {
					return NpcSkinManager.getTypeSkins(skinSettings.getNpcTypeName()).stream().sorted().collect(Collectors.toList());
				}
			});
			startHeight += 16;
		}
		return startHeight;
	}

	private int addPlayerElements(int startHeight) {
		addGuiElement(new Text(SUBCONTROLS_INDENT, startHeight, 100, skinSettings.getPlayerName(), this) {
			@Override
			public void onTextUpdated(String oldText, String newText) {
				skinSettings.setPlayerName(newText);
				updatePlayerSkin = true;
			}
		});

		return startHeight + 16;
	}

	public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) posX, (float) posY, 50.0F);
		GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		float f = ent.renderYawOffset;
		float f1 = ent.rotationYaw;
		float f2 = ent.rotationPitch;
		float f3 = ent.prevRotationYawHead;
		float f4 = ent.rotationYawHead;
		GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
		ent.renderYawOffset = (float) Math.atan((double) (mouseX / 40.0F)) * 20.0F;
		ent.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
		ent.rotationPitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
		ent.rotationYawHead = ent.rotationYaw;
		ent.prevRotationYawHead = ent.rotationYaw;
		GlStateManager.translate(0.0F, 0.0F, 0.0F);
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		rendermanager.setPlayerViewY(180.0F);
		rendermanager.setRenderShadow(false);
		rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
		rendermanager.setRenderShadow(true);
		ent.renderYawOffset = f;
		ent.rotationYaw = f1;
		ent.rotationPitch = f2;
		ent.prevRotationYawHead = f3;
		ent.rotationYawHead = f4;
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	@Override
	protected boolean onGuiCloseRequested() {
		skinSettingsContainer.setSkinSettings(skinSettings);
		skinSettingsContainer.handleNpcSkinUpdate();
		parent.refreshGui();
		Minecraft.getMinecraft().displayGuiScreen(parent);
		return false;
	}

	@Override
	public void initElements() {
		//noop
	}
}
