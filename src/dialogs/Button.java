package dialogs;

import engine.BaseGame;
import graphics.Sprite2D;

import java.lang.reflect.Method;

import utils.Paths;
import utils.Vector2;

public class Button extends Component{
	protected Object callbackObject;
	protected Method callbackMethod;
	protected Sprite2D normalSprite;
	protected Sprite2D hoverSprite;
	protected Vector2 fontScale = Vector2.one;
	protected Label label;
	
	public Button(){
		this(null, null, null, "");
	}
	
	public Button(BaseGame game, Object obj, Method m, String text){
		super(game);
		callbackObject = obj;
		callbackMethod = m;
		normalSprite = new Sprite2D(Paths.UI + "button_green.png");
		hoverSprite = new Sprite2D(Paths.UI + "hover_green.png");
		setSprite(normalSprite);
		//this.text = new TrueTypeFont(ConfigManager.loadFont(Paths.DEFAULT_FONT, 14), false);
		//this.text.setText(text);
		setText(text);
		super.initEntity();
		setScale(Vector2.one);
		setVisible(true);
	}
	
	public String getText(){
		return label.getText();
	}
	
	@Override
	public void initEntity() {
		//stub
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
				return ret;
			}
		}
		catch (Exception e) {
			return ret;		
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
		fontScale = (new Vector2(min, min)).mul(0.16f);
		super.setScale(scale);
	}
	
	public void setText(String text){
		if (label == null){
			label = new Label(game, text);
		}
		else {
			label.setText(text);
		}
	}
	
	@Override
	public void render(Vector2 position, float rotation, Vector2 scale) {
		super.render(position, rotation, scale);
		if (label != null){
			label.render(position, rotation, fontScale);
		}
	}
	
	@Override
	public boolean onHover(Vector2 pos) {
		boolean ret = isMouseOver(pos);
		if (ret){
			setSprite(hoverSprite);
		}
		else {
			setSprite(normalSprite);
		}
		return ret;
	}
}
