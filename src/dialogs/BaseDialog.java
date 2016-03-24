package dialogs;

import java.util.ArrayList;

import utils.Paths;
import utils.Vector2;
import engine.BaseGame;
import graphics.Sprite2D;


public class BaseDialog extends Component{
	protected ArrayList<Button> clickables = new ArrayList<Button>();
	protected boolean isVisible = false;
	protected String name = "BaseDialog";
	
	public BaseDialog(BaseGame game, String name){
		super(game);
		this.name = name;
		this.setSprite(new Sprite2D(Paths.UI + "square_blue.png"));		
		getScale().mul(40f);
		setPosition(Vector2.zero);		
	}
	
	@Override
	protected void initialize() {

	}
}
