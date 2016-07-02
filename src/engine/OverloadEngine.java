package engine;

import graphics.PhysicsDebugDraw;
import graphics.Renderer;

import java.io.File;
import java.util.ArrayList;

import org.lwjgl.LWJGLUtil;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import utils.ConfigManager;
import utils.DebugFrameCounter;
import controls.ControllerManager;

public class OverloadEngine {
	private enum Settings {
		frameWidth {
			String get(){
				return "" + OverloadEngine.frameWidth;
			}
			
			void set(String value) {
				try {
					OverloadEngine.frameWidth = Integer.parseInt(value);
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		},
		frameHeight {
			String get() {
				return "" + OverloadEngine.frameHeight;
			}
			
			void set(String value) {
				try {
					OverloadEngine.frameHeight = Integer.parseInt(value);
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		},
		fullScreen {
			@Override
			String get() {
				return "" + engine.isFullScreen;
			}
			
			@Override
			void set(String value) {
				try {
					engine.isFullScreen = Boolean.parseBoolean(value);
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		},
		vsync {
			@Override
			String get() {
				return "" + engine.isVsyncEnabled;
			}
			
			@Override
			void set(String value) {
				try {
					engine.isVsyncEnabled = Boolean.parseBoolean(value);
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		};

		static OverloadEngine engine;
		
		abstract String get();
		abstract void set(String value);
		
		static void parseConfig(ArrayList<String> values){
			if (values == null || values.isEmpty()){
				return;
			}
			
			String params[] = null;
			for (String line : values){
				// Ignore emtpy lines and comments
				if (line.startsWith("#") || line.isEmpty()){
					continue;
				}
				
				params = line.split("=");
				if (params == null || params.length < 2){
					continue;
				}
				
				Settings setting = Settings.valueOf(params[0]);
				if (setting == null){
					continue;
				}
				setting.set(params[1]);
			}
		}
	}
	
	public static float aspectRatio;
	public static int frameHeight = 720;
	public static int frameWidth = 1280;
	public static final int bpp = 32;
	private static boolean isCloseRequested = false;

	private float deltaTime;
	private long t0, t1; // Frame start/end time
	private DebugFrameCounter frameCounter;
	private EngineConfig config;
	private BaseGame game;
	private Renderer renderer;
	private boolean isFullScreen = true;
	private boolean isVsyncEnabled = true;

	public OverloadEngine(EngineConfig config){
		this.config = config;
		if (config == null || !config.validateConfig()){
			System.err.println("Invalid configuration. Check settings first.");
			return;
		}
	}
	
	private void destroy() {
		game.destroy();
		ControllerManager.getInstance().destroyManager();
		Display.destroy();
		AL.destroy();
	}

	private void init() {
		File nativesFolder = null;
		switch (LWJGLUtil.getPlatform()) {
			case LWJGLUtil.PLATFORM_WINDOWS: {
				nativesFolder = new File("./native/windows/");
				break;
			}
			case LWJGLUtil.PLATFORM_LINUX: {
				nativesFolder = new File("./native/linux/");
				break;
			}
			case LWJGLUtil.PLATFORM_MACOSX: {
				nativesFolder = new File("./native/macosx/");
				break;
			}
		}
		System.setProperty("org.lwjgl.librarypath", nativesFolder.getAbsolutePath());
		
		Settings.engine = this;
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
					&& d.getWidth() == OverloadEngine.frameWidth 
					&& d.getHeight() == OverloadEngine.frameHeight) {
					bestMatch = d;
					break;
				}
			}
			if (bestMatch == null) {
				System.out.println("No display mode with current parameters was found. Creating default one.");
				bestMatch = new DisplayMode(OverloadEngine.frameWidth, OverloadEngine.frameHeight);
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
			renderer = Renderer.getInstance();
		}

		// Create and initialize game
		game = config.game;
		game.onGameConfigurationLoaded(ConfigManager.gameConfiguration);
		game.init();

		if (config.isDebug) {
			frameCounter = new DebugFrameCounter();
		}
	}

	private void initGL(){
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glClearColor(0.4f, 0.4f, 0.4f, 1.0f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glTranslatef(-1.0f, -1.0f, 0.0f);
		GL11.glViewport(0, 0, OverloadEngine.frameWidth, OverloadEngine.frameHeight);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
	}
	
	private void loop() {
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

	public void run() {
		init();
		loop();
		destroy();
	}
	
	public static void requestClose(){
		isCloseRequested = true;
	}
}
