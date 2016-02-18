package utils;

public final class Paths {
	public static final String RESOURCES = "res/";
	public static final String FONTS = RESOURCES + "fonts/";
	public static final String TEXTURES = RESOURCES + "textures/";
	public static final String CONFIGS = RESOURCES + "configs/";
	public static final String MAPS = RESOURCES + "maps/";
	public static final String ANIMATIONS = TEXTURES + "animations/";
	public static final String SOUNDS = RESOURCES + "sounds/";
	public static final String MUSIC = RESOURCES + "music/";
	public static final String UI = TEXTURES + "ui/";

	public static final String ALLOWED_DEVICES = CONFIGS + "AllowedDevices.dat";
	public static final String DEFAULT_FONT_IMG = FONTS + "simplefont.png";
	public static final String DEFAULT_FONT_JSON = FONTS + "simplefont.json";

	public static final String USER_CONFIG_DIR = (System.getProperty("user.dir") + "\\usercfg\\").substring(3).replace("\\", "/");
	public static final String USER_CONFIGS = USER_CONFIG_DIR + "Keybinds.json";
}
