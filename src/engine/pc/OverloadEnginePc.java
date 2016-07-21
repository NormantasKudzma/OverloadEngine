package engine.pc;

import java.io.File;

import org.lwjgl.LWJGLUtil;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import utils.ConfigManager;
import utils.DebugFrameCounter;
import utils.pc.PhysicsDebugDraw;
import controls.ControllerManager;
import controls.pc.KeyboardController;
import controls.pc.MouseController;
import engine.EngineConfig;
import engine.OverloadEngine;

public class OverloadEnginePc extends OverloadEngine {	
	public static float aspectRatio;
	public static int frameHeight = 720;
	public static int frameWidth = 1280;
	public static final int bpp = 32;
	private static boolean isCloseRequested = false;

	public OverloadEnginePc(EngineConfig config){
		super(config);
	}
	
	protected void destroy() {
		game.destroy();
		ControllerManager.getInstance().destroyManager();
		Display.destroy();
		AL.destroy();
	}

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
		}
		System.setProperty("org.lwjgl.librarypath", nativesFolder.getAbsolutePath());
		ConfigManager.gameConfiguration = ConfigManager.loadFileLines(config.configPath);
		Settings.parseConfig(ConfigManager.gameConfiguration);
		
		aspectRatio = (float) (1.0f * frameWidth) / frameHeight;
		
		try {
			Display.setTitle(config.title);
			Display.setResizable(false);
			Display.setFullscreen(isFullScreen);
			DisplayMode displayModes[] = Display.getAvailableDisplayModes();
			DisplayMode bestMatch = null;
			for (DisplayMode d : displayModes) {
				if (d.getFrequency() == config.targetFps
					&& d.getBitsPerPixel() == bpp
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
			return;
		}

		initGL();
		
		if (renderer == null) {
			renderer = new RendererPc();
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
		}
	}
	
	protected void loop() {
		t0 = t1 = System.nanoTime();
		while (!Display.isCloseRequested() && !isCloseRequested) {
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
			if (config.isDebug) {
				PhysicsDebugDraw.render();

				frameCounter.update(deltaTime);
				frameCounter.render();
			}
			renderer.postRender();

			//
			Display.update();
			Display.sync(config.targetFps);
		}
	}
	
	public static void requestClose(){
		isCloseRequested = true;
	}
}
