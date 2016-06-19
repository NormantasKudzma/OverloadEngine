package testing;

import engine.EngineConfig;
import engine.OverloadEngine;

public class EngineTestMain {
	public static void main(String args[]){
		EngineConfig config = new EngineConfig();
		config.game = new TestGame();
		config.isDebug = true;
		config.isFullscreen = false;
		
		config.frameHeight = 640;
		config.frameWidth = 640;
		
		OverloadEngine engine = new OverloadEngine(config);
		engine.run();
	}
}
