package com.ovl.testing;

import com.jogamp.opengl.GLProfile;
import com.ovl.engine.EngineConfig;
import com.ovl.engine.arm.OverloadEngineArm;

public class EngineTestArm {
	public static void main(String args[]){
		EngineConfig config = new EngineConfig();
		config.game = new TestGame();
		config.configPath = "C:\\Users\\nkudzma\\Desktop\\workspace\\OverloadEngine\\resources\\res\\config.cfg";
		config.isDebug = true;
		
		OverloadEngineArm engine = new OverloadEngineArm(config);
		/*engine.frameWidth = 960;
		engine.frameHeight = 540;
		engine.aspectRatio = engine.frameWidth / engine.frameHeight;
		engine.isFullScreen = false;*/
		engine.run();
	}
}
