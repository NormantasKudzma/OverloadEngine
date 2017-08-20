package com.ovl.engine.pc;

import java.io.File;

import org.lwjgl.LWJGLUtil;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.ovl.controls.ControllerManager;
import com.ovl.controls.pc.KeyboardController;
import com.ovl.controls.pc.MouseController;
import com.ovl.engine.EngineConfig;
import com.ovl.engine.OverloadEngine;
import com.ovl.utils.ConfigManager;
import com.ovl.utils.DebugFrameCounter;
import com.ovl.utils.Vector2;

public class OverloadEnginePc extends OverloadEngine {
	public OverloadEnginePc(EngineConfig config){
		super(config);
	}
	
	@Override
	protected void destroy() {
		if (game != null){
			game.destroy();
		}
		ControllerManager.getInstance().destroyManager();
		Display.destroy();
		AL.destroy();
	}

	@Override
	protected void init() {
		File nativesFolder = null;
		switch (LWJGLUtil.getPlatform()) {
			case LWJGLUtil.PLATFORM_WINDOWS: {
				nativesFolder = new File("./native/windows/");
				platform = EnginePlatform.PLATFORM_WINDOWS;
				break;
			}
			case LWJGLUtil.PLATFORM_LINUX: {
				nativesFolder = new File("./native/linux/");
				platform = EnginePlatform.PLATFORM_LINUX;
				break;
			}
			case LWJGLUtil.PLATFORM_MACOSX: {
				nativesFolder = new File("./native/macosx/");
				platform = EnginePlatform.PLATFORM_MAC;
				break;
			}
			default:{
				platform = EnginePlatform.PLATFORM_PC_OTHER;
				break;
			}
		}
		System.setProperty("org.lwjgl.librarypath", nativesFolder.getAbsolutePath());
		ConfigManager.gameConfiguration = ConfigManager.loadFileLines(config.configPath);
		Settings.parseConfig(ConfigManager.gameConfiguration);
		platformAssetsRoot = "";
		
		aspectRatio = 1.0f * frameWidth / frameHeight;
		
		if (referenceHeight <= 0){
			referenceHeight = frameHeight;
		}
		if (referenceWidth <= 0){
			referenceWidth = frameWidth;
		}
		referenceScale = Math.min(1.0f * frameWidth / referenceWidth, 1.0f * frameHeight / referenceHeight);
		
		try {
			Display.setTitle(config.title);
			Display.setResizable(false);
			Display.setFullscreen(isFullScreen);
			DisplayMode displayModes[] = Display.getAvailableDisplayModes();
			DisplayMode bestMatch = null;
			for (DisplayMode d : displayModes) {
				if (d.getFrequency() == config.targetFps
					&& d.getBitsPerPixel() == 32
					&& d.getWidth() == frameWidth 
					&& d.getHeight() == frameHeight) {
					bestMatch = d;
					break;
				}
			}
			if (bestMatch == null) {
				System.out.println("No display mode with current parameters was found. Creating default one.");
				bestMatch = new DisplayMode(frameWidth, frameHeight);
			}
			Display.setDisplayMode(bestMatch);
			Display.setVSyncEnabled(isVsyncEnabled);
			Display.create();
			Display.setLocation(Display.getX(), 0);
		}
		catch (Exception e) {
			System.err.println("Could not create display.");
			e.printStackTrace();
			isCloseRequested = true;
			return;
		}
		
		if (renderer == null) {
			renderer = new RendererPc();
			initGL();
		}

		ControllerManager controllerManager = ControllerManager.getInstance();
		controllerManager.addController(new KeyboardController(0));
		controllerManager.addController(new MouseController(0));
		
		// Create and initialize game
		game = config.game;
		game.onGameConfigurationLoaded(ConfigManager.gameConfiguration);
		game.init();

		if (config.isDebug) {
			frameCounter = new DebugFrameCounter();
			game.addObject(frameCounter);
		}
	}
	
	@Override
	protected void loop() {
		long t0, t1; // Frame start/end time
		t0 = t1 = System.nanoTime();
		while (!isCloseRequested && !Display.isCloseRequested()) {
			t0 = System.nanoTime();
			deltaTime = (t0 - t1) * 0.000000001f;
			t1 = t0;

			// Poll controllers for input
			ControllerManager.getInstance().pollControllers();

			// Update game logic
			game.update(deltaTime);

			// Render game and swap buffers
			renderer.preRender();
			game.render();

			// Render debug things
			/*if (config.isDebug) {
				PhysicsDebugDraw.render();

				frameCounter.update(deltaTime);
				frameCounter.render();
			}*/
			renderer.postRender();

			//
			Display.update();
			//Display.sync(config.targetFps);
		}
	}
}
