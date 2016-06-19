package ui.animations;

import utils.Vector2;

public interface Interpolator {
	public void interpolate(Vector2 startVal, Vector2 endVal, Vector2 out, float t);
}
