package com.ovl.ui;

import com.ovl.engine.BaseGame;
import com.ovl.graphics.Sprite;
import com.ovl.utils.Paths;
import com.ovl.utils.Vector2;


public class BaseDialog extends Composite {	
	public BaseDialog(BaseGame game, String name){
		super(game);	
		this.name = name;		
	}
	
	@Override
	protected void initialize(){
		super.initialize();
		isVisible = false;
		this.setSprite(new Sprite(Paths.UI + "square_blue.png"));		
		getScale().mul(40f);
		setPosition(Vector2.one);	
	}
}