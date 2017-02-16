package com.ovl.ui.animations;

import com.ovl.utils.Vector2;

public interface Interpolator {
	public void interpolate(Vector2 startVal, Vector2 endVal, Vector2 out, float t);
}
