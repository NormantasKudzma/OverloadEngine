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
		
		Interpolator quad = new QuadInterpolator();
		Vector2 out = new Vector2();
		System.out.println("0 --- > 1");
		for (int i = 0; i <= 10; ++i){
			quad.interpolate(Vector2.zero, Vector2.one, out, i * 0.1f);
			System.out.println(out);
		}
		System.out.println("------------");
		System.out.println("4 --- > 9");
		Vector2 four = new Vector2(4.0f, 4.0f);
		Vector2 nine = new Vector2(9.0f, 9.0f);
		for (int i = 0; i <= 10; ++i){
			quad.interpolate(four, nine, out, i * 0.1f);
			System.out.println(out);
		}
	}
}
