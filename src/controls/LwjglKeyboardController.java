package controls;

import org.lwjgl.input.Keyboard;

public class LwjglKeyboardController extends AbstractController {
	public LwjglKeyboardController() {

	}

	@Override
	protected void destroyController() {

	}

	public void pollController() {
		int key = Keyboard.getEventKey();

		if (defaultCallback != null) {
			defaultCallback.getCallback().handleEvent(key, null);
		}

		if (oneClickCallback != null) {
			oneClickCallback.getCallback().handleEvent(key, null);
			oneClickCallback = null;
		}

		for (ControllerKeybind bind : keyBindings) {
			if (Keyboard.isKeyDown(bind.getIntmask())) {
				bind.getCallback().handleEvent(bind.getBitmask(), null);
			}
		}
	}

	@Override
	public void startController() {

	}
}
