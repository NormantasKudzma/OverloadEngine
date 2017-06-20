package com.ovl.engine;

import com.ovl.game.BaseGame;

public class EngineConfig {
	public BaseGame game = null;
	
	public String title = "Game";
	public String configPath = "";
	public boolean isDebug = false;
	public int targetFps = 60;
	
	public EngineConfig(){
		
	}
	
	public boolean validateConfig(){
		if (game == null){
			System.err.println("No game was specified in engine configuration.");
			return false;
		}
		
		if (title == null){
			System.err.println("Window title cannot be null.");
			return false;
		}

		if (targetFps <= 0){
			System.err.println("Target fps cannot be less than 1.");
			return false;
		}

		if (configPath == null){
			System.err.println("Configuration file path must not be null.");
			return false;
		}
				
		return true;
	}
}
