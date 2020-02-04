package com.ovl.script;

import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.ovl.engine.OverloadEngine;
import com.ovl.game.BaseGame;
import com.ovl.game.GameObject;
import com.ovl.graphics.Sprite;
import com.ovl.utils.Paths;

public class LuaSetup {
	private LuaSetup() {}
	
	public static void setup() {
		Globals globals = Lua.globals();
		
		globals.set("Engine", CoerceJavaToLua.coerce(OverloadEngine.getInstance()));
		globals.set("Paths", CoerceJavaToLua.coerce(Paths.class));
		
		globals.set("GameObject", CoerceJavaToLua.coerce(GameObject.class));
		globals.set("Sprite", CoerceJavaToLua.coerce(Sprite.class));
	}
}
