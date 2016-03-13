package dialogs;

import java.util.ArrayList;

import utils.Vector2;
import controls.IClickable;
import engine.BaseGame;
import engine.Entity;
import graphics.Sprite2D;

public abstract class Component extends Entity<Sprite2D> implements IClickable{
	protected Component parent;
	protected ArrayList<Component> children = new ArrayList<Component>(1);
	protected String name;
	protected boolean isVisible = true;
	protected Component lastClickable;
	
	public Component(BaseGame game){
		super(game);
		initEntity();
		initialize();
	}
	
	protected abstract void initialize();
	
	@Override
	public void initEntity() {
		super.initEntity();
		getPhysicsBody().getBody().setGravityScale(0.0f);
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public boolean isVisible(){
		return isVisible;
	}
	
	public void setVisible(boolean isVisible){
		this.isVisible = isVisible;
	}
	
	@Override
	public boolean isMouseOver(Vector2 pos) {
		if (!isVisible){
			return false;
		}
		
		Vector2 offset = sprite == null ? Vector2.zero : sprite.getRenderOffset();
		
		return  pos.x < getPosition().x + offset.x &&
				pos.x > getPosition().x - offset.x &&
				pos.y < getPosition().y + offset.y &&
			    pos.y > getPosition().y - offset.y;
	}

	@Override
	public boolean onHover(Vector2 pos) {
		if (!isVisible){
			return false;
		}
		
		boolean ret = isMouseOver(pos);
		
		lastClickable = null;
		if (ret){
			for (Component child : children){
				if (child.onHover(pos)){
					lastClickable = child;
					break;
				}
			}
		}
	     
		return ret;
	}

	@Override
	public boolean onClick(Vector2 pos) {
		if (!isVisible){
			return false;
		}
		
		boolean ret = isMouseOver(pos);
		
		if (lastClickable != null){
			lastClickable.onClick(pos);
		}
		
		return ret;
	}
	
	public void addChild(Component c){
		if (c != null){
			c.setParent(this);
			children.add(c);
		}
	}
	
	public ArrayList<Component> getChildren(){
		return children;
	}
	
	public void setParent(Component c){
		parent = c;
	}
	
	public Component getParent(){
		return parent;
	}
	
	@Override
	public void update(float deltaTime) {
		if (isVisible){
			super.update(deltaTime);
			for (Component component : children){
				component.update(deltaTime);
			}
		}
	}
	
	@Override
	public void render() {
		if (isVisible){
			if (sprite != null){
				super.render();
			}
			
			for (Component component : children){
				component.render();
			}
		}
	}
}
