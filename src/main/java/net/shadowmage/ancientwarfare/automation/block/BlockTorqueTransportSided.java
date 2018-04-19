package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;

public abstract class BlockTorqueTransportSided extends BlockTorqueTransport {
	public static final IUnlistedProperty<Boolean>[] CONNECTIONS = new IUnlistedProperty[6];

	static {
		for (EnumFacing facing : EnumFacing.VALUES) {
			CONNECTIONS[facing.ordinal()] = Properties.toUnlisted(PropertyBool.create("connection_" + facing.name()));
		}
	}

	protected BlockTorqueTransportSided(String regName) {
		super(regName);
	}

	@Override
	protected void addProperties(BlockStateContainer.Builder builder) {
		super.addProperties(builder);
		builder.add(CONNECTIONS);
	}

}
