package net.shadowmage.ancientwarfare.npc.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

import java.io.IOException;
import java.util.UUID;

public class UUIDDataSerializer implements DataSerializer<UUID> {
	public static final UUIDDataSerializer INSTANCE = new UUIDDataSerializer();

	static {
		DataSerializers.registerSerializer(INSTANCE);
	}

	private UUIDDataSerializer() {
	}

	@Override
	public void write(PacketBuffer buf, UUID value) {
		buf.writeUniqueId(value);
	}

	@Override
	public UUID read(PacketBuffer buf) throws IOException {
		return buf.readUniqueId();
	}

	@Override
	public DataParameter<UUID> createKey(int id) {
		return new DataParameter<>(id, this);
	}

	@Override
	public UUID copyValue(UUID value) {
		return value;
	}
}
