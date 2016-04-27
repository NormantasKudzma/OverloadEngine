package controls;

import org.lwjgl.input.Keyboard;

public class KeyboardController extends AbstractController {
	public KeyboardController(EController type, int index) {
		super(type, index);
	}

	public void pollController() {
		if (!isActive()){
			return;
		}
		
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
}
