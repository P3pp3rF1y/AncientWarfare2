package net.shadowmage.ancientwarfare.structure.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;

public class PacketStructureRemove extends PacketBase {
	private String structureName;

	public PacketStructureRemove() {}

	public PacketStructureRemove(String name) {
		structureName = name;
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		StringTools.writeString(data, structureName);
	}

	@Override
	protected void readFromStream(ByteBuf data) {
		structureName = StringTools.readString(data);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void execute() {
		StructureTemplateManager.removeTemplate(structureName);
	}
}
