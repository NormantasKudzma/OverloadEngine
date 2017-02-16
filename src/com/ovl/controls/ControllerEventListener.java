package com.ovl.controls;

import com.ovl.utils.Vector2;

public interface ControllerEventListener {
	void handleEvent(long eventArg, Vector2 pos, int... params);
}
