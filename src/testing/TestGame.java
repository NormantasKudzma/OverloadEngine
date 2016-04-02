package testing;

import utils.OverloadRandom;
import utils.Vector2;
import dialogs.BaseDialog;
import dialogs.CheckBox;
import dialogs.Label;
import engine.BaseGame;
import graphics.Color;

public class TestGame extends BaseGame{
	public TestGame() {
		//
	}

	@Override
	public void init() {
		super.init();
		initBenchmark();
		//initDialogs();
		/*Label l = new Label(this, "01");
		l.setPosition(Vector2.one);
		l.setScale(Vector2.one);
		l.setColor(new Color(1.0f, 0.7f, 1.0f));
		addEntity(l);*/
	}
	
	private void initBenchmark(){
		for (int i = 0; i < 40; ++i){
			StringBuilder builder = new StringBuilder();
			for (int j = 0; j < 57; ++j){
				builder.append((char)(OverloadRandom.nextRandom(25) + 97));
			}
			Label label;
			label = new Label(this, builder.toString());
			label.setPosition(1.0f, 0.025f + i*0.05f);
			label.setScale(4.0f, 4.0f);
			label.setColor(new Color(OverloadRandom.nextRandom(255) / 255.0f, 
									OverloadRandom.nextRandom(255) / 255.0f, 
									OverloadRandom.nextRandom(255) / 255.0f));
			addEntity(label);
		}
	}
	
	private void initDialogs(){
		BaseDialog dialog = new BaseDialog(this, "test1");
		
		CheckBox check = new CheckBox(this);
		check.setPosition(Vector2.one);
		dialog.addChild(check);
		
		addDialog(dialog);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
	}
}
