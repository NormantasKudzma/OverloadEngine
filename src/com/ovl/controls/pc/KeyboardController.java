package com.ovl.controls.pc;

import org.lwjgl.input.Keyboard;

import com.ovl.controls.Controller;
import com.ovl.controls.ControllerKeybind;

public class KeyboardController extends Controller {
	public KeyboardController(int index) {
		super(index);
		type = Type.TYPE_KEYBOARD;
	}

	@Override
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
