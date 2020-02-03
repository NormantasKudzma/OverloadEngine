package com.ovl.script;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaScript {
	LuaValue mScript;
	
	public LuaScript(String resource){
		load(resource);
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
		return mScript != null && mScript.istable();
	}
	
	public void call(String f, Object... args) {
		if (!isCallable()) { return; }
		mScript.get(f).invoke(valuesOf(args));
	}
	
	public LuaValue valueOf(Object obj) {
		// STUB
		return null;
	}
	
	public LuaValue[] valuesOf(Object... objs) {
		LuaValue values[] = new LuaValue[objs.length];
		for (int i = 0; i < objs.length; ++i) {
			values[i] = valueOf(objs[i]);
		}
		return values;
	}
}
