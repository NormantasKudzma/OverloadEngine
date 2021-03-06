package com.ovl.audio;

import java.util.HashMap;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

import com.ovl.game.Updatable;

public abstract class AudioManager<T> implements Updatable {
	public enum EAudioType {
		OGG("OGG"),
		MP3("MP3"),
		WAV("WAV"),
		INVALID(null);
		
		private String name;

		private EAudioType(String name){
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
	
		public static EAudioType typeFromName(String name){
			for (EAudioType t : values()){
				if (t.getName().equalsIgnoreCase(name)){
					return t;
				}
			}
			return INVALID;
		}
	}
	
	protected HashMap<T, Audio> audioList = new HashMap<T, Audio>(16);

	public AudioManager() {
		
	}

	public void destroy() {
		stopAll();
	}
	
	public Audio loadAudio(String path, EAudioType type, T key){
		try {
			Audio audio = AudioLoader.getAudio(type.getName(), ResourceLoader.getResourceAsStream(path));
			audioList.put(key, audio);
			return audio;
		}
		catch (Exception e) {
			System.out.println("Error loading " + path);
			e.printStackTrace();
		}
		return null;
	}
	
	public abstract void pause();
	
	public abstract void play();
	
	public abstract void play(T audio, boolean loop);
	
	public abstract void resume();
	
	public abstract void stop();
	
	public void stop(T sound){
		Audio audio = audioList.get(sound);
		if (audio.isPlaying()){
			audio.stop();
		}
	}
	
	public void stopAll(){
		for (Audio i : audioList.values()){
			i.stop();
		}
	}
}
