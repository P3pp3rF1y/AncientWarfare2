package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InjectionTools;
import net.shadowmage.ancientwarfare.structure.event.IBoxRenderer;
import net.shadowmage.ancientwarfare.structure.gui.GuiStructureSelection;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class ItemStructureBuilder extends ItemBaseStructure implements IItemKeyInterface, IBoxRenderer {
	private static final String LOCK_POS_TAG = "lockPos";

	public ItemStructureBuilder(String name) {
		super(name);
		setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		String structure = "guistrings.structure.no_selection";
		ItemStructureSettings viewSettings = ItemStructureSettings.getSettingsFor(stack);
		if (viewSettings.hasName()) {
			structure = viewSettings.name;
		}
		tooltip.add(I18n.format("guistrings.current_structure") + " " + I18n.format(structure));
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
		return false;
	}

	@Override
	public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		return altFunction == ItemAltFunction.ALT_FUNCTION_1;
	}

	@Override
	public void onKeyAction(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		if (player == null || player.world.isRemote) {
			return;
		}
		ItemStructureSettings buildSettings = ItemStructureSettings.getSettingsFor(stack);
		if (buildSettings.hasName()) {
			StructureTemplate template = StructureTemplateManager.INSTANCE.getTemplate(buildSettings.name);
			if (template == null) {
				player.sendMessage(new TextComponentTranslation("guistrings.template.not_found"));
				return;
			}
			BlockPos bpHit = BlockTools.getBlockClickedOn(player, player.world, true);
			if (bpHit == null) {
				return;
			}//no hit position, clicked on air
			StructureBuilder builder = new StructureBuilder(player.world, template, player.getHorizontalFacing(), bpHit);
			builder.getTemplate().getValidationSettings().preGeneration(player.world, bpHit, player.getHorizontalFacing(), builder.getTemplate(), builder.getBoundingBox());
			builder.instantConstruction();
			builder.getTemplate().getValidationSettings().postGeneration(player.world, bpHit, builder.getBoundingBox());
			if (!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}
		} else {
			player.sendMessage(new TextComponentTranslation("guistrings.structure.no_selection"));
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!player.world.isRemote && !player.isSneaking() && player.capabilities.isCreativeMode) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_BUILDER, 0, 0, 0);
		}
		ItemStack stack = player.getHeldItem(hand);
		if (player.isSneaking() && getLockPosition(stack).isPresent()) {
			removeLockPosition(stack);
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	private void removeLockPosition(ItemStack stack) {
		//noinspection ConstantConditions
		stack.getTagCompound().removeTag(LOCK_POS_TAG);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		ItemStructureSettings buildSettings = ItemStructureSettings.getSettingsFor(stack);
		if (player.isSneaking() && buildSettings.hasName()) {
			if (!worldIn.isRemote) {
				lockPosition(stack, pos.offset(facing), player);
			}
			return EnumActionResult.SUCCESS;
		}

		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	private void lockPosition(ItemStack stack, BlockPos pos, EntityPlayer player) {
		NBTTagCompound tag = stack.getTagCompound();
		tag.setLong(LOCK_POS_TAG, pos.toLong());
		tag.setByte("lockFacing", (byte) player.getHorizontalFacing().getHorizontalIndex());
	}

	private Optional<Tuple<BlockPos, EnumFacing>> getLockPosition(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		//noinspection ConstantConditions
		return tag.hasKey(LOCK_POS_TAG) ? Optional.of(new Tuple<>(BlockPos.fromLong(tag.getLong(LOCK_POS_TAG)), EnumFacing.HORIZONTALS[tag.getByte("lockFacing")])) : Optional.empty();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBox(EntityPlayer player, ItemStack stack, float delta) {
		ItemStructureSettings settings = ItemStructureSettings.getSettingsFor(stack);
		if (!settings.hasName()) {
			return;
		}
		String name = settings.name();
		StructureTemplate structure = StructureTemplateManager.INSTANCE.getTemplate(name);
		if (structure == null) {
			return;
		}
		Optional<Tuple<BlockPos, EnumFacing>> lockPosition = getLockPosition(stack);
		BlockPos hit;
		EnumFacing facing;
		if (lockPosition.isPresent()) {
			hit = lockPosition.get().getFirst();
			facing = lockPosition.get().getSecond();
		} else {
			hit = BlockTools.getBlockClickedOn(player, player.world, true);
			if (hit == null) {
				return;
			}
			facing = player.getHorizontalFacing();
		}

		StructureBB bb = new StructureBB(hit, facing, structure.getSize(), structure.getOffset());
		int turns = (facing.getHorizontalIndex() + 2) % 4;
		Util.renderBoundingBox(player, bb.min, bb.max, delta);
		renderTemplatePreview(player, stack, delta, structure, bb, turns);
	}

	private static class PreviewCache {
		private ItemStack stack;
		private BlockPos bbMin;
		private BufferBuilder.State state;
		public static final PreviewCache EMPTY = new PreviewCache(ItemStack.EMPTY, BlockPos.ORIGIN, InjectionTools.nullValue());

		private PreviewCache(ItemStack stack, BlockPos bbMin, BufferBuilder.State state) {
			this.stack = stack;
			this.bbMin = bbMin;
			this.state = state;
		}

		public Optional<BufferBuilder.State> getState(ItemStack stack, BlockPos bbMin) {
			return this.stack == stack && this.bbMin.equals(bbMin) ? Optional.of(state) : Optional.empty();
		}
	}

	@SuppressWarnings("NewExpressionSideOnly")
	@SideOnly(Side.CLIENT)
	private static PreviewCache cachedPreview = PreviewCache.EMPTY;

	@SideOnly(Side.CLIENT)
	private static void renderTemplatePreview(EntityPlayer player, ItemStack stack, float delta, StructureTemplate structure, StructureBB bb, int turns) {
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.translate(-getRenderOffsetX(player, delta), -getRenderOffsetY(player, delta), -getRenderOffsetZ(player, delta));
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(GL_QUADS, DefaultVertexFormats.BLOCK);
		Optional<BufferBuilder.State> state = cachedPreview.getState(stack, bb.min);
		if (state.isPresent()) {
			buffer.setVertexState(state.get());
		} else {
			renderPreviewToBuffer(player, structure, bb, turns, buffer);
			cachedPreview = new PreviewCache(stack, bb.min, buffer.getVertexState());
		}
		tessellator.draw();
		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}

	@SideOnly(Side.CLIENT)
	private static void renderPreviewToBuffer(EntityPlayer player, StructureTemplate structure, StructureBB bb, int turns, BufferBuilder buffer) {
		TemplateBlockAccess blockAccess = new TemplateBlockAccess(player.world, structure, bb, turns);
		for (int pass = 0; pass < 3; pass++) {
			for (int y = 0; y < structure.getSize().getY(); y++) {
				for (int x = 0; x < structure.getSize().getX(); x++) {
					for (int z = 0; z < structure.getSize().getZ(); z++) {
						BlockPos translateTo = BlockTools.rotateInArea(new BlockPos(x, y, z), structure.getSize().getX(), structure.getSize().getZ(), turns).add(bb.min);
						structure.getBlockRuleAt(new Vec3i(x, y, z)).ifPresent(r -> r.renderRule(turns, translateTo, blockAccess, buffer));
					}
				}
			}
		}
	}

	public static double getRenderOffsetX(EntityPlayer player, float partialTick) {
		return player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTick;
	}

	public static double getRenderOffsetY(EntityPlayer player, float partialTick) {
		return player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTick;
	}

	public static double getRenderOffsetZ(EntityPlayer player, float partialTick) {
		return player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTick;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		super.registerClient();

		NetworkHandler.registerGui(NetworkHandler.GUI_BUILDER, GuiStructureSelection.class);
	}

	private static class TemplateBlockAccess implements IBlockAccess {
		private final int templateXSize;
		private final int templateZSize;
		private World world;
		private StructureTemplate template;
		private StructureBB bb;
		private int turns;

		private Map<BlockPos, IBlockState> positionStates = new HashMap<>();

		private TemplateBlockAccess(World world, StructureTemplate template, StructureBB bb, int turns) {
			this.world = world;
			this.template = template;
			this.bb = bb;
			this.turns = turns;
			int xSize = template.getSize().getX();
			int zSize = template.getSize().getZ();
			if (turns % 2D != 0) {
				this.templateXSize = zSize;
				this.templateZSize = xSize;
			} else {
				this.templateXSize = xSize;
				this.templateZSize = zSize;
			}
		}

		@Nullable
		@Override
		public TileEntity getTileEntity(BlockPos pos) {
			return null;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public int getCombinedLight(BlockPos pos, int lightValue) {
			return !bb.contains(pos) ? world.getCombinedLight(pos, lightValue) : 0;
		}

		@Override
		public IBlockState getBlockState(BlockPos pos) {
			if (!positionStates.containsKey(pos)) {
				if (!bb.contains(pos)) {
					positionStates.put(pos, world.getBlockState(pos));
				} else {
					Vec3i tempPos = BlockTools.rotateInArea(pos.add(-bb.min.getX(), -bb.min.getY(), -bb.min.getZ()), templateXSize, templateZSize, -turns);
					positionStates.put(pos, template.getBlockRuleAt(tempPos).map(templateRuleBlock -> templateRuleBlock.getState(turns)).orElse(Blocks.AIR.getDefaultState()));
				}
			}
			return positionStates.get(pos);
		}

		@Override
		public boolean isAirBlock(BlockPos pos) {
			return false;
		}

		@Override
		public Biome getBiome(BlockPos pos) {
			return Biomes.PLAINS;
		}

		@Override
		public int getStrongPower(BlockPos pos, EnumFacing direction) {
			return 0;
		}

		@Override
		public WorldType getWorldType() {
			return WorldType.DEFAULT;
		}

		@Override
		public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean defaultValue) {
			return false;
		}
	}
}
