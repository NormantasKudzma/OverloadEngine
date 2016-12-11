package com.ovl.testing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Util;

import com.ovl.engine.BaseGame;
import com.ovl.engine.GameObject;
import com.ovl.engine.OverloadEngine;
import com.ovl.physics.PhysicsBody.EBodyType;
import com.ovl.ui.Button;
import com.ovl.utils.Vector2;

public class TestGame extends BaseGame {	
	@Override
	public void init() {
		super.init();
		
		/*GameObject triangle = new GameObject(){
		};
		triangle.initEntity(EBodyType.NON_INTERACTIVE);
		triangle.setPosition(1.0f, 1.0f);
		addObject(triangle);*/
		
		Button btn = new Button(this, "Btn");
		btn.setPosition(1.0f, 1.0f);
		addObject(btn);
	}
}
