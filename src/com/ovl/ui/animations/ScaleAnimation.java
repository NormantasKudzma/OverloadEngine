package com.ovl.ui.animations;

import com.ovl.ui.Component;
import com.ovl.utils.Vector2;

public class ScaleAnimation extends TransformAnimation {
	public ScaleAnimation(Component c, Vector2 from, Vector2 to) {
		super(c, from, to);
	}

	@Override
	protected void animationStep() {
		super.animationStep();
		component.setScale(out.x, out.y);
	}
}
