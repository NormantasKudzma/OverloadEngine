package com.ovl.engine.android;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.ovl.engine.EngineConfig;
import com.ovl.engine.OverloadEngine;
import com.ovl.utils.ConfigManager;
import com.ovl.utils.DebugFrameCounter;
import com.ovl.utils.android.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class OverloadEngineAndroid extends OverloadEngine {
	private class SurfaceViewRenderer implements GLSurfaceView.Renderer {
		@Override
		public void onDrawFrame(GL10 deprecated) {
			if (!canRender.get()) { return; }

			if (reloadResources && isRunning){
				renderer.reloadResources();
				game.reloadResources();
			}
			reloadResources = false;

			loop();
			
			renderer.preRender();
			game.render();
			renderer.postRender();
		}

		@Override
		public void onSurfaceChanged(GL10 deprecated, int w, int h) {
			Log.w(String.format("On surface changed w%d h%d", w, h));
			GLES20.glViewport(0, 0, w, h);

			if (frameWidth == 0 || frameHeight == 0 || h < w) {
				frameHeight = h;
				frameWidth = w;
				aspectRatio = 1.0f * w / h;

				if (referenceHeight <= 0) {
					referenceHeight = frameHeight;
				}
				if (referenceWidth <= 0) {
					referenceWidth = frameWidth;
				}
				referenceScale = Math.min(1.0f * frameWidth / referenceWidth, 1.0f * frameHeight / referenceHeight);
			}

			t0 = t1 = System.nanoTime();
			
			if (!isRunning){
				init();
				isRunning = true;
			}

			((RendererAndroid)renderer).onSurfaceChanged(frameWidth, frameHeight, aspectRatio);
		}

		@Override
		public void onSurfaceCreated(GL10 deprecated, EGLConfig cfg) {
			Log.w("Surface created");

			if (renderer == null) {
				renderer = new RendererAndroid();
			}
			initGL();

			canRender.set(true);
		}
	}
	
	private CustomSurfaceView surfaceView;
	private SurfaceViewRenderer surfaceViewRenderer;
	private Context ctx;
	private long t0, t1; // Frame start/end time;
	private boolean isRunning = false;
	private boolean reloadResources = false;
	private AtomicBoolean canRender = new AtomicBoolean(false);
	
	public OverloadEngineAndroid(EngineConfig config) {
		super(config);
	}

	@Override
	protected void destroy() {
		// TODO:...
	}

	@Override
	protected void init() {
		platform = EnginePlatform.PLATFORM_ANDROID;
		platformAssetsRoot = "assets/";
		
		ConfigManager.gameConfiguration = ConfigManager.loadFileLines(config.configPath);
		Settings.parseConfig(ConfigManager.gameConfiguration);
		
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
		t0 = System.nanoTime();
		deltaTime = (t0 - t1) * 0.000000001f;
		t1 = t0;

		game.update(deltaTime);

		surfaceView.requestRender();
	}
	
	public Context getContext(){
		return ctx;
	}
	
	public GLSurfaceView getSurfaceView(Context ctx){
		if (surfaceViewRenderer == null){
			this.ctx = ctx;
			surfaceViewRenderer = new SurfaceViewRenderer();
			surfaceView = new CustomSurfaceView(ctx, surfaceViewRenderer);
		}
		return surfaceView;
	}

	public void onPause() {
		canRender.set(false);
		if (isRunning) {
			renderer.unloadResources();
			game.unloadResources();
		}
		surfaceView.onPause();
	}

	public void onResume(){
		surfaceView.onResume();
		reloadResources = isRunning;
	}
}
