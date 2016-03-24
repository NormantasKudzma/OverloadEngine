package dialogs;

import java.util.ArrayList;

import physics.PhysicsBody;

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
		initEntity(PhysicsBody.EBodyType.NON_INTERACTIVE);
		initialize();
	}	
	
	public void addChild(Component c){
		if (c != null){
			c.setParent(this);
			children.add(c);
			c.setPosition(c.getPosition().add(getPosition()));
		}
	}
	
	public ArrayList<Component> getChildren(){
		return children;
	}
	
	public String getName(){
		return name;
	}
	
	public Component getParent(){
		return parent;
	}

	protected abstract void initialize();
	
	@Override
	public boolean isMouseOver(Vector2 pos) {
		if (!isVisible || sprite == null){
			return false;
		}
		
		return  pos.x < getPosition().x + sprite.getRenderOffset().x &&
				pos.x > getPosition().x - sprite.getRenderOffset().x &&
				pos.y < getPosition().y + sprite.getRenderOffset().y &&
			    pos.y > getPosition().y - sprite.getRenderOffset().y;
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
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setParent(Component c){
		parent = c;
	}
	
	public void setPosition(float x, float y){
		Vector2 delta = new Vector2(x, y).sub(getPosition());
		for (Component component : children){
			component.setPosition(component.getPosition().add(delta));
		}
		super.setPosition(x, y);
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
}
