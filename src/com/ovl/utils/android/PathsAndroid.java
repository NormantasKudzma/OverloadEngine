package com.ovl.utils.android;

import com.ovl.utils.Paths;

public class PathsAndroid implements Paths {
	public static final String ANDROID_ASSETS = "assets/";
	public static final String RESOURCES = ANDROID_ASSETS + "res/";
	public static final String FONTS = RESOURCES + "fonts/";
	public static final String TEXTURES = RESOURCES + "textures/";
	public static final String CONFIGS = RESOURCES + "configs/";
	public static final String MAPS = RESOURCES + "maps/";
	public static final String ANIMATIONS = TEXTURES + "animations/";
	public static final String SOUNDS = RESOURCES + "sounds/";
	public static final String MUSIC = RESOURCES + "music/";
	public static final String UI = TEXTURES + "ui/";
	public static final String SHADERS = RESOURCES + "shaders/";	

	public static final String ALLOWED_DEVICES = CONFIGS + "AllowedDevices.dat";
	
	public String getPlatformSpecificFolder(){
		return ANDROID_ASSETS;
	}
	
	@Override
	public String getResources() {
		return RESOURCES;
	}

	@Override
	public String getFonts() {
		return FONTS;
	}

	@Override
	public String getTextures() {
		return TEXTURES;
	}

	@Override
	public String getConfigs() {
		return CONFIGS;
	}

	@Override
	public String getMaps() {
		return MAPS;
	}

	@Override
	public String getAnimations() {
		return ANIMATIONS;
	}

	@Override
	public String getSounds() {
		return SOUNDS;
	}

	@Override
	public String getMusic() {
		return MUSIC;
	}

	@Override
	public String getUI() {
		return UI;
	}

	@Override
	public String getShaders() {
		return SHADERS;
	}

	@Override
	public String getAllowedDevices() {
		return ALLOWED_DEVICES;
	}
	
}
