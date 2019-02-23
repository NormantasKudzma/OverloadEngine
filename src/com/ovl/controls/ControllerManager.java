package com.ovl.controls;

import java.util.ArrayList;

public class ControllerManager {
	private static final ControllerManager INSTANCE = new ControllerManager();

	private ArrayList<Controller> allControllers = new ArrayList<Controller>();
	private ArrayList<Controller> activeControllers = new ArrayList<Controller>();

	private ControllerManager() {
		
	}

	public void addController(Controller controller){
		if (!allControllers.contains(controller)){
			allControllers.add(controller);
		}
	}
	
	public void controllerStarted(Controller controller){
		if (!activeControllers.contains(controller)){
			activeControllers.add(controller);
		}
	}
	
	public void controllerStopped(Controller controller){
		activeControllers.remove(controller);
	}
	
	public void destroyManager() {
		for (Controller c : allControllers) {
			c.destroyController();
		}
	}

	public Controller getController(Controller.Type type) {
		return getController(type, 0);
	}

	public Controller getController(Controller.Type type, int index) {
		for (Controller c : allControllers){
			if (c.getType() == type && c.getIndex() == index){
				return c;
			}
		}

		return null;
	}
	
	public void removeController(Controller controller){
		allControllers.remove(controller);
	}

	public static ControllerManager getInstance() {
		return INSTANCE;
	}

	public int getNumControllers(Controller.Type type) {
		int numControllers = 0;
		while (getController(type, numControllers) != null){
			++numControllers;
		}
		return numControllers;
	}

	public void pollControllers() {
		for (Controller c : activeControllers) {
			c.pollController();
		}
	}
}
