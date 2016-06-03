package testing;

import junit.framework.TestCase;
import utils.Vector2;
import controls.AbstractController;
import controls.ControllerEventListener;
import controls.ControllerManager;
import controls.EController;
import controls.MouseController;
import engine.BaseGame;
import engine.EngineConfig;
import engine.OverloadEngine;

public class ControllerTest extends TestCase {
	public void testGetController(){
		
		class EmptyGame extends BaseGame {
			@Override
			public void init() {
				super.init();
				
				ControllerManager manager = ControllerManager.getInstance();
				
				AbstractController keyboard = manager.getController(EController.KEYBOARD_CONTROLLER);
				assertNotNull(keyboard);
				keyboard.startController();
				assertTrue(keyboard.isActive());
				keyboard.stopController();
				assertFalse(keyboard.isActive());
				
				AbstractController mouse = manager.getController(EController.MOUSE_CONTROLLER);
				assertNotNull(mouse);
				mouse.startController();
				assertTrue(mouse.isActive());
				
				MouseController m = (MouseController)mouse;
				m.setMouseMoveListener(new ControllerEventListener(){
					@Override
					public void handleEvent(long eventArg, Vector2 pos, int... params) {
						assertNotNull(pos);
					}
				});
				mouse.pollController();
				mouse.stopController();
				assertFalse(mouse.isActive());
				
				int numUsbControllers = manager.getNumControllers(EController.USB_CONTROLLER);
				if (numUsbControllers > 0){
					for (int i = 0; i < numUsbControllers; ++i){
						AbstractController usb = manager.getController(EController.USB_CONTROLLER, i);
						assertNotNull(usb);
						assertFalse(usb.isActive());
						usb.startController();
						assertTrue(usb.isActive());
						usb.stopController();
						assertFalse(usb.isActive());
					}
				}

				OverloadEngine.requestClose();
			}
		}
		
		EmptyGame game = new EmptyGame();
		
		EngineConfig cfg = new EngineConfig();
		cfg.game = game;
		cfg.isFullscreen = false;
		
		OverloadEngine engine = new OverloadEngine(cfg);
		engine.run();
	}
}
