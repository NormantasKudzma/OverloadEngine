package utils;

import org.jbox2d.common.Vec2;

public class Vector2 {
	public static final float VECTOR2_TO_PHYSICS = 20.0f;
	public static final float PHYSICS_TO_VECTOR2 = 1.0f / VECTOR2_TO_PHYSICS;
	
	public static final Vector2 right = new Vector2(1.0f, 0.0f);
	public static final Vector2 left = 	new Vector2(-1.0f, 0.0f);
	public static final Vector2 up = 	new Vector2(0.0f, -1.0f);
	public static final Vector2 down = 	new Vector2(0.0f, 1.0f);
	public static final Vector2 zero = 	new Vector2(0.0f, 0.0f);
	public static final Vector2 one = 	new Vector2(1.0f, 1.0f);
	
	public float x, y;
	
	public Vector2(){
		this(0, 0);
	}
	
	public Vector2(Vector2 i){
		x = i.x;
		y = i.y;
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
	
	public void reset(){
		x = 0;
		y = 0;
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
	
	public static Vector2 fromVec2(Vec2 v){
		return new Vector2(v.x * PHYSICS_TO_VECTOR2, v.y * PHYSICS_TO_VECTOR2);
	}
	
	public static Vector2 mul(Vector2 i, Vector2 j){
		return new Vector2(i.x * j.x, i.y * j.y);
	}
	
	public static Vector2 sub(Vector2 i, Vector2 j){
		return new Vector2(i.x - j.x, i.y - j.y);
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
		return String.format("Vector2[x:%f, y:%f] ", x, y);
	}
}
