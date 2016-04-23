package ui;

import utils.Paths;
import utils.Vector2;
import engine.BaseGame;
import graphics.Sprite;


public class BaseDialog extends Component{	
	public BaseDialog(BaseGame game, String name){
		super(game);
		isVisible = false;
		this.name = name;
		this.setSprite(new Sprite(Paths.UI + "square_blue.png"));		
		getScale().mul(40f);
		setPosition(Vector2.one);		
	}
	
	@Override
	protected void initialize() {

	}
}
