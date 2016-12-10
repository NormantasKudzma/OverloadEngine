package com.ovl.graphics;

import com.ovl.utils.Vector2;

public class SpineAnimation extends Sprite {
	public SpineAnimation() {
		super();
		init();
	}
	
	public native void FillRenderArray(float[] arr);
}
