package com.ovl.testing;

import com.ovl.engine.BaseGame;
import com.ovl.engine.GameObject;
import com.ovl.graphics.Color;
import com.ovl.graphics.Sprite;
import com.ovl.physics.PhysicsBody.EBodyType;
import com.ovl.utils.Paths;
import com.ovl.utils.Vector2;

public class TestGame extends BaseGame {	
	@Override
	public void init() {
		super.init();
		
		GameObject obj1 = new GameObject();
		obj1.initEntity(EBodyType.NON_INTERACTIVE);
		obj1.setSprite(new Sprite(Paths.UI + "square_green.png"));
		obj1.setScale(0.4f, 0.4f);
		//obj1.setColor(new Color(0.85f, 0.5f, 0.4f, 1.0f));
		obj1.setPosition(1.0f, 1.0f);
		addObject(obj1);
		
		/*GameObject obj2 = new GameObject();
		obj2.initEntity(EBodyType.NON_INTERACTIVE);
		obj2.setSprite(new Sprite(Paths.UI + "square_blue.png"));
		obj2.setScale(0.7f, 0.7f);
		obj2.setPosition(0.4f, 0.4f);
		//obj2.setColor(new Color(0.2f, 0.8f, 0.4f, 1.0f));
		addObject(obj2);*/
		
		/*GameObject obj3 = new GameObject();
		obj3.initEntity(EBodyType.NON_INTERACTIVE);
		obj3.setSprite(new Sprite(Paths.UI + "square_yellow.png"));
		//obj3.setColor(new Color(0.2f, 0.1f, 0.9f, 1.0f));
		obj3.setPosition(1.7f, 1.7f);
		obj3.setScale(1.0f, 1.0f);
		addObject(obj3);*/
	}
}
