package testing;

import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;

import physics.PhysicsWorld;
import physics.PhysicsBody.EBodyType;
import ui.BaseDialog;
import ui.Button;
import ui.CheckBox;
import ui.Label;
import utils.OverloadRandom;
import utils.Paths;
import utils.Vector2;
import engine.BaseGame;
import engine.GameObject;
import graphics.Color;
import graphics.Sprite;

public class TestGame extends BaseGame {
	
	public TestGame() {
		//
	}

	@Override
	public void init() {
		PhysicsWorld.getInstance().setGravity(new Vector2(0.0f, -9.8f));
		
		GameObject<Sprite> kamuolys = new GameObject<Sprite>(this);
		kamuolys.initEntity(EBodyType.INTERACTIVE);		
		kamuolys.setPosition(1.0f, 1.5f);	
		Fixture karkasas = kamuolys.getPhysicsBody().attachBoxCollider(new Vector2(0.2f, 0.2f));
		karkasas.m_restitution = 1.02f;		
		kamuolys.setSprite(new Sprite(Paths.RESOURCES + "1.png"));
		addEntity(kamuolys);	
		GameObject<Sprite> siena = new GameObject<Sprite>(this);
		siena.initEntity(EBodyType.INTERACTIVE);
		siena.setPosition(1.0f, 0.25f);	
		Fixture karkasas2 = siena.getPhysicsBody().attachBoxCollider(new Vector2(1.0f, 0.2f));
		siena.getPhysicsBody().getBody().setType(BodyType.STATIC);
		siena.setSprite(new Sprite(Paths.RESOURCES + "2.png"));
		addEntity(siena);
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
