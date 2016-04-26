package testing;

import physics.PhysicsBody.EBodyType;
import ui.BaseDialog;
import ui.Button;
import ui.CheckBox;
import ui.Label;
import utils.OverloadRandom;
import utils.Paths;
import utils.Vector2;
import engine.BaseGame;
import graphics.Color;
import graphics.SimpleFont;
import graphics.Sprite;

public class TestGame extends BaseGame {
	
	public TestGame() {
		//
	}

	@Override
	public void init() {
		super.init();
		
		RotObject o = new RotObject(this);
		o.initEntity(EBodyType.NON_INTERACTIVE);
		o.setPosition(Vector2.one);
		o.setScale(new Vector2(2.0f, 2.0f));
		o.setSprite(new Sprite(Paths.UI + "checkbox_checked_hover.png"));
		addEntity(o);
		
		//initBenchmark();
		//initDialogs();
		/*Label l = new Label(this, "01");
		l.setPosition(Vector2.one);
		l.setScale(Vector2.one);
		l.setColor(new Color(1.0f, 0.7f, 1.0f));
		addEntity(l);*/
		//setUpController();
	}
	
	private void setUpController() {
		Button b = new Button();
		b.setScale(new Vector2(0.005f, 1.0f));
		b.setPosition(Vector2.one);
		addEntity(b);
		
		Button bb = new Button();
		bb.setScale(new Vector2(1.0f, 0.008f));
		bb.setPosition(Vector2.one);
		addEntity(bb);
		
		/*final Label test = new Label(this, "4444");
		test.setPosition(new Vector2(1.0f, 1.5f));
		addEntity(test);
		*/
		final Label l = new Label(this, "labas");
		l.setPosition(Vector2.one);
		l.setScale(new Vector2(1.0f, 1.0f));
		addEntity(l);
		
		/*AbstractController controller = ControllerManager.getInstance().getController(EController.USBCONTROLLER, 0);
		if (controller != null){
			ControllerEventListener listener = new ControllerEventListener(){
				@Override
				public void handleEvent(long eventArg, Vector2 pos, int... params) {
					l.setText("" + eventArg);
					System.out.println(eventArg);
				}
			};
			
			controller.setUnmaskedCallback(listener);
			controller.startController();
		}*/
		
		/*AbstractController keyboard = ControllerManager.getInstance().getController(EController.LWJGLKEYBOARDCONTROLLER);
		if (keyboard != null){
			ControllerEventListener left = new ControllerEventListener() {				
				@Override
				public void handleEvent(long eventArg, Vector2 pos, int... params) {
					test.setPosition(test.getPosition().add(-0.001f, 0.0f));
				}
			};
			keyboard.addKeybind(203, left);
			
			ControllerEventListener right = new ControllerEventListener() {				
				@Override
				public void handleEvent(long eventArg, Vector2 pos, int... params) {
					test.setPosition(test.getPosition().add(0.001f, 0.0f));
				}
			};
			keyboard.addKeybind(205, right);
			
			keyboard.startController();
		}*/
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
		check.setText("labas");
		check.setPosition(Vector2.zero);
		dialog.addChild(check);
		
		dialog.setPosition(Vector2.one);
		
		addDialog(dialog);
		dialog.setVisible(true);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
	}
}
