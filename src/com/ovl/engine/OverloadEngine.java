package com.ovl.engine;

import java.util.ArrayList;

import com.ovl.game.BaseGame;
import com.ovl.utils.DebugFrameCounter;
import com.ovl.utils.Vector2;

public abstract class OverloadEngine {
	public enum EnginePlatform {
		PLATFORM_WINDOWS,
		PLATFORM_LINUX,
		PLATFORM_MAC,
		PLATFORM_PC_OTHER,
		PLATFORM_ANDROID,
		PLATFORM_UNKNOWN
	}
	
	protected enum Settings {
		framewidth {
			@Override
			public String get(){
				return "" + engine.frameWidth;
			}
			
			@Override
			public void set(String value) {
				try {
					engine.frameWidth = Integer.parseInt(value);
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		},
		frameheight {
			@Override
			public String get() {
				return "" + engine.frameHeight;
			}
			
			@Override
			public void set(String value) {
				try {
					engine.frameHeight = Integer.parseInt(value);
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		},
		fullscreen {
			@Override
			public String get() {
				return "" + engine.isFullScreen;
			}
			
			@Override
			public void set(String value) {
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
			public String get() {
				return "" + engine.isVsyncEnabled;
			}
			
			@Override
			public void set(String value) {
				try {
					engine.isVsyncEnabled = Boolean.parseBoolean(value);
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		},
		referencewidth {
			@Override
			public String get() {
				return "" + engine.referenceWidth;
			}
			
			@Override
			public void set(String value) {
				try {
					engine.referenceWidth = Integer.parseInt(value);
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		},
		referenceheight {
			@Override
			public String get() {
				return "" + engine.referenceHeight;
			}
			
			@Override
			public void set(String value) {
				try {
					engine.referenceHeight = Integer.parseInt(value);
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		};
		
		public static OverloadEngine engine;
		
		public abstract String get();
		public abstract void set(String value);
		
		public static void parseConfig(ArrayList<String> values){
			if (values == null || values.isEmpty()){
				return;
			}
			
			String params[] = null;
			for (String line : values){
				// Ignore emtpy lines and comments
				if (line.startsWith("#") || line.length() <= 0){
					continue;
				}
				
				params = line.split("=");
				if (params == null || params.length < 2){
					continue;
				}
				
				try {
					Settings.valueOf(params[0]).set(params[1]);
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	protected static OverloadEngine INSTANCE = null;
	
	public Renderer renderer;
	public int frameWidth, frameHeight;
	public int referenceWidth, referenceHeight;
	public float referenceScale;
	public float aspectRatio;
	public boolean isFullScreen = true;

	protected EnginePlatform platform;
	protected String platformAssetsRoot;
	
	protected float deltaTime;
	protected DebugFrameCounter frameCounter;
	protected EngineConfig config;
	protected BaseGame game;
	protected boolean isVsyncEnabled = true;
	protected boolean isCloseRequested = false;

	public OverloadEngine(EngineConfig config){
		this.config = config;
		if (config == null || !config.validateConfig()){
			System.err.println("Invalid configuration. Check settings first.");
			return;
		}
		
		INSTANCE = this;
		Settings.engine = this;
		platform = EnginePlatform.PLATFORM_UNKNOWN;
	}
	
	protected abstract void destroy();
	
	public static OverloadEngine getInstance(){
		return INSTANCE;
	}
	
	public BaseGame getGame(){
		return game;
	}
	
	public EnginePlatform getPlatform(){
		return platform;
	}
	
	public String getPlatformAssetsRoot(){
		return platformAssetsRoot;
	}
	
	protected abstract void init();

	protected void initGL(){
		renderer.init();
	}
	
	protected abstract void loop();

	public void requestClose(){
		isCloseRequested = true;
	}
	
	public void run() {
		init();
		loop();
		destroy();
	}
}
