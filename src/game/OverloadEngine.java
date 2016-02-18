package game;

import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import utils.Vector2;
import audio.AudioManager;
import controls.ControllerEventListener;
import controls.ControllerManager;
import controls.EController;
import controls.LwjglMouseController;

public class OverloadEngine {
	private static final int TARGET_FPS = 60;
	
	public static final int frameHeight = 720;
	public static final int frameWidth = 1280;
	public static final float aspectRatio = (float)frameWidth / (float)frameHeight;
	
	private long deltaTime;
	private BaseGame game;
	private long t0, t1; // Frame start/end time

	private void destroy() {
		game.destroy();
		AudioManager.destroy();

		Display.destroy();
	}

	private void init() {
		try {
			Display.setTitle("-");
			Display.setResizable(false);
			Display.setDisplayMode(new DisplayMode(frameWidth, frameHeight));
			Display.create();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);

		// Create and initialize game
		if (game == null){
			System.out.println("You need to setGame() first!");
			return;
		}
		game.init();

		LwjglMouseController c = (LwjglMouseController) ControllerManager.getInstance().getController(EController.LWJGLMOUSECONTROLLER);
		c.addKeybind(0, new ControllerEventListener() {

			@Override
			public void handleEvent(long eventArg, Vector2 pos, int... params) {
				if (params[0] == 1) {
					game.onClick(pos);
				}
			}
		});
		
		c.setMouseMoveListener(new ControllerEventListener() {
			@Override
			public void handleEvent(long eventArg, Vector2 pos, int... params) {
				game.onHover(pos);
			}
		});
	}

	private void loop() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glViewport(0, 0, frameWidth, frameHeight);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

		//GL11.glOrtho(0, frameWidth, 0, frameHeight, 0, -1);
		//GL11.glMatrixMode(GL11.GL_MODELVIEW);

		t0 = t1 = System.currentTimeMillis();

		while (!Display.isCloseRequested()) {
			t0 = System.currentTimeMillis();
			deltaTime = t0 - t1;
			t1 = t0;

			// Poll controllers for input
			ControllerManager.getInstance().pollControllers();

			// Update game logic
			game.update(deltaTime * 0.001f);

			// Prepare for rendering
			GL11.glLoadIdentity();
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glTranslatef(-1.0f, -1.0f, 0.0f);
			GL11.glPopMatrix();
			//GL11.glScalef(1.0f, -1.0f, 1.0f);

			// Render game and swap buffers
			game.render();
			
			Display.update();
			Display.sync(TARGET_FPS);
		}
	}

	public void run() {
		init();
		loop();
		destroy();
	}
	
	public void setGame(BaseGame g){
		game = g;
	}
}
