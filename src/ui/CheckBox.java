package ui;

import utils.Paths;
import utils.Vector2;
import engine.BaseGame;
import graphics.SimpleFont;
import graphics.Sprite;

public class CheckBox extends SpriteComponent{
	public class CheckBoxClickListener implements OnClickListener {
		@Override
		public void clickFunction(Vector2 pos) {
			isChecked = !isChecked;
			setState(isChecked ? EUIState.CLICKED : EUIState.NORMAL);
		}
	}
	
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
		clickListener = new CheckBoxClickListener();
	}
	
	public boolean isChecked(){
		return isChecked;
	}
	
	public void setChecked(boolean checked){
		if (checked != isChecked){
			clickListener.clickFunction(null);
		}
	}

	public void setText(SimpleFont text){
		if (label == null){
			label = new Label(game, text);
			label.setPosition(label.getSimpleFont().getSprite().getSize().x * 0.5f + sprite.getSize().x * 0.5f, 0.0f);
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
