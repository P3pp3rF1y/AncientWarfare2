package net.shadowmage.ancientwarfare.vehicle.input;

import com.google.common.collect.Sets;
import net.shadowmage.ancientwarfare.core.input.InputHandler;

import java.util.Set;

public class InputCallbackDispatcher {
	private Set<InputHandler.IInputCallback> inputCallbacks = Sets.newHashSet();

	public InputCallbackDispatcher(InputHandler.IInputCallback initialCallback) {
		inputCallbacks.add(initialCallback);
	}

	public void addInputCallback(InputHandler.IInputCallback inputCallback) {
		inputCallbacks.add(inputCallback);
	}

	public void onKeyPressed() {
		inputCallbacks.forEach(InputHandler.IInputCallback::onKeyPressed);
	}
}
