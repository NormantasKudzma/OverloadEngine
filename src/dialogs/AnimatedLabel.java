package dialogs;


public abstract class AnimatedLabel extends Label{
	private float animationDuration = 0.0f;
	private boolean isInfinite = false;
	
	public AnimatedLabel(){
		super();
	}
	
	public AnimatedLabel(String text){
		this(0.0f, text);
	}
	
	public AnimatedLabel(float duration, String text){
		super(text);
		if (duration > 0.0f){
			animationDuration = duration;
		}
	}
	
	public void setInfinite(boolean isInfinite){
		this.isInfinite = isInfinite;
	}
	
	public abstract void animationStep(float deltaTime);
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		if (isInfinite || animationDuration > 0.0f){
			animationDuration -= deltaTime;
			animationStep(deltaTime);
		}
	}
}
