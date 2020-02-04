package com.ovl.script;

import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

public class Lua {
	private static final Globals mGlobals;
	
	static {
		mGlobals = JsePlatform.standardGlobals();
	}
	
	private Lua() {}
	
	public static Globals globals() {
		return mGlobals;
	}
}
