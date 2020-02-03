package com.ovl.testing;

import com.ovl.game.BaseGame;
import com.ovl.script.LuaScript;
import com.ovl.utils.Paths;

public class TestGame extends BaseGame {
	public void init(){
		LuaScript s = new LuaScript(Paths.getResources() + "test/t.lua");
	}
	
	public void update(float dt)
	{
		
	}
}
