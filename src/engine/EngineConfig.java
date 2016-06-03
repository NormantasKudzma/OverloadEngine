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
		if (game == null){
			System.err.println("No game was specified in engine configuration.");
			return false;
		}
		
		if (title == null){
			System.err.println("Window title cannot be null.");
			return false;
		}

		if (frameWidth <= 0 || frameHeight <= 0){
			System.err.println("Window size cannot be smaller than 1x1.");
			return false;
		}

		if (targetFps <= 0){
			System.err.println("Target fps cannot be less than 1.");
			return false;
		}

		if (targetBpp <= 1){
			System.err.println("Target bpp cannot be less than 1.");
			return false;
		}

		if (viewportHeight <= 0 || viewportWidth <= 0){
			System.err.println("Viewport height and width must be bigger than 1.");
			return false;
		}
				
		return true;
	}
}
