package controls;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import utils.Vector2;

public class LwjglMouseController extends AbstractController {
	/**
	 * 0 - not clicked 1 - on click 2+ - hold
	 */
	private int buttonStates[];
	private float widthInverse;
	private float heightInverse;
	private Vector2 mousePos;
	private ControllerEventListener mouseMoveListener;

	public LwjglMouseController() {
		buttonStates = new int[Mouse.getButtonCount()];
		mousePos = new Vector2();
		widthInverse = 2.0f / Display.getWidth();
		heightInverse = 2.0f / Display.getHeight();
	}

	@Override
	public void pollController() {
		mousePos.set(Mouse.getX(), Mouse.getY()).mul(widthInverse, heightInverse);
		int intmask;

		if (mouseMoveListener != null) {
			mouseMoveListener.handleEvent(0, mousePos);
		}

		for (ControllerKeybind bind : keyBindings) {
			intmask = bind.getIntmask();
			if (Mouse.isButtonDown(intmask)) {
				bind.getCallback().handleEvent(intmask, mousePos, ++buttonStates[intmask]);
			}
			else {
				buttonStates[intmask] = 0;
			}
		}
	}

	public void setMouseMoveListener(ControllerEventListener listener) {
		mouseMoveListener = listener;
	}

	@Override
	public void startController() {
		//stub
	}

	@Override
	protected void destroyController() {
		//stub
	}
}
