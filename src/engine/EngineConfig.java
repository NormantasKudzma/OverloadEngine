package engine;

public class EngineConfig {
	public BaseGame game = null;
	
	public String title = "Game";

	public int frameHeight = 720;
	public int frameWidth = 1280;
	public int targetFps = 60;
	public int targetBpp = 32;
	public int viewportHeight = frameHeight;
	public int viewportWidth = frameWidth;

	public boolean isDebug = false;
	public boolean isFullscreen = true;
	public boolean vSyncEnabled = true;
	
	public EngineConfig(){
		
	}
	
	public boolean validateConfig(){
		boolean isOk = true;
		
		isOk &= game != null;
		
		isOk &= title != null;

		isOk &= frameHeight > 0;
		isOk &= frameWidth > 0;
		isOk &= targetFps > 0;
		isOk &= targetBpp > 0 && targetBpp % 2 == 0;
		isOk &= viewportHeight > 0;
		isOk &= viewportWidth > 0;
				
		return isOk;
	}
}
