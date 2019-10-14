package net.shadowmage.ancientwarfare.npc.entity.faction.attributes;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityLiving;

import java.util.Map;
import java.util.Optional;

public class AdditionalAttributes {
	private AdditionalAttributes() {}

	public static final ClassAttribute HORSE_ENTITY = new ClassAttribute("horse_entity", EntityLiving.class);
	public static final BoolAttribute BURNS_IN_SUN = new BoolAttribute("burns_in_sun");
	public static final BoolAttribute UNDEAD = new BoolAttribute("undead");
	private static final Map<String, IAdditionalAttribute<?>> ALL_ATTRIBUTES =
			ImmutableMap.of(HORSE_ENTITY.getName(), HORSE_ENTITY, BURNS_IN_SUN.getName(), BURNS_IN_SUN, UNDEAD.getName(), UNDEAD);

	public static IAdditionalAttribute getByName(String attributeName) {
		return ALL_ATTRIBUTES.getOrDefault(attributeName, INVALID_ATTRIBUTE);
	}

	private static final IAdditionalAttribute<String> INVALID_ATTRIBUTE = new IAdditionalAttribute<String>() {
		@Override
		public String getName() {
			return "invalid_attribute";
		}

		@Override
		public Class<String> getValueClass() {
			return String.class;
		}

		@Override
		public Optional<String> parseValue(String value) {
			return Optional.of("invalid value");
		}
	};
}
