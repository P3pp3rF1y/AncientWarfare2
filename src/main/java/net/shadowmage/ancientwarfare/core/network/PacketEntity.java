package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.interfaces.IEntityPacketHandler;

import java.io.IOException;

public class PacketEntity extends PacketBase {
	private int entityId;
	public NBTTagCompound packetData = new NBTTagCompound();

	public PacketEntity() {}

	public PacketEntity(Entity e) {
		this.entityId = e.getEntityId();
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		data.writeInt(entityId);
		if (packetData != null) {
			try (ByteBufOutputStream outputStream = new ByteBufOutputStream(data)) {
				CompressedStreamTools.writeCompressed(packetData, outputStream);
			}
			catch (IOException e) {
				AncientWarfareCore.LOG.error("Error writing entity packet data: ", e);
			}
		}
	}

	@Override
	protected void readFromStream(ByteBuf data) {
		entityId = data.readInt();
		try (ByteBufInputStream inputStream = new ByteBufInputStream(data)) {
			packetData = CompressedStreamTools.readCompressed(inputStream);
		}
		catch (IOException e) {
			AncientWarfareCore.LOG.error("Error reading entity packet data: ", e);
		}
	}

	@Override
	protected void execute(EntityPlayer player) {
		Entity e = player.world.getEntityByID(entityId);
		if (e instanceof IEntityPacketHandler) {
			((IEntityPacketHandler) e).handlePacketData(packetData);
		}
	}

}
