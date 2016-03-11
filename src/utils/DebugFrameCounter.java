package utils;

import engine.IUpdatable;
import graphics.IRenderable;
import graphics.SimpleFont;

import java.util.Locale;


public class DebugFrameCounter implements IUpdatable, IRenderable{
	private static final Vector2 fpsTextPosition = new Vector2(0.2f, 0.15f);
	private static final Vector2 fpsTextScale = new Vector2(2.0f, 2.0f);
	
	private boolean isFirstFrame = true;
	private float debugTime = 0;
	private float fpsTextUpdate = 0.0f;
	private float numFrames = 0;
	private SimpleFont fpsText;

	public DebugFrameCounter(){
		fpsText = new SimpleFont("00.00 fps");
	}
	
	@Override
	public void render() {
		render(fpsTextPosition, 0.0f, fpsTextScale);
	}
	
	@Override
	public void render(Vector2 position, float rotation, Vector2 scale) {
		fpsText.render(position, rotation, scale);
	}
	
	@Override
	public void update(float deltaTime) {
		if (!isFirstFrame){
			numFrames++;
			debugTime += deltaTime;
			fpsTextUpdate += deltaTime;
			
			// Only set text every once in a while (text is expensive)
			if (fpsTextUpdate > 0.25f){
				fpsText.setText(String.format(Locale.ENGLISH, "%2.2f fps", (numFrames / debugTime)));
				fpsTextUpdate = 0.0f;
				numFrames = 0.0f;
				debugTime = 0.0f;
			}
		}
		
		if (isFirstFrame){
			isFirstFrame = false;
		}
	}	
}
