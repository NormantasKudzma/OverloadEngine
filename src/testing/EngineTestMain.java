package testing;

import engine.EngineConfig;
import engine.OverloadEngine;

public class EngineTestMain {
	public static void main(String[] args){
		TestGame game = new TestGame();
		
		EngineConfig config = new EngineConfig();
		config.game = game;
		config.isDebug = true;
		config.isFullscreen = false;
		config.frameHeight = 640;
		config.frameWidth = 640;
		config.viewportHeight = 640;
		config.viewportWidth = 640;
		
		OverloadEngine engine = new OverloadEngine(config);
		engine.run();
	}
}
