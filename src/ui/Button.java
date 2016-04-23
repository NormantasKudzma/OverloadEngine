package ui;

import engine.BaseGame;
import graphics.Color;
import graphics.Sprite;

import java.lang.reflect.Method;

import utils.Paths;
import utils.Vector2;

public class Button extends SpriteComponent{
	private static final float DEFAULT_FONT_SIZE = 99.0f;
	
	protected Object callbackObject;
	protected Method callbackMethod;
	protected Label label;
	
	public Button(){
		this(null, null, null, null);
	}
	
	public Button(BaseGame game, Object obj, Method m, String text){
		super(game);
		callbackObject = obj;
		callbackMethod = m;
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
	
	public boolean isMouseOver(Vector2 pos){
		return pos.x < getPosition().x + sprite.getRenderOffset().x &&
			   pos.x > getPosition().x - sprite.getRenderOffset().x &&
			   pos.y < getPosition().y + sprite.getRenderOffset().y &&
			   pos.y > getPosition().y - sprite.getRenderOffset().y;
	}
	
	@Override
	public boolean onClick(Vector2 pos) {
		boolean ret = isMouseOver(pos);
		if (!ret){
			return ret;
		}
		
		try {
			if (callbackMethod != null && callbackObject != null){
				callbackMethod.invoke(callbackObject);
			}
			else {
				clickFunction();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public void setCallbackMethod(Method m){
		callbackMethod = m;
	}
	
	public void setCallbackObject(Object obj){
		callbackObject = obj;
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
