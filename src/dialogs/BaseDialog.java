package dialogs;

import engine.BaseGame;
import graphics.Sprite2D;

import java.util.ArrayList;

import utils.Paths;


public class BaseDialog extends Component{
	protected ArrayList<Button> clickables = new ArrayList<Button>();
	protected boolean isVisible = false;
	protected String name = "BaseDialog";
	protected BaseGame game = null;
	
	public BaseDialog(String name){
		this.name = name;
		this.setSprite(new Sprite2D(Paths.UI + "square_blue.png"));		
		getScale().mul(40f);
		setPosition(1.0f, 1.0f);		
	}
	
	public void setGame(BaseGame g){
		game = g;
	}
}
