package net.shadowmage.ancientwarfare.structure.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InjectionTools;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.shadowmage.ancientwarfare.core.util.RenderTools.*;
import static org.lwjgl.opengl.GL11.GL_QUADS;

@SideOnly(Side.CLIENT)
public class PreviewRenderer {
	@SuppressWarnings("NewExpressionSideOnly")
	@SideOnly(Side.CLIENT)
	private static Map<EnumHand, PreviewCache> handPreview = new EnumMap<>(EnumHand.class);

	private PreviewRenderer() {}

	@SideOnly(Side.CLIENT)
	public static void renderTemplatePreview(EntityPlayer player, EnumHand hand, ItemStack stack, float delta, StructureTemplate structure, StructureBB bb, int turns) {
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.translate(-getRenderOffsetX(player, delta), -getRenderOffsetY(player, delta), -getRenderOffsetZ(player, delta));
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(GL_QUADS, DefaultVertexFormats.BLOCK);
		EnumFacing facing = player.getHorizontalFacing();
		Optional<BufferBuilder.State> state = handPreview.getOrDefault(hand, PreviewCache.EMPTY).getState(stack, bb.min, facing);
		if (state.isPresent()) {
			buffer.setVertexState(state.get());
		} else {
			renderPreviewToBuffer(player, structure, bb, turns, buffer);
			handPreview.put(hand, new PreviewCache(stack, bb.min, facing, buffer.getVertexState()));
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

	@SideOnly(Side.CLIENT)
	private static class PreviewCache {
		private ItemStack stack;
		private BlockPos bbMin;
		private EnumFacing facing;
		private BufferBuilder.State state;
		public static final PreviewCache EMPTY = new PreviewCache(ItemStack.EMPTY, BlockPos.ORIGIN, EnumFacing.NORTH, InjectionTools.nullValue());

		private PreviewCache(ItemStack stack, BlockPos bbMin, EnumFacing facing, BufferBuilder.State state) {
			this.stack = stack;
			this.bbMin = bbMin;
			this.facing = facing;
			this.state = state;
		}

		private Optional<BufferBuilder.State> getState(ItemStack stack, BlockPos bbMin, EnumFacing facing) {
			return this.stack == stack && this.bbMin.equals(bbMin) && this.facing == facing ? Optional.of(state) : Optional.empty();
		}
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
