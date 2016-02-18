package audio;

import java.util.HashMap;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

import utils.Paths;


public class AudioManager {
	public enum SoundType {
		BOMB_EXPLODE(Paths.SOUNDS + "bombexplosion.ogg"), BOMB_PLACED(
				Paths.SOUNDS + "bombplaced.ogg"), FIRST_BLOOD(
				Paths.SOUNDS + "firstblood.ogg"), DOUBLE_KILL(
				Paths.SOUNDS + "doublekill.ogg"), TRIPLE_KILL(
				Paths.SOUNDS + "triplekill.ogg"), MONSTER_KILL(
				Paths.SOUNDS + "monsterkill.ogg"), POWERUP_PICKUP(
				Paths.SOUNDS + "poweruppickup.ogg"), INVALID(null);

		private String filename;

		private SoundType(String f) {
			filename = f;
		}

		public String getFilename() {
			return filename;
		}

		public static SoundType fromString(String str) {
			for (int i = 0; i < values().length; i++) {
				if (values()[i].getFilename().equals(str)) {
					return values()[i];
				}
			}
			return INVALID;
		}
	}

	private static HashMap<String, Audio> soundList = new HashMap<String, Audio>(SoundType.values().length + 10);
	private static Audio currentMusic = null;

	private AudioManager() {
	}

	static {
		// Loading all initial sounds and putting them into sound list
		for (SoundType stype : SoundType.values()) {
			if (stype == SoundType.INVALID) {
				continue;
			}
			try {
				Audio audio = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream(stype.getFilename()));
				soundList.put(stype.getFilename(), audio);
			}
			catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}

	public static final void destroy() {
		stopMusic();
	}

	public static void playMusic(String path) {
		if (!path.contains(Paths.MUSIC)) {
			path = Paths.MUSIC + path;
		}
		try {
			currentMusic = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream(path));
			currentMusic.playAsMusic(1.0f, 1.0f, false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void playSound(SoundType sound) {
		playSound(sound.getFilename());
	}

	public static void playSound(String path) {
		Audio audio = soundList.get(path);
		if (audio == null) {
			try {
				audio = soundList.put(path, AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream(path)));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		audio.playAsSoundEffect(1.0f, 1.0f, false);
	}

	public static void stopMusic() {
		if (currentMusic != null) {
			currentMusic.stop();
			currentMusic = null;
		}
	}
}
