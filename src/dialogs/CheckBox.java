package dialogs;

import utils.Paths;
import utils.Vector2;
import engine.BaseGame;
import graphics.Sprite2D;

public class CheckBox extends SpriteComponent{
	private boolean isChecked = false;
	private Label label;
	
	public CheckBox(BaseGame game) {
		super(game);
	}

	@Override
	protected void initialize() {
		super.initialize();
		setSprite(new Sprite2D(Paths.UI + "checkbox_normal.png"), EUIState.NORMAL, false);
		setSprite(new Sprite2D(Paths.UI + "checkbox_normal_hover.png"), EUIState.NORMAL, true);
		setSprite(new Sprite2D(Paths.UI + "checkbox_checked.png"), EUIState.CLICKED, false);
		setSprite(new Sprite2D(Paths.UI + "checkbox_checked_hover.png"), EUIState.CLICKED, true);
		
		label = new Label(game, "hi");
		label.setPosition(getSprite().getHalfSize().x, 0.0f);
		addChild(label);
	}
	
	public boolean isChecked(){
		return isChecked;
	}
	
	@Override
	public void clickFunction() {
		isChecked = !isChecked;
		setState(isChecked ? EUIState.CLICKED : EUIState.NORMAL);
	}
}
