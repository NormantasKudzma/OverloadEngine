package com.ovl.utils;

import java.util.Locale;

import org.jbox2d.common.Vec2;
import org.json.JSONArray;
import org.json.JSONException;

import com.ovl.engine.OverloadEngine;

public final class Vector2 {
	public static final float VECTOR2_TO_PHYSICS = 20.0f;
	public static final float PHYSICS_TO_VECTOR2 = 1.0f / VECTOR2_TO_PHYSICS;
	
	public static final Vector2 right = new Vector2(1.0f, 0.0f);
	public static final Vector2 left = 	new Vector2(-1.0f, 0.0f);
	public static final Vector2 up = 	new Vector2(0.0f, -1.0f);
	public static final Vector2 down = 	new Vector2(0.0f, 1.0f);
	public static final Vector2 zero = 	new Vector2(0.0f, 0.0f);
	public static final Vector2 one = 	new Vector2(1.0f, 1.0f);
	
	public float x = 0.0f;
	public float y = 0.0f;
	
	public Vector2(){
		this(0.0f, 0.0f);
	}
	
	public Vector2(Vector2 i){
		this(i.x, i.y);
	}
	
	public Vector2(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public float x(){
		return x;
	}
	
	public float y(){
		return y;
	}
	
	public Vector2 set(Vector2 i)
	{
		this.x = i.x;
		this.y = i.y;
		return this;
	}
	
	public Vector2 set(float x, float y){
		this.x = x;
		this.y = y;
		return this;
	}
	
	public Vector2 setX(float x){
		this.x = x;
		return this;
	}
	
	public Vector2 setY(float y){
		this.y = y;
		return this;
	}
	
	public Vector2 add(float x, float y){
		this.x += x;
		this.y += y;
		return this;
	}
	
	public Vector2 add(Vector2 i){
		this.x += i.x;
		this.y += i.y;
		return this;
	}
	
	public float angle(){
		return FastMath.atan2(x, y);
	}
	
	public Vector2 copy(){
		return new Vector2(x, y);
	}
	
	public Vector2 div(Vector2 i){
		x /= i.x;
		y /= i.y;
		return this;
	}
	
	public Vector2 div(float i){
		this.x /= i;
		this.y /= i;
		return this;
	}
	
	public Vector2 div(float i, float j){
		x /= i;
		y /= j;
		return this;
	}
	
	public float dot(Vector2 i){
		return x * i.x + y * i.y;
	}
	
	public Vector2 invert(){
		x = 1.0f / x;
		y = 1.0f / y;
		return this;
	}
	
	public float len(){
		return (float)FastMath.fastSqrt(x * x + y * y);
	}
	
	public Vector2 mul(float c){
		x *= c;
		y *= c;
		return this;
	}
	
	public Vector2 mul(float i, float j){
		this.x *= i;
		this.y *= j;
		return this;
	}
	
	public Vector2 mul(Vector2 i){
		x *= i.x;
		y *= i.y;
		return this;
	}
	
	public Vector2 normalized(){
		float len = len();
		x /= len;
		y /= len;
		return this;
	}
	
	public float ratio(){
		return x / y;
	}
	
	public float ratioInvert(){
		return y / x;
	}
	
	public Vector2 reset(){
		x = 0;
		y = 0;
		return this;
	}
	
	public Vector2 rotate(float angle){
		angle = FastMath.normalizeAngle(angle);
		float sin = FastMath.sinDeg(angle);
		float cos = FastMath.cosDeg(angle);
		float xi = x * cos - y * sin;
		float yi = x * sin + y * cos;
		x = xi;
		y = yi;
		return this;
	}
	
	public Vector2 rotateAroundPoint(Vector2 point, float radAngle){
		float dx = x - point.x;
		float dy = y - point.y;
		
		float sina = FastMath.sin(radAngle);
		float cosa = FastMath.cos(radAngle);
		
		x = cosa * dx - sina * dy + point.x;
		y = sina * dx + cosa * dy + point.y;
		return this;
	}
	
	public Vector2 sub(Vector2 i){
		this.x -= i.x;
		this.y -= i.y;
		return this;
	}
	
	public Vector2 sub(float i, float j) {
		x -= i;
		y -= j;
		return this;
	}
	
	public Vec2 toVec2()
	{
		return toVec2(this);
	}
	
	public static Vector2 add(Vector2 i, Vector2 j){
		return new Vector2(i.x + j.x, i.y + j.y);
	}

	public static Vector2 copy(Vector2 i){
		return new Vector2(i.x, i.y);
	}
	
	public static Vector2 fromJsonArray(JSONArray json) throws JSONException{
		return new Vector2((float)json.getDouble(0), (float)json.getDouble(1));
	}
	
	public static Vector2 fromVec2(Vec2 v){
		return new Vector2(v.x * PHYSICS_TO_VECTOR2, v.y * PHYSICS_TO_VECTOR2);
	}
	
	public static Vector2 invert(Vector2 i){
		return i.copy().invert();
	}
	
	public static Vector2 mul(Vector2 i, Vector2 j){
		return new Vector2(i.x * j.x, i.y * j.y);
	}
	
	public static Vector2 normalize(Vector2 i){
		float len = i.len();
		return new Vector2(i.x / len, i.y / len);
	}
	
	public static Vector2 sub(Vector2 i, Vector2 j){
		return new Vector2(i.x - j.x, i.y - j.y);
	}
	
	public static Vec2 toVec2(float x, float y){
		return new Vec2(x * VECTOR2_TO_PHYSICS, y * VECTOR2_TO_PHYSICS);
	}
	
	public static Vec2 toVec2(Vector2 v){
		return new Vec2(v.x * VECTOR2_TO_PHYSICS, v.y * VECTOR2_TO_PHYSICS);
	}
	
	public static float distanceSqr(Vector2 i, Vector2 j){
		float dx = j.x - i.x;
		float dy = j.y - i.y;
		return dx * dx + dy * dy;
	}
	
	public static float distance(Vector2 i, Vector2 j){
		return (float)Math.sqrt(distanceSqr(i, j));
	}
	
	public static Vector2 pixelCoordsToNormal(Vector2 i){
		OverloadEngine engine = OverloadEngine.getInstance();
		i.mul(2.0f)
		 .div(engine.frameHeight, engine.frameWidth)
		 .div(engine.aspectRatio, 1.0f / engine.aspectRatio);
		return i;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vector2)){
			return false;
		}
		Vector2 v = (Vector2)obj;
		return v == this || (v.x == x && v.y == y);
	}
	
	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "Vector2[x:%f, y:%f]", x, y);
	}
}
