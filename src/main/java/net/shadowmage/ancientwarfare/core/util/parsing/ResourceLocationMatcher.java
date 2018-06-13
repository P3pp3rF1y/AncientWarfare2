package net.shadowmage.ancientwarfare.core.util.parsing;

import jdk.nashorn.internal.ir.annotations.Immutable;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Predicate;

@Immutable
public class ResourceLocationMatcher implements Predicate<ResourceLocation> {
	private final ResourceLocation registryName;

	public ResourceLocationMatcher(String registryName) {
		this.registryName = new ResourceLocation(registryName);
	}

	@Override
	public boolean test(@Nullable ResourceLocation resourceLocation) {
		return registryName.equals(resourceLocation);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ResourceLocationMatcher that = (ResourceLocationMatcher) o;

		return registryName.equals(that.registryName);
	}

	@Override
	public int hashCode() {
		return registryName.hashCode();
	}
}
