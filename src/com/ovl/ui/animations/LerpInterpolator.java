package com.ovl.ui.animations;

import com.ovl.utils.Vector2;

public class LerpInterpolator implements Interpolator {
	@Override
	public void interpolate(Vector2 startVal, Vector2 endVal, Vector2 out, float t) {
		float invt = 1.0f - t;
		out.set(startVal.x * invt + endVal.x * t, startVal.y * invt + endVal.y * t);
	}
}
