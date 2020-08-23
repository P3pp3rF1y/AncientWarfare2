package net.shadowmage.ancientwarfare.structure.render;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

//CC bakery requires extended state and we can't use extended state container with no unlisted properties as that just return regular state
// So that's why this blank state container which doesn't care about the number of unlisted properties
public class BlankExtendedBlockStateContainer extends ExtendedBlockState {
	public BlankExtendedBlockStateContainer(Block blockIn) {
		super(blockIn, new IProperty[] {}, new IUnlistedProperty[] {});
	}

	@Override
	protected StateImplementation createState(
			@Nonnull Block block,
			@Nonnull ImmutableMap<IProperty<?>, Comparable<?>> properties, @Nullable ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties) {
		return new BlankExtendedStateImplementation(block);
	}

	private class BlankExtendedStateImplementation extends ExtendedStateImplementation {
		protected BlankExtendedStateImplementation(Block block) {
			super(block, ImmutableMap.of(), ImmutableMap.of(), null, null);
		}
	}
}
