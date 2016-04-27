package controls;

import java.util.ArrayList;

public abstract class AbstractController implements IController {
	protected enum ControllerState {
		INACTIVE,
		STARTED,
		STOPPED,
		INVALID
	}

	protected ControllerState state = ControllerState.INACTIVE;
	protected ControllerKeybind defaultCallback;
	protected long defaultDataValue;
	protected Thread eventThread;
	protected ArrayList<ControllerKeybind> keyBindings = new ArrayList<ControllerKeybind>();
	protected ControllerKeybind oneClickCallback;
	protected EController type;
	protected int index;

	public AbstractController(EController type, int index){
		this.type = type;
		this.index = index;
	}
	
	public void addKeybind(long bitmask, ControllerEventListener callback) {
		addKeybind(new ControllerKeybind(bitmask, callback));
	}

	public void addKeybind(ControllerKeybind keybind) {
		keyBindings.add(keybind);
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

	public int getIndex(){
		return index;
	}
	
	public EController getType(){
		return type;
	}
	
	public boolean isActive() {
		return state == ControllerState.STARTED;
	}

	public ControllerKeybind removeKeybind(ControllerEventListener callback){
		ControllerKeybind temp = null;
		for (ControllerKeybind bind : keyBindings) {
			if (bind.getCallback() == callback) {
				temp = bind;
				keyBindings.remove(temp);
				break;
			}
		}
		return temp;
	}
	
	public ControllerKeybind removeKeybind(long bitmask) {
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

	public void startController(){
		ControllerManager.getInstance().controllerStarted(this);
		state = ControllerState.STARTED;
	}

	public void stopController() {
		ControllerManager.getInstance().controllerStopped(this);
		state = ControllerState.STOPPED;
	}

	protected void destroyController(){
		stopController();
		state = ControllerState.INVALID;
		defaultCallback = null;
		eventThread = null;
		oneClickCallback = null;
		if (keyBindings != null){
			keyBindings.clear();
			keyBindings = null;
		}
	}
}
