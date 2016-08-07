package com.ovl.ui;

import com.ovl.engine.BaseGame;
import com.ovl.graphics.Sprite;
import com.ovl.utils.Paths;
import com.ovl.utils.Vector2;

public class Button extends SpriteComponent{
	private static final float DEFAULT_FONT_SIZE = 99.0f;
	protected Label label;
	
	public Button(){
		this(null, null);
	}
	
	public Button(BaseGame game, String text){
		super(game);
		label = new Label(game, text);
		label.setFont(label.getSimpleFont().getFont().deriveFont(DEFAULT_FONT_SIZE));
		addChild(label);
		setScale(Vector2.one);
		setVisible(true);
	}
	
	public String getText(){
		return label.getText();
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		setSprite(new Sprite(Paths.UI + "button_green.png"), SpriteComponent.EUIState.NORMAL, false);
		setSprite(new Sprite(Paths.UI + "hover_green.png"), SpriteComponent.EUIState.NORMAL, true);

		addChild(label);
	}

	@Override
	public void setScale(Vector2 scale) {
		float min = Math.min(scale.x, scale.y);
		label.setFont(label.getSimpleFont().getFont().deriveFont(DEFAULT_FONT_SIZE * min));
		super.setScale(scale);
	}
	
	public void setText(String text){
		label.setText(text);
	}
}
