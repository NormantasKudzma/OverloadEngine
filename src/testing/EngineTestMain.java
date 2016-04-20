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
		
		OverloadEngine engine = new OverloadEngine(config);
		engine.run();
	}
}
