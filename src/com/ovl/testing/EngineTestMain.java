package com.ovl.testing;

import com.ovl.engine.EngineConfig;
import com.ovl.engine.pc.OverloadEnginePc;

public class EngineTestMain {
	public static void main(String args[]){
		EngineConfig config = new EngineConfig();
		config.game = new TestGame();
		config.isDebug = false;
		
		OverloadEnginePc engine = new OverloadEnginePc(config);
		engine.frameWidth = 1280;
		engine.frameHeight = 720;
		engine.aspectRatio = 1280.0f / 720.0f;
		engine.isFullScreen = false;
		engine.run();
	}
}
