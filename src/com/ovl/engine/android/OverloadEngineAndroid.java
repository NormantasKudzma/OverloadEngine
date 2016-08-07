package com.ovl.engine.android;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.SurfaceView;

import com.ovl.engine.BaseGame;
import com.ovl.engine.EngineConfig;
import com.ovl.engine.OverloadEngine;
import com.ovl.utils.ConfigManager;
import com.ovl.utils.DebugFrameCounter;

public class OverloadEngineAndroid extends OverloadEngine {
	private class SurfaceViewRenderer implements GLSurfaceView.Renderer {
		@Override
		public void onDrawFrame(GL10 deprecated) {
			renderer.preRender();
			game.render();
			if (config.isDebug) {
				frameCounter.update(deltaTime);
				frameCounter.render();
			}
			renderer.postRender();
		}

		@Override
		public void onSurfaceChanged(GL10 deprecated, int w, int h) {
			GLES20.glViewport(0, 0, w, h);
			frameHeight = h;
			frameWidth = w;
			aspectRatio = 1.0f * w / h;
			
			run();
		}

		@Override
		public void onSurfaceCreated(GL10 deprecated, EGLConfig cfg) {
			Log.w("ovl", "Surface created.");
		}
	}
	
	private CustomSurfaceView surfaceView;
	private SurfaceViewRenderer surfaceViewRenderer;
	private Context ctx;
	
	public OverloadEngineAndroid(EngineConfig config) {
		super(config);
	}

	@Override
	protected void destroy() {
		// TODO Auto-generated method stub	
	}

	@Override
	protected void init() {
		platform = EnginePlatform.PLATFORM_ANDROID;
		
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
		}
	}

	@Override
	protected void loop() {
		new Thread(){
			public void run() {
				t0 = t1 = System.nanoTime();
				while (!isCloseRequested) {
					t0 = System.nanoTime();
					deltaTime = (t0 - t1) * 0.000000001f;
					t1 = t0;
	
					// Poll controllers for input
					//ControllerManager.getInstance().pollControllers();
	
					// Update game logic
					game.update(deltaTime);
	
					surfaceView.requestRender();
					
					try {
						Thread.sleep(50);
					}
					catch (Exception e){
						
					}
				}
			}
		}.start();
	}
	
	public Context getContext(){
		return ctx;
	}
	
	public BaseGame getGame(){
		return game;
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
