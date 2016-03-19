package audio;

import org.newdawn.slick.openal.Audio;

public class SoundManager<T> extends AudioManager<T> {	
	public SoundManager(){
		
	}
	
	@Override
	public void play(T audio, boolean loop){
		Audio sound = audioList.get(audio);
		if (sound != null){
			sound.playAsSoundEffect(1.0f, 1.0f, loop);
		}
	}

	@Override
	public void update(float deltaTime){
		// TODO Auto-generated method stub
		
	}

	@Override
	public void play(){
		//
	}

	@Override
	public void stop(){
		stopAll();
	}

	@Override
	public void pause() {
		stopAll();
	}

	@Override
	public void resume() {
		//
	}
}
