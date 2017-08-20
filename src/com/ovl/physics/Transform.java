package com.ovl.physics;

import com.ovl.utils.Vector2;

public class Transform {
	private Vector2 position;
	private Vector2 scale;
	private float rotation;
	
	public Transform(){
		this(new Vector2(), Vector2.one, 0.0f);
	}
	
	public Transform(Transform t){
		this(t.position, t.scale, t.rotation);
	}
	
	public Transform(Vector2 pos, Vector2 scale, float rotation){
		this.position = pos.copy();
		this.scale = scale.copy();
		this.rotation = rotation;
	}
	
	public void applyTransform(Transform t){
		position.add(t.position);
		scale.mul(t.scale);
		rotation += t.rotation;
	}
	
	@Override
	public Transform clone(){
		return new Transform(this);
	}
	
	public Vector2 getPosition(){
		return position;
	}
	
	public float getRotation(){
		return rotation;
	}
	
	public Vector2 getScale(){
		return scale;
	}
	
	public void setPosition(Vector2 pos){
		position = pos.copy();
	}
	
	public void setPosition(float x, float y){
		position.set(x, y);
	}
	
	public void setRotation(float rot){
		rotation = rot;
	}
	
	public void setScale(Vector2 scale){
		this.scale = scale.copy();
	}
	
	public void setScale(float x, float y){
		scale.set(x, y);
	}
}
