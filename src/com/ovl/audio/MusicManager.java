package com.ovl.audio;

import java.util.ArrayList;
import java.util.Collections;

import org.newdawn.slick.openal.Audio;

public class MusicManager<T> extends AudioManager<T> {
	private Audio currentMusic = null;
	private float position = 0.0f;
	private boolean isLooped = false;
	private boolean isAllLooped = false;
	private boolean isShuffled = false;
	private ArrayList<T> playlist = null;
	private int currentIndex = 0;
	
	public MusicManager(){
		
	}

	public void loadAll(ArrayList<String> paths, ArrayList<T> keys){
		String type = null;
		String path = null;
		for (int i = 0; i < paths.size(); ++i){
			path = paths.get(i);
			type = path.substring(path.lastIndexOf(".") + 1);
			loadAudio(path, EAudioType.typeFromName(type), keys.get(i));
		}
	}
	
	public void play(T audio){
		play(audio, isLooped);
	}
	
	@Override
	public void play(T audio, boolean loop){
		Audio music = audioList.get(audio);
		if (music != null){
			if (currentMusic != null && currentMusic.isPlaying()){
				stop();
			}
			currentMusic = music;

			currentMusic.playAsMusic(1.0f, 1.0f, loop);
		}
	}

	@Override
	public void pause(){
		if (currentMusic != null){
			position = currentMusic.getPosition();
			currentMusic.stop();
		}
	}
	
	@Override
	public void play(){
		if (currentMusic != null){
			if (!currentMusic.isPlaying()){
				resume();
			}
		}
	}

	public void playAll(ArrayList<T> keys, boolean repeat, boolean shuffle){
		isAllLooped = repeat;
		isLooped = false;
		isShuffled = shuffle;
		if (shuffle){
			Collections.shuffle(keys);
		}
		playlist = keys;
		currentIndex = 0;
		play(keys.get(currentIndex));
	}
	
	@Override
	public void resume() {
		if (currentMusic != null && !currentMusic.isPlaying()){
			currentMusic.setPosition(position);
			currentMusic.playAsMusic(1.0f, 1.0f, isLooped);
		}
	}

	@Override
	public void stop(){
		position = 0.0f;
		isLooped = false;
		if (currentMusic != null){
			currentMusic.stop();
			currentMusic = null;
		}
		isAllLooped = false;
		isShuffled = false;
		playlist = null;
		currentIndex = 0;
	}
	
	@Override
	public void stopAll() {
		super.stopAll();
		stop();
	}

	@Override
	public void update(float deltaTime){
		if (currentMusic != null 
				&& !currentMusic.isPlaying() 
				&& playlist != null){
			++currentIndex;
			if (currentIndex < playlist.size()){
				play(playlist.get(currentIndex));
			}
			else {
				if (isAllLooped){
					playAll(playlist, isAllLooped, isShuffled);
				}
				else {
					stop();
				}
			}
		}
	}
}
