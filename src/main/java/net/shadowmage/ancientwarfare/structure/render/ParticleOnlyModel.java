package net.shadowmage.ancientwarfare.structure.render;

import codechicken.lib.model.DummyBakedModel;
import codechicken.lib.render.particle.IModelParticleProvider;
import codechicken.lib.texture.TextureUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

public class ParticleOnlyModel extends DummyBakedModel implements IModelParticleProvider, ISelectiveResourceReloadListener {
	public static final ParticleOnlyModel INSTANCE = new ParticleOnlyModel("planks_oak");
	private Set<TextureAtlasSprite> sprite;
	private String blockTexture;

	public ParticleOnlyModel(String blockTexture) {
		this.blockTexture = blockTexture;
	}

	private Set<TextureAtlasSprite> getSprite() {
		if (sprite == null) {
			sprite = Collections.singleton(TextureUtils.getBlockTexture(blockTexture));
		}
		return sprite;
	}

	@Override
	public Set<TextureAtlasSprite> getHitEffects(@Nonnull RayTraceResult traceResult, IBlockState state, IBlockAccess world, BlockPos pos) {
		return getSprite();
	}

	@Override
	public Set<TextureAtlasSprite> getDestroyEffects(IBlockState state, IBlockAccess world, BlockPos pos) {
		return getSprite();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return getSprite().iterator().next();
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
		if (resourcePredicate.test(VanillaResourceType.TEXTURES)) {
			sprite = null;
		}
	}
}
