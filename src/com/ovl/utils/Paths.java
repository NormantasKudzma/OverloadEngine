package com.ovl.utils;

import com.ovl.engine.OverloadEngine;

public final class Paths {
	private static final String PLATFORM_ROOT;
	private static final String RESOURCES;
	private static final String FONTS;
	private static final String TEXTURES;
	private static final String CONFIGS;
	private static final String MAPS;
	private static final String ANIMATIONS;
	private static final String SOUNDS;
	private static final String MUSIC;
	private static final String UI;
	private static final String SHADERS;
	private static final String ALLOWED_DEVICES;
	
	static {
		PLATFORM_ROOT = OverloadEngine.getInstance().getPlatformAssetsRoot();
		RESOURCES = PLATFORM_ROOT + "res/";
		FONTS = RESOURCES + "fonts/";
		TEXTURES = RESOURCES + "textures/";
		CONFIGS = RESOURCES + "configs/";
		MAPS = RESOURCES + "maps/";
		ANIMATIONS = TEXTURES + "animations/";
		SOUNDS = RESOURCES + "sounds/";
		MUSIC = RESOURCES + "music/";
		UI = TEXTURES + "ui/";
		SHADERS = RESOURCES + "shaders/";
		ALLOWED_DEVICES = CONFIGS + "AllowedDevices.dat";
	}
	
	/**
	 * @return platform specific resources root folder (ie. <i>assets/</i> on android)
	 */
	public static String getPlatformAssetsRoot(){
		return PLATFORM_ROOT;
	}
	
	public static String getResources(){
		return RESOURCES;
	}
	
	public static String getFonts(){
		return FONTS;
	}
	
	public static String getTextures(){
		return TEXTURES;
	}
	
	public static String getConfigs(){
		return CONFIGS;
	}
	
	public static String getMaps(){
		return MAPS;
	}
	
	public static String getAnimations(){
		return ANIMATIONS;
	}
	
	public static String getSounds(){
		return SOUNDS;
	}
	
	public static String getMusic(){
		return MUSIC;
	}
	
	public static String getUI(){
		return UI;
	}
	
	public static String getShaders(){
		return SHADERS;
	}
	
	public static String getAllowedDevices(){
		return ALLOWED_DEVICES;
	}
}
