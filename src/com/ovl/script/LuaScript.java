package com.ovl.script;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaScript {
	LuaValue mScript;
	
	public LuaScript(String resource){
		load(resource);
		if (isCallable()) {
			mScript.get("init").call();
		}
	}
	
	private void load(String resource){
		try {
			Globals globals = JsePlatform.standardGlobals();
			LuaValue chunk = globals.loadfile(resource);
			mScript = chunk.call();
		}
		catch (Exception e){
			System.err.println("Err loading script " + resource);
			e.printStackTrace();
		}
	}
	
	public boolean isCallable() {
		return mScript != null && !mScript.istable();
	}
}
