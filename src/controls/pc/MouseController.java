package controls.pc;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import utils.Vector2;
import controls.Controller;
import controls.ControllerEventListener;
import controls.ControllerKeybind;

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
		
		mousePos.set(Mouse.getX(), Mouse.getY()).mul(widthInverse, heightInverse);
		int intmask;

		if (mouseMoveListener != null) {
			mouseMoveListener.handleEvent(0, mousePos);
		}

		for (int i = 0; i < Mouse.getButtonCount(); ++i){
			buttonStates[i] += Mouse.isButtonDown(i) ? 1 : -buttonStates[i];
		}
		
		for (ControllerKeybind bind : keyBindings) {
			intmask = bind.getIntmask();
			if (Mouse.isButtonDown(intmask)){
				bind.getCallback().handleEvent(intmask, mousePos, buttonStates[intmask]);
			}
		}
	}

	public void setMouseMoveListener(ControllerEventListener listener) {
		mouseMoveListener = listener;
	}
}
