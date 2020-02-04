package com.ovl.utils;

import com.ovl.engine.OverloadEngine;

public final class Paths {
	public static final String ROOT;
	public static final String RESOURCES;
	public static final String FONTS;
	public static final String TEXTURES;
	public static final String CONFIGS;
	public static final String MAPS;
	public static final String ANIMATIONS;
	public static final String SOUNDS;
	public static final String MUSIC;
	public static final String UI;
	public static final String SHADERS;
	public static final String SCRIPTS;
	public static final String ALLOWED_DEVICES;
	
	static {
		ROOT = OverloadEngine.getInstance().getPlatformAssetsRoot();
		RESOURCES = ROOT + "res/";
		FONTS = RESOURCES + "fonts/";
		TEXTURES = RESOURCES + "textures/";
		CONFIGS = RESOURCES + "configs/";
		MAPS = RESOURCES + "maps/";
		ANIMATIONS = TEXTURES + "animations/";
		SOUNDS = RESOURCES + "sounds/";
		MUSIC = RESOURCES + "music/";
		UI = TEXTURES + "ui/";
		SHADERS = RESOURCES + "shaders/";
		SCRIPTS = RESOURCES + "scripts/";
		ALLOWED_DEVICES = CONFIGS + "AllowedDevices.dat";
	}
	
	/**
	 * @return platform specific resources root folder (ie. <i>assets/</i> on android)
	 */
	public static String getPlatformAssetsRoot(){
		return ROOT;
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
