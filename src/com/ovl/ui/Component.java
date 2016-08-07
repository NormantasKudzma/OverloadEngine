package com.ovl.ui;

import java.util.ArrayList;

import com.ovl.engine.BaseGame;
import com.ovl.engine.GameObject;
import com.ovl.physics.PhysicsBody;
import com.ovl.ui.animations.TransformAnimation;
import com.ovl.utils.Vector2;

public class Component extends GameObject {
	protected Component parent;
	protected String name;
	protected OnClickListener clickListener;
	protected ArrayList<TransformAnimation> animations = new ArrayList<TransformAnimation>(1);

	public Component(BaseGame game){
		super(game);
		initEntity(PhysicsBody.EBodyType.NON_INTERACTIVE);
		initialize();
	}
	
	public void addAnimation(TransformAnimation anim){
		animations.add(anim);
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
		removeAllAnimations();
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
	
	public void removeAnimation(TransformAnimation anim){
		animations.remove(anim);
	}
	
	public void removeAllAnimations(){
		animations.clear();
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

	@Override
	public void update(float deltaTime) {
		if (isVisible){
			super.update(deltaTime);
			for (TransformAnimation anim : animations){
				anim.update(deltaTime);
			}
		}
	}
}
