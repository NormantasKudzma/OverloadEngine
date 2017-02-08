package com.ovl.testing;

import com.ovl.engine.EngineConfig;
import com.ovl.engine.pc.OverloadEnginePc;


public class EngineTestMain {
	public static void main(String args[]){
		EngineConfig config = new EngineConfig();
		config.game = new TestGame();
		config.isDebug = true;
		
		OverloadEnginePc engine = new OverloadEnginePc(config);
		engine.frameWidth = 960;
		engine.frameHeight = 540;
		engine.aspectRatio = engine.frameWidth / engine.frameHeight;
		engine.isFullScreen = false;
		engine.run();
	}
}
