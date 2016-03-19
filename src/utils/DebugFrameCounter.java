package utils;

import graphics.SimpleFont;

import java.util.Locale;

import org.lwjgl.util.Color;

import physics.Transform;
import physics.PhysicsBody.EBodyType;
import dialogs.Label;


public class DebugFrameCounter extends Label{
	private static final Vector2 fpsTextPosition = new Vector2(0.2f, 0.15f);
	private static final Vector2 fpsTextScale = new Vector2(2.0f, 2.0f);
	
	private boolean isFirstFrame = true;
	private float debugTime = 0;
	private float fpsTextUpdate = 0.0f;
	private float numFrames = 0;

	public DebugFrameCounter(){
		super(null, "00.00 fps");
	}
	
	@Override
	public void initEntity(EBodyType type) {
		super.initEntity(type);
		setPosition(fpsTextPosition);
		setScale(fpsTextScale);
	}
	
	@Override
	public void update(float deltaTime) {
		if (!isFirstFrame){
			numFrames++;
			debugTime += deltaTime;
			fpsTextUpdate += deltaTime;
			
			// Only set text every once in a while (text is expensive)
			if (fpsTextUpdate > 0.25f){
				setText(String.format(Locale.ENGLISH, "%2.2f fps", (numFrames / debugTime)));
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
