package com.ovl.script;

import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.ovl.engine.OverloadEngine;
import com.ovl.game.GameObject;
import com.ovl.graphics.Primitive;
import com.ovl.graphics.SimpleFont;
import com.ovl.graphics.Sprite;
import com.ovl.utils.Paths;
import com.ovl.utils.Vector2;

public class Lua {
	private static final Globals mGlobals;
	
	static {
		mGlobals = JsePlatform.standardGlobals();
	}
	
	private Lua() {}
	
	public static Globals globals() {
		return mGlobals;
	}

	public static void expose(String name, Object o) {
		globals().set(name, CoerceJavaToLua.coerce(o));
	}
	
	public static void setup() {
		Lua.expose("Engine", OverloadEngine.getInstance());
		Lua.expose("Paths", Paths.class);
		Lua.expose("Vector2", Vector2.class);
		
		Lua.expose("GameObject", GameObject.class);
		Lua.expose("Sprite", Sprite.class);
		Lua.expose("Primitive", Primitive.class);
		Lua.expose("SimpleFont", SimpleFont.class);
	}
}
