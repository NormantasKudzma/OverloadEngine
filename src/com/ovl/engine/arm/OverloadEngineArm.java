package com.ovl.engine.arm;

import com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowUpdateEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;
import com.ovl.controls.ControllerManager;
import com.ovl.controls.arm.KeyboardController;
import com.ovl.controls.arm.MouseController;
import com.ovl.engine.EngineConfig;
import com.ovl.engine.OverloadEngine;
import com.ovl.utils.ConfigManager;
import com.ovl.utils.DebugFrameCounter;

public class OverloadEngineArm extends OverloadEngine implements GLEventListener {
	public static GL2ES2 gl;
	private GLCapabilities capabilities;
	private GLProfile profile;
	private GLWindow window;
	private FPSAnimator animator;
	private long t0, t1;
	private boolean isDestroyed;
	private boolean isInitialized;
	private String glProfile;
	
	public OverloadEngineArm(EngineConfig config){
		super(config);
		isDestroyed = false;
	}
	
	@Override
	protected void destroy() {
		if (isDestroyed){
			return;
		}
		isDestroyed = true;
		
		if (game != null){
			game.destroy();
		}
		ControllerManager.getInstance().destroyManager();
		animator.stop();
	}

	@Override
	protected void init(){
		ConfigManager.gameConfiguration = ConfigManager.loadFileLines(config.configPath);
		Settings.parseConfig(ConfigManager.gameConfiguration);
		platformAssetsRoot = "";
		
		aspectRatio = 1.0f * frameWidth / frameHeight;
		
		if (referenceHeight <= 0){
			referenceHeight = frameHeight;
		}
		if (referenceWidth <= 0){
			referenceWidth = frameWidth;
		}
		referenceScale = Math.min(1.0f * frameWidth / referenceWidth, 1.0f * frameHeight / referenceHeight);
		
		System.out.println(String.format("Requesting window with size [%d;%d], aspect %2.2f", frameWidth, frameHeight, aspectRatio));	
		
		System.out.println("AWT available ? " + GLProfile.isAWTAvailable());

		if (GLProfile.isAvailable(GLProfile.GL2)){
			glProfile = GLProfile.GL2;
		}
		else if (GLProfile.isAvailable(GLProfile.GLES2)){
			glProfile = GLProfile.GLES2;
		}
		profile = GLProfile.get(glProfile);
		capabilities = new GLCapabilities(profile);
		
		window = GLWindow.create(capabilities);
		window.setSize(frameWidth, frameHeight);
		window.setFullscreen(isFullScreen);
		window.addGLEventListener(this);
		window.setDefaultCloseOperation(WindowClosingMode.DISPOSE_ON_CLOSE);
		window.setResizable(false);
		window.setTitle(config.title);
		window.setVisible(true);
		window.setAlwaysOnTop(false);
		window.addWindowListener(new WindowListener() {
			
			@Override
			public void windowResized(WindowEvent e) {}
			
			@Override
			public void windowRepaint(WindowUpdateEvent arg0) {}
			
			@Override
			public void windowMoved(WindowEvent e) {}
			
			@Override
			public void windowLostFocus(WindowEvent e) {}
			
			@Override
			public void windowGainedFocus(WindowEvent e) {}
			
			@Override
			public void windowDestroyed(WindowEvent e) {
				isCloseRequested = true;
				destroy();
			}
			
			@Override
			public void windowDestroyNotify(WindowEvent e) {}
		});
		
		if (animator == null){
			animator = new FPSAnimator(60);
			animator.add(window);
			animator.start();
		}
	}
	
	protected void initArm() {
		if (renderer == null) {
			renderer = new RendererArm();
			initGL();
		}

		ControllerManager controllerManager = ControllerManager.getInstance();
		controllerManager.addController(new MouseController(0));
		controllerManager.addController(new KeyboardController(0));
		
		// Create and initialize game
		game = config.game;
		game.onGameConfigurationLoaded(ConfigManager.gameConfiguration);
		game.init();

		if (config.isDebug) {
			frameCounter = new DebugFrameCounter();
			game.addObject(frameCounter);
		}
		
		window.requestFocus();
	}
	
	public GLWindow getFrame(){
		return window;
	}
	
	@Override
	protected void loop() {
		while (!isCloseRequested) {
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e){
				
			}
		}
	}
	
	private void loopArm(){
		t0 = System.nanoTime();
		deltaTime = (t0 - t1) * 0.000000001f;
		t1 = t0;

		// Poll controllers for input
		ControllerManager.getInstance().pollControllers();

		// Update game logic
		game.update(deltaTime);

		// Render game and swap buffers
		renderer.preRender();
		game.render();

		renderer.postRender();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		if (!isInitialized){
			initArm();
			isInitialized = true;
		}
		loopArm();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		isCloseRequested = true;
		destroy();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		System.out.println("Created gl drawable with profile " + drawable.getGL().getGLProfile().getName());
		
		GL glBase = drawable.getGL();
		if (glBase.isGL2()){
			gl = glBase.getGL2();
		}
		else if (glBase.isGLES2()){
			gl = glBase.getGLES2();
		}
		else {
			gl = null;
			System.err.println("Incorrect gl profile");
		}
		
		System.out.println(String.format("Created window with size[%d;%d]", window.getWidth(), window.getHeight()));
		
		t0 = t1 = System.nanoTime();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		System.out.println(String.format("Reshape called xy %d %d; wh %d %d", x, y, w, h));
		gl.glViewport(x, y, w, h);
	}
}
