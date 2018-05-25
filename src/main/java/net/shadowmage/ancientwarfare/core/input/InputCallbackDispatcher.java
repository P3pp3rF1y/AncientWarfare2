package net.shadowmage.ancientwarfare.core.input;

import com.google.common.collect.Sets;
import net.minecraft.client.settings.KeyBinding;

import java.util.Set;

class InputCallbackDispatcher {
	private Set<IInputCallback> inputCallbacks = Sets.newHashSet();
	private KeyBinding keyBinding;

	KeyBinding getKeyBinding() {
		return keyBinding;
	}

	InputCallbackDispatcher(KeyBinding keyBinding, IInputCallback initialCallback) {
		this.keyBinding = keyBinding;
		inputCallbacks.add(initialCallback);
	}

	void addInputCallback(IInputCallback inputCallback) {
		inputCallbacks.add(inputCallback);
	}

	void onKeyPressed() {
		inputCallbacks.forEach(IInputCallback::onKeyPressed);
	}
}
