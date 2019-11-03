package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.research.ResearchData;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

import java.io.IOException;

public class PacketResearchInit extends PacketBase {
	private NBTTagCompound researchDataTag;

	public PacketResearchInit(ResearchData data) {
		researchDataTag = new NBTTagCompound();
		data.writeToNBT(researchDataTag);
	}

	public PacketResearchInit() {}

	@Override
	protected void writeToStream(ByteBuf data) {
		try (ByteBufOutputStream outputStream = new ByteBufOutputStream(data)) {
			CompressedStreamTools.writeCompressed(researchDataTag, outputStream);
		}
		catch (IOException e) {
			AncientWarfareCore.LOG.error("Error writing research packet data: ", e);
		}
	}

	@Override
	protected void readFromStream(ByteBuf data) {
		try (ByteBufInputStream inputStream = new ByteBufInputStream(data)) {
			researchDataTag = CompressedStreamTools.readCompressed(inputStream);
		}
		catch (IOException e) {
			AncientWarfareCore.LOG.error("Error reading research packet data: ", e);
		}
	}

	@Override
	protected void execute() {
		ResearchTracker.INSTANCE.onClientResearchReceived(researchDataTag);
	}

}
