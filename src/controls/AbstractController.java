package controls;

import java.util.ArrayList;

public abstract class AbstractController implements IController {
	public static final long DEFAULT_POLL_TIMEOUT_MLS = 50; // Poll sleep time in miliseconds
	public static final long DEFAULT_POLL_TIMEOUT_MCS = 1000000; // Poll sleep time in microseconds	

	protected ControllerKeybind defaultCallback;
	protected long defaultDataValue;
	protected Thread eventThread;
	protected volatile boolean isActive;
	protected volatile boolean isStopped;
	protected ArrayList<ControllerKeybind> keyBindings = new ArrayList<ControllerKeybind>();
	protected ControllerKeybind oneClickCallback;

	public void addKeybind(long bitmask, ControllerEventListener callback) {
		addKeybind(new ControllerKeybind(bitmask, callback));
	}

	public void addKeybind(ControllerKeybind keybind) {
		keyBindings.add(keybind);
	}

	public ControllerKeybind clearKeybind(long bitmask) {
		ControllerKeybind temp = null;
		for (ControllerKeybind bind : keyBindings) {
			if (bind.getBitmask() == bitmask) {
				temp = bind;
				keyBindings.remove(temp);
				break;
			}
		}
		return temp;
	}

	public void clearKeybinds() {
		keyBindings.clear();
	}

	public ArrayList<ControllerKeybind> getAllKeybinds() {
		return keyBindings;
	}

	public long getDefaultBitmaskValue() {
		return defaultDataValue;
	}

	public boolean isActive() {
		return isActive && !isStopped;
	}

	public ControllerKeybind removeUnmaskedCallback() {
		ControllerKeybind temp = defaultCallback;
		defaultCallback = null;
		return temp;
	}

	public void setOneClickCallback(ControllerEventListener callback) {
		oneClickCallback = new ControllerKeybind(0xffff, callback);
	}

	public void setUnmaskedCallback(ControllerEventListener callback) {
		defaultCallback = new ControllerKeybind(-1, callback);
	}

	public abstract void startController();

	public void stopController() {
		if (isStopped) {
			return;
		}

		isActive = false;
		isStopped = true;
	}

	protected abstract void destroyController();
}
