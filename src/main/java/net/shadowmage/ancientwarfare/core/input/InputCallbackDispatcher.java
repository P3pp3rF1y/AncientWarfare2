package net.shadowmage.ancientwarfare.core.input;

import com.google.common.collect.Sets;
import net.minecraft.client.settings.KeyBinding;

import java.util.Set;

public class InputCallbackDispatcher {
	private Set<IInputCallback> inputCallbacks = Sets.newHashSet();
	private KeyBinding keyBinding;

	public KeyBinding getKeyBinding() {
		return keyBinding;
	}

	public InputCallbackDispatcher(KeyBinding keyBinding, IInputCallback initialCallback) {
		this.keyBinding = keyBinding;
		inputCallbacks.add(initialCallback);
	}

	public void addInputCallback(IInputCallback inputCallback) {
		inputCallbacks.add(inputCallback);
	}

	public void onKeyPressed() {
		inputCallbacks.forEach(IInputCallback::onKeyPressed);
	}
}
