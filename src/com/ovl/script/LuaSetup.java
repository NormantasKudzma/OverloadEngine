package com.ovl.script;

import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.ovl.utils.Paths;

public class LuaSetup {
	private LuaSetup() {}
	
	public static void setup() {
		Globals globals = Lua.globals();
		
		globals.set("Paths", CoerceJavaToLua.coerce(Paths.class));
		globals.set("muh_secret", 7);
	}
}
