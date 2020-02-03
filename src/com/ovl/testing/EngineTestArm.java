package com.ovl.testing;

import com.jogamp.opengl.GLProfile;
import com.ovl.engine.EngineConfig;
import com.ovl.engine.arm.OverloadEngineArm;
import com.ovl.utils.Paths;

public class EngineTestArm {
	public static void main(String args[]){
		EngineConfig config = new EngineConfig();
		config.game = new TestGame();
		//config.configPath = Paths.getResources() + "test/config.cfg";
		config.isDebug = true;
		
		OverloadEngineArm engine = new OverloadEngineArm(config);
		engine.frameWidth = 960;
		engine.frameHeight = 540;
		engine.aspectRatio = engine.frameWidth / engine.frameHeight;
		engine.isFullScreen = false;
		engine.run();
	}
}
