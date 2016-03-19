package testing;

import org.lwjgl.util.Color;

import utils.OverloadRandom;
import utils.Vector2;
import dialogs.Label;
import engine.BaseGame;
import engine.Entity;

public class TestGame extends BaseGame{
	private static final int LIM = 254;
	private int d = 5;
	
	public TestGame() {
		//
	}

	@Override
	public void init() {
		super.init();
		
		for (int i = 0; i < 15; ++i){
			StringBuilder builder = new StringBuilder();
			for (int j = 0; j < 57; ++j){
				builder.append((char)(OverloadRandom.nextRandom(25) + 97));
			}
			Label label;
			label = new Label(this, builder.toString());
			label.setPosition(1.0f, 0.025f + i * 0.05f);
			label.setScale(4.0f, 4.0f);
			label.setColor(new Color(OverloadRandom.nextRandom(255), OverloadRandom.nextRandom(255), OverloadRandom.nextRandom(255), 255));
			addEntity(label);
		}
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		for (Entity<?> e : entityList){
			Color c = e.getColor();
			int r = c.getRed();
			int g = c.getGreen();
			int b = c.getBlue();
			r += d;
			if (r >= LIM){
				r %= LIM;
				g += d;
				if (g >= LIM){
					g %= LIM;
					b += d;
					if (b >= LIM){
						b %= LIM;
					}
				}
			}
			c.set(r, g, b);
		}
	}
}
