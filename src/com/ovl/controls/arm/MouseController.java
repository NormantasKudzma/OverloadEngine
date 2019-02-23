package com.ovl.controls.arm;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.opengl.GLWindow;
import com.ovl.controls.Controller;
import com.ovl.controls.ControllerEventListener;
import com.ovl.controls.ControllerKeybind;
import com.ovl.engine.OverloadEngine;
import com.ovl.engine.arm.OverloadEngineArm;
import com.ovl.utils.Vector2;

public class MouseController extends Controller {
	public static final int LEFT_MOUSE_BUTTON = 1;
	
	private class FrameMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mouseDragged(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mouseMoved(MouseEvent e) {
			mousePosFrame.set(e.getX(), e.getY());
		}

		@Override
		public void mousePressed(MouseEvent e) {
			buttonStates[e.getButton()] = 1;
			buttonStatesChanged[e.getButton()] = true;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			buttonStates[e.getButton()] = 0;
			buttonStatesChanged[e.getButton()] = true;
		}

		@Override
		public void mouseWheelMoved(MouseEvent e) {}
	}
	
	private int buttonStates[];
	private boolean buttonStatesChanged[];
	private float widthInverse;
	private float heightInverse;
	private Vector2 mousePosGame;
	private Vector2 mousePosFrame;
	private ControllerEventListener mouseMoveListener;
	private FrameMouseListener frameMouseListener;

	public MouseController(int index) {
		super(index);
		type = Type.TYPE_MOUSE;
		buttonStates = new int[64];
		buttonStatesChanged = new boolean[64];
		mousePosGame = new Vector2();
		mousePosFrame = new Vector2();
		
		GLWindow frame = ((OverloadEngineArm)OverloadEngine.getInstance()).getFrame();
		widthInverse = 2.0f / frame.getWidth();
		heightInverse = -2.0f / frame.getHeight();
		
		frameMouseListener = new FrameMouseListener();
		//frameMouseMotionListener = new FrameMouseMotionListener();
	}

	@Override
	public void pollController() {
		if (!isActive()){
			return;
		}
		
		mousePosGame.set(mousePosFrame).mul(widthInverse, heightInverse).sub(1.0f, -1.0f);

		if (mouseMoveListener != null) {
			mouseMoveListener.handleEvent(0, mousePosGame);
		}

		int intmask;
		
		for (ControllerKeybind bind : keyBindings) {
			intmask = bind.getIntmask();
			
			if (buttonStatesChanged[intmask]){
				bind.getCallback().handleEvent(intmask, mousePosGame, buttonStates[intmask]);
				buttonStatesChanged[intmask] = false;
			}
		}
	}

	public void setMouseMoveListener(ControllerEventListener listener) {
		mouseMoveListener = listener;
	}
	
	@Override
	public boolean startController() {
		GLWindow frame = ((OverloadEngineArm)OverloadEngine.getInstance()).getFrame();
		System.out.println(frame.getMouseListeners().length + " mouse listeners");
		frame.addMouseListener(0, frameMouseListener);
		
		return super.startController();
	}
	
	@Override
	public boolean stopController() {
		GLWindow frame = ((OverloadEngineArm)OverloadEngine.getInstance()).getFrame();
		frame.removeMouseListener(frameMouseListener);
		//frame.removeMouseMotionListener(frameMouseMotionListener);
		
		return super.stopController();
	}
}
