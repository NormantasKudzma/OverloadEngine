package com.ovl.controls.arm;

import java.util.Arrays;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.opengl.GLWindow;
import com.ovl.controls.Controller;
import com.ovl.controls.ControllerKeybind;
import com.ovl.engine.OverloadEngine;
import com.ovl.engine.arm.OverloadEngineArm;

public class KeyboardController extends Controller {
	private class KeyListenerImpl implements KeyListener {
		@Override
		public void keyPressed(KeyEvent e) {
			buttonStates[e.getKeyCode()] = true;
			buttonStatesChanged[e.getKeyCode()] = true;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			buttonStates[e.getKeyCode()] = false;
			buttonStatesChanged[e.getKeyCode()] = true;
			lastChar = e.getKeyChar();
		}
	}

	private boolean buttonStates[];
	private boolean buttonStatesChanged[];
	private KeyListenerImpl keyListener;
	private char lastChar;
	
	public KeyboardController(int index) {
		super(index);
		type = Type.TYPE_KEYBOARD;
		keyListener = new KeyListenerImpl();
		
		buttonStates = new boolean[1024];
		buttonStatesChanged = new boolean[buttonStates.length];
	}

	public char getLastChar(){
		return lastChar;
	}
	
	@Override
	public void pollController() {
		if (!isActive()){
			return;
		}
		
		if (defaultCallback != null) {
			for (int i = 0; i < buttonStates.length; ++i){
				if (buttonStatesChanged[i]){
					defaultCallback.getCallback().handleEvent(i, null, buttonStates[i] ? 1 : 0);
				}
			}
		}

		if (oneClickCallback != null) {
			for (int i = 0; i < buttonStates.length; ++i){
				if (buttonStatesChanged[i]){
					oneClickCallback.getCallback().handleEvent(i, null, buttonStates[i] ? 1 : 0);
				}
			}
			oneClickCallback = null;
		}

		for (ControllerKeybind bind : keyBindings) {
			if (buttonStatesChanged[bind.getIntmask()]){
				bind.getCallback().handleEvent(bind.getBitmask(), null, buttonStates[bind.getIntmask()] ? 1 : 0);
			}
		}
		
		Arrays.fill(buttonStatesChanged, false);
	}
	
	@Override
	public boolean startController() {
		GLWindow frame = ((OverloadEngineArm)OverloadEngine.getInstance()).getFrame();
		frame.addKeyListener(keyListener);
		
		return super.startController();
	}
	
	@Override
	public boolean stopController() {
		GLWindow frame = ((OverloadEngineArm)OverloadEngine.getInstance()).getFrame();
		frame.removeKeyListener(keyListener);
		
		return super.stopController();
	}
}
