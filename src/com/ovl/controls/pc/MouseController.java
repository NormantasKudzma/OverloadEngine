package com.ovl.controls.pc;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.ovl.controls.Controller;
import com.ovl.controls.ControllerEventListener;
import com.ovl.controls.ControllerKeybind;
import com.ovl.utils.Vector2;

public class MouseController extends Controller {
	private int buttonStates[];
	private float widthInverse;
	private float heightInverse;
	private Vector2 mousePos;
	private ControllerEventListener mouseMoveListener;

	public MouseController(int index) {
		super(index);
		type = Type.TYPE_MOUSE;
		buttonStates = new int[Mouse.getButtonCount()];
		mousePos = new Vector2();
		widthInverse = 2.0f / Display.getWidth();
		heightInverse = 2.0f / Display.getHeight();
	}

	@Override
	public void pollController() {
		if (!isActive()){
			return;
		}
		
		mousePos.set(Mouse.getX(), Mouse.getY()).mul(widthInverse, heightInverse).sub(1.0f, 1.0f);

		if (mouseMoveListener != null) {
			mouseMoveListener.handleEvent(0, mousePos);
		}

		int intmask;
		int newState;
		
		for (ControllerKeybind bind : keyBindings) {
			intmask = bind.getIntmask();
			
			newState = Mouse.isButtonDown(intmask) ? 1 : 0;
			if (newState != buttonStates[intmask]){
				bind.getCallback().handleEvent(intmask, mousePos, newState);
			}
			
			buttonStates[intmask] = newState;
		}
	}

	public void setMouseMoveListener(ControllerEventListener listener) {
		mouseMoveListener = listener;
	}
}
