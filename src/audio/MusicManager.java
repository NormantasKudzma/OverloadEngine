package audio;

import org.newdawn.slick.openal.Audio;

public class MusicManager<T> extends AudioManager<T> {
	private Audio currentMusic = null;
	private float position = 0.0f;
	private boolean isLooped = false;
	
	public MusicManager(){
		
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
	}
	
	@Override
	public void stopAll() {
		super.stopAll();
		stop();
	}

	@Override
	public void update(float deltaTime){
		// TODO Auto-generated method stub
		
	}
}
