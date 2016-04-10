package dialogs;

import engine.BaseGame;
import graphics.Color;
import graphics.Sprite2D;

import java.lang.reflect.Method;

import utils.Paths;
import utils.Vector2;

public class Button extends SpriteComponent{
	protected Object callbackObject;
	protected Method callbackMethod;
	protected Vector2 fontScale = Vector2.one;
	protected Label label;
	
	public Button(){
		this(null, null, null, null);
	}
	
	public Button(BaseGame game, Object obj, Method m, String text){
		super(game);
		callbackObject = obj;
		callbackMethod = m;
		label = new Label(game, text == null ? " " : text);
		label.setPosition(Vector2.one);
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
		setSprite(new Sprite2D(Paths.UI + "button_green.png"), SpriteComponent.EUIState.NORMAL, false);
		setSprite(new Sprite2D(Paths.UI + "hover_green.png"), SpriteComponent.EUIState.NORMAL, true);

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
		fontScale = (new Vector2(min, min)).mul(7.0f);
		label.setScale(fontScale);
		super.setScale(scale);
	}
	
	public void setText(String text){
		label.setText(text);
	}
}
