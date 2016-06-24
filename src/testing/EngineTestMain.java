package testing;

import engine.EngineConfig;
import engine.OverloadEngine;

public class EngineTestMain {
	public static void main(String args[]){
		EngineConfig config = new EngineConfig();
		config.game = new TestGame();
		config.isDebug = true;
		config.isFullscreen = false;
		config.vSyncEnabled = true;
		
		config.frameWidth = 640;
		config.frameHeight = 480;
		
		OverloadEngine engine = new OverloadEngine(config);
		engine.run();
	}
}
