package com.ovl.engine.android;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class CustomSurfaceView extends GLSurfaceView {
	public CustomSurfaceView(Context context, GLSurfaceView.Renderer surfaceViewRenderer) {
		super(context);
		setEGLContextClientVersion(2);
        setRenderer(surfaceViewRenderer);
		
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR);
		setDebugFlags(GLSurfaceView.DEBUG_LOG_GL_CALLS);
	}
}
