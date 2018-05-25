package net.shadowmage.ancientwarfare.vehicle.input;

import net.minecraft.client.settings.KeyBinding;
import net.shadowmage.ancientwarfare.vehicle.network.PacketVehicleInput;

import java.util.function.Consumer;

interface IVehicleMovementHandler {
	KeyBinding getKeyBinding();

	KeyBinding getReverseKeyBinding();

	void updatePacket(PacketVehicleInput pkt);

	class Impl implements IVehicleMovementHandler {
		private KeyBinding keyBinding;
		private KeyBinding reverseKeyBinding;
		private Consumer<PacketVehicleInput> doUpdatePacket;

		public Impl(KeyBinding keyBinding, KeyBinding reverseKeyBinding, Consumer<PacketVehicleInput> doUpdatePacket) {
			this.keyBinding = keyBinding;
			this.reverseKeyBinding = reverseKeyBinding;
			this.doUpdatePacket = doUpdatePacket;
		}

		@Override
		public KeyBinding getKeyBinding() {
			return keyBinding;
		}

		@Override
		public KeyBinding getReverseKeyBinding() {
			return reverseKeyBinding;
		}

		@Override
		public void updatePacket(PacketVehicleInput pkt) {
			doUpdatePacket.accept(pkt);
		}
	}
}
