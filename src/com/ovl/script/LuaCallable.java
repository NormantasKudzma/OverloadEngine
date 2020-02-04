package com.ovl.script;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class LuaCallable {
	protected LuaValue mValue;
	
	public LuaCallable() {
		
	}
	
	public LuaCallable(LuaValue val) {
		mValue = val;
	}
	
	public LuaValue valueOf(Object obj) {
		return CoerceJavaToLua.coerce(obj);
	}
	
	public LuaValue[] valuesOf(Object... objs) {
		LuaValue values[] = new LuaValue[objs.length];
		for (int i = 0; i < objs.length; ++i) {
			values[i] = valueOf(objs[i]);
		}
		return values;
	}

	public boolean isCallable() {
		return mValue != null && (mValue.isclosure() || mValue.isfunction());
	}
	
	public Varargs call(Object... objs) {
		return mValue.invoke(valuesOf(objs));
	}
	
	public Varargs callSafe(Object... objs){
		if (!isCallable()) { return null; }
		return call(objs);
	}
}
