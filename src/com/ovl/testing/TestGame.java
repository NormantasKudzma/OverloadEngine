package com.ovl.testing;

import com.ovl.game.BaseGame;
import com.ovl.script.LuaScript;
import com.ovl.utils.Paths;

public class TestGame extends BaseGame {
	public void init(){
		LuaScript s = new LuaScript(Paths.RESOURCES + "test/t.lua");
		s.call("init");
	}
	
	public void update(float dt)
	{
		
	}
}
