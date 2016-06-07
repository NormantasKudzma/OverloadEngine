package ui;

import physics.PhysicsBody;
import utils.Vector2;
import engine.BaseGame;
import engine.GameObject;

public class Component extends GameObject {
	protected Component parent;
	protected String name;
	protected OnClickListener clickListener;

	public Component(BaseGame game){
		super(game);
		initEntity(PhysicsBody.EBodyType.NON_INTERACTIVE);
		initialize();
	}
	
	public String getName(){
		return name;
	}
	
	public Component getParent(){
		return parent;
	}

	protected void hoverEnded(){
		
	}
	
	protected void hoverStarted(){
		
	}
	
	protected void initialize(){
		
	}
	
	public boolean isMouseOver(Vector2 pos) {
		if (!isVisible || sprite == null){
			return false;
		}
		
		final Vector2 size = sprite.getSize().copy().mul(getScale()).mul(0.5f);
		return  pos.x < getPosition().x + size.x &&
				pos.x > getPosition().x - size.x &&
				pos.y < getPosition().y + size.y &&
			    pos.y > getPosition().y - size.y;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (parent != null && parent instanceof Composite){
			Composite compositeParent = (Composite)parent;
			compositeParent.getChildren().remove(this);
		}
	}
	
	public boolean onHover(Vector2 pos) {
		if (!isVisible){
			return false;
		}

		if (isMouseOver(pos)){
			hoverStarted();
			return true;
		}
	    
		hoverEnded();
		return false;
	}

	public boolean onClick(Vector2 pos) {
		if (!isVisible){
			return false;
		}

		if (isMouseOver(pos)){
			if (clickListener != null){
				clickListener.clickFunction(pos);
			}
			return true;
		}
		
		return false;
	}
	
	@Override
	public void render() {
		if (isVisible){
			if (sprite != null){
				super.render();
			}
		}
	}
	
	public void setClickListener(OnClickListener listener){
		clickListener = listener;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setParent(Component c){
		parent = c;
	}
}
