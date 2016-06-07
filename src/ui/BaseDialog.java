package ui;

import utils.Paths;
import utils.Vector2;
import engine.BaseGame;
import graphics.Sprite;


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
