package com.ovl.script;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaScript extends LuaCallable {
	public LuaScript(String resource){
		super();
		load(resource);
	}
	
	private void load(String resource){
		try {
			Globals globals = JsePlatform.standardGlobals();
			LuaValue chunk = globals.loadfile(resource);
			mValue = chunk.call();
		}
		catch (Exception e){
			System.err.println("Err loading script " + resource);
			e.printStackTrace();
		}
	}
	
	public boolean hasContents() {
		return mValue != null && mValue.istable();
	}
	
	public LuaValue get(String item){
		return mValue.get(item);
	}
	
	public LuaValue get(int item) {
		return mValue.get(item);
	}
	
	public LuaCallable getCallable(String item) {
		return new LuaCallable(get(item));
	}
	
	public LuaCallable getCallable(int item) {
		return new LuaCallable(get(item));
	}
	
	public void call(String f, Object... args) {
		if (!hasContents()) { return; }
		get(f).invoke(valuesOf(args));
	}
}
