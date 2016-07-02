package testing;

import ui.BaseDialog;
import ui.Button;
import ui.OnClickListener;
import ui.animations.Interpolator;
import ui.animations.QuadInterpolator;
import utils.Vector2;
import engine.BaseGame;

public class TestGame extends BaseGame {
	@Override
	public void init() {
		super.init();
		
		BaseDialog dialog = new BaseDialog(this, "dialog1");
		Button button = new Button(this, "Hello");
		button.setPosition(0.0f, 0.0f);
		OnClickListener listener = new OnClickListener() {		
			@Override
			public void clickFunction(Vector2 pos) {
				System.out.println("Clicked");
			}
		};
		button.setClickListener(listener);
		dialog.addChild(button);
		dialog.setVisible(true);
		addDialog(dialog);
	}
}
