package com.ovl.controls;

import java.util.ArrayList;

public abstract class Controller {
	public enum Type {
		TYPE_INVALID, 
		TYPE_USB, 
		TYPE_KEYBOARD, 
		TYPE_MOUSE;

		public static Type getFromString(String name) {
			Type types[] = Type.values();
			for (Type i : types) {
				if (i.toString().equalsIgnoreCase(name)) {
					return i;
				}
			}

			return TYPE_INVALID;
		}
	}
	
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
	protected Type type;
	protected int index;

	public Controller(int index){
		this.index = index;
		type = Type.TYPE_INVALID;
	}
	
	public void addKeybind(long bitmask, ControllerEventListener callback) {
		if (callback == null){
			System.err.println("Keybind callback cannot be null.");
			return;
		}
		addKeybind(new ControllerKeybind(bitmask, callback));
	}

	public void addKeybind(ControllerKeybind keybind) {
		if (keybind == null){
			System.err.println("Keybind cannot be null.");
			return;
		}
		keyBindings.add(keybind);
	}

	public void clearKeybinds() {
		keyBindings.clear();
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
	
	public ArrayList<ControllerKeybind> getAllKeybinds() {
		return keyBindings;
	}

	public long getDefaultBitmaskValue() {
		return defaultDataValue;
	}

	public int getIndex(){
		return index;
	}
	
	public Type getType(){
		return type;
	}
	
	public boolean isActive() {
		return state == ControllerState.STARTED;
	}

	public abstract void pollController();	
	
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
		if (callback != null){
			defaultCallback = new ControllerKeybind(-1, callback);
		}
		else {
			defaultCallback = null;
		}
	}

	public boolean startController(){
		if (state == ControllerState.INVALID){
			System.err.println("Controller " + type.toString() + " cannot be used after destruction!");
			return false;
		}
		
		ControllerManager.getInstance().controllerStarted(this);
		state = ControllerState.STARTED;
		return true;
	}

	public boolean stopController() {
		if (state == ControllerState.INVALID){
			System.err.println("Controller " + type.toString() + " cannot be used after destruction!");
			return false;
		}
		
		ControllerManager.getInstance().controllerStopped(this);
		state = ControllerState.STOPPED;
		return true;
	}
}
