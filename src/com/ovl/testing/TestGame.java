package com.ovl.testing;

import com.ovl.game.BaseGame;
import com.ovl.graphics.SimpleFont;
import com.ovl.script.LuaScript;
import com.ovl.utils.Paths;

public class TestGame extends BaseGame {
	LuaScript script;
	
	public void init(){
		super.init();
		
		script = new LuaScript(Paths.RESOURCES + "test.lua");
		script.call("init");
	}
	
	public void update(float dt){
		super.update(dt);
		script.call("update", dt);
	}
}
