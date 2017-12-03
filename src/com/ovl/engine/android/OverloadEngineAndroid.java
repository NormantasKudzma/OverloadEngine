package com.ovl.engine.android;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.SurfaceView;

import com.ovl.engine.EngineConfig;
import com.ovl.engine.OverloadEngine;
import com.ovl.utils.ConfigManager;
import com.ovl.utils.DebugFrameCounter;
import com.ovl.utils.android.Log;

public class OverloadEngineAndroid extends OverloadEngine {
	private class SurfaceViewRenderer implements GLSurfaceView.Renderer {
		@Override
		public void onDrawFrame(GL10 deprecated) {
			loop();
			
			renderer.preRender();
			game.render();
			renderer.postRender();
		}

		@Override
		public void onSurfaceChanged(GL10 deprecated, int w, int h) {
			GLES20.glViewport(0, 0, w, h);
			frameHeight = h;
			frameWidth = w;
			aspectRatio = 1.0f * w / h;
			
			if (referenceHeight <= 0){
				referenceHeight = frameHeight;
			}
			if (referenceWidth <= 0){
				referenceWidth = frameWidth;
			}
			referenceScale = Math.min(1.0f * frameWidth / referenceWidth, 1.0f * frameHeight / referenceHeight);
			
			t0 = t1 = System.nanoTime();
			
			if (!isRunning){
				isRunning = true;
				init();
			}
			
			surfaceView.requestRender();
		}

		@Override
		public void onSurfaceCreated(GL10 deprecated, EGLConfig cfg) {
			Log.w("Surface created");
		}
	}
	
	private CustomSurfaceView surfaceView;
	private SurfaceViewRenderer surfaceViewRenderer;
	private Context ctx;
	//private boolean isStarted = false;
	private long t0, t1; // Frame start/end time;
	private boolean isRunning = false;
	
	public OverloadEngineAndroid(EngineConfig config) {
		super(config);
	}

	@Override
	protected void destroy() {
		// TODO Auto-generated method stub	
	}

	@Override
	protected void init() {
		//isStarted = true;
		platform = EnginePlatform.PLATFORM_ANDROID;
		platformAssetsRoot = "assets/";
		
		ConfigManager.gameConfiguration = ConfigManager.loadFileLines(config.configPath);
		Settings.parseConfig(ConfigManager.gameConfiguration);
				
		if (renderer == null) {
			renderer = new RendererAndroid();
			initGL();
		}
		
		// Create and initialize game
		game = config.game;
		game.onGameConfigurationLoaded(ConfigManager.gameConfiguration);
		game.init();

		if (config.isDebug) {
			frameCounter = new DebugFrameCounter();
			game.addObject(frameCounter);
		}
	}

	@Override
	protected void loop() {
		/*new Thread(){
			public void run() {*/
				//while (!isCloseRequested) {
					t0 = System.nanoTime();
					deltaTime = (t0 - t1) * 0.000000001f;
					t1 = t0;
	
					// Poll controllers for input
					//ControllerManager.getInstance().pollControllers();
	
					// Update game logic
					game.update(deltaTime);
	
					surfaceView.requestRender();
					
					/*try {
						// plz FIXME ...
						long sleepTime = (long)(Math.max(0.001f, 0.016667f - deltaTime) * 100);
						Thread.sleep(sleepTime);
					}
					catch (Exception e){
						
					}*/
				//}
		/*	}
		}.start();*/
	}
	
	public Context getContext(){
		return ctx;
	}
	
	public SurfaceView getSurfaceView(Context ctx){
		if (surfaceViewRenderer == null){
			surfaceViewRenderer = new SurfaceViewRenderer();
			surfaceView = new CustomSurfaceView(ctx, surfaceViewRenderer);
		}
		this.ctx = ctx;
		return surfaceView;
	}
}
