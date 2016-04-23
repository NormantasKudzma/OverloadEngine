package ui;

import utils.Paths;
import engine.BaseGame;
import graphics.SimpleFont;
import graphics.Sprite;

public class CheckBox extends SpriteComponent{
	private boolean isChecked = false;
	private Label label;
	
	public CheckBox(BaseGame game) {
		super(game);
	}

	@Override
	protected void initialize() {
		super.initialize();
		setSprite(new Sprite(Paths.UI + "checkbox_normal.png"), EUIState.NORMAL, false);
		setSprite(new Sprite(Paths.UI + "checkbox_normal_hover.png"), EUIState.NORMAL, true);
		setSprite(new Sprite(Paths.UI + "checkbox_checked.png"), EUIState.CLICKED, false);
		setSprite(new Sprite(Paths.UI + "checkbox_checked_hover.png"), EUIState.CLICKED, true);	
	}
	
	public boolean isChecked(){
		return isChecked;
	}
	
	@Override
	public void clickFunction() {
		isChecked = !isChecked;
		setState(isChecked ? EUIState.CLICKED : EUIState.NORMAL);
	}
	
	public void setChecked(boolean checked){
		if (checked != isChecked){
			clickFunction();
		}
	}

	public void setText(SimpleFont text){
		if (label == null){
			label = new Label(game, text);
			label.setPosition(label.getSimpleFont().getSprite().getHalfSize().x + sprite.getHalfSize().x, 0.0f);
			addChild(label);
		}
		else {
			label.setText(text.getText());
			label.setFont(text.getFont());
		}
	}
	
	public void setText(String text){
		setText(new SimpleFont(text));
	}
}
