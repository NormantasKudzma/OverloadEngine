package engine;

import graphics.PhysicsDebugDraw;
import graphics.Renderer;

import java.io.File;

import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.PixelFormat;

import utils.DebugFrameCounter;
import utils.Vector2;
import controls.ControllerEventListener;
import controls.ControllerManager;
import controls.EController;
import controls.LwjglMouseController;

public class OverloadEngine {
	public static final int TARGET_FPS = 60;
	public static final int TARGET_BPP = 32;
	
	public static final int frameHeight = 720;
	public static final int frameWidth = 1280;
	public static final float aspectRatio = (float)frameWidth / (float)frameHeight;
	
	private BaseGame game;
	private Renderer renderer;
	private boolean isDebugDrawn = true;
	private boolean isFullscreen = true;
	private float deltaTime;
	private long t0, t1; // Frame start/end time
	private DebugFrameCounter frameCounter;
	private String title = "Overload engine";

	private void destroy() {
		game.destroy();
		ControllerManager.getInstance().destroyManager();
		Display.destroy();
	}

	private void init() {
		File nativesFolder = null;
		switch(LWJGLUtil.getPlatform())
		{
		    case LWJGLUtil.PLATFORM_WINDOWS:
		    {
		    	nativesFolder = new File("./native/windows/");
		    }
		    break;

		    case LWJGLUtil.PLATFORM_LINUX:
		    {
		    	nativesFolder = new File("./native/linux/");
		    }
		    break;

		    case LWJGLUtil.PLATFORM_MACOSX:
		    {
		    	nativesFolder = new File("./native/macosx/");
		    }
		    break;
		}
		System.setProperty("org.lwjgl.librarypath", nativesFolder.getAbsolutePath());
		
		try {
			Display.setTitle(title);
			Display.setResizable(false);
			Display.setFullscreen(isFullscreen);
			DisplayMode displayModes[] = Display.getAvailableDisplayModes();
			DisplayMode bestMatch = null;
			for (DisplayMode d : displayModes){
				if (d.getFrequency() == TARGET_FPS
					&& d.getWidth() == frameWidth
					&& d.getHeight() == frameHeight
					&& d.getBitsPerPixel() == TARGET_BPP){
					bestMatch = d;
					break;
				}
			}
			if (bestMatch == null){
				System.out.println("No display mode with current parameters was found. Creating default one.");
				bestMatch = new DisplayMode(frameWidth, frameHeight);
			}
			Display.setDisplayMode(bestMatch);
			Display.create();
			Display.setVSyncEnabled(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if (renderer == null){
			renderer = Renderer.getInstance();
		}

		// Create and initialize game
		if (game == null){
			System.err.println("You need to call setGame() first!");
			return;
		}
		game.init();
		
		if (isDebugDrawn){
			frameCounter = new DebugFrameCounter();
		}

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
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glClearColor(0.4f, 0.4f, 0.4f, 1.0f);

		GL11.glViewport(0, 0, frameWidth, frameHeight);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

		t0 = t1 = System.nanoTime();

		while (!Display.isCloseRequested()) {
			t0 = System.nanoTime();
			deltaTime = (t0 - t1) * 0.000000001f;
			t1 = t0;

			// Poll controllers for input
			ControllerManager.getInstance().pollControllers();

			// Update game logic
			game.update(deltaTime);

			// Prepare for rendering
			GL11.glLoadIdentity();
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glTranslatef(-1.0f, -1.0f, 0.0f);
			GL11.glPopMatrix();

			// Render game and swap buffers
			renderer.preRender();
			game.render();
			
			// Render debug things
			if (isDebugDrawn){
				PhysicsDebugDraw.render();
				
				frameCounter.update(deltaTime);
				frameCounter.render();
			}
			renderer.postRender();
			
			Display.update();
		}
	}

	public void run() {
		init();
		loop();
		destroy();
	}
	
	public void setDebugDraw(boolean isDebugDrawn){
		this.isDebugDrawn = isDebugDrawn;
	}
	
	public void setFullscreen(boolean fullscreen){
		isFullscreen = fullscreen;
	}
	
	public void setGame(BaseGame g){
		game = g;
	}
	
	public void setTitle(String title){
		if (title != null){
			this.title = title;
		}
	}
}
