package graphics;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;

import physics.PhysicsBody;
import utils.FastMath;
import utils.Vector2;
import engine.GameObject;

public class SimpleFont extends GameObject {
	public static final Font DEFAULT_FONT = new Font("Consolas", Font.PLAIN, 32);

	private String text;
	private Font font;
	
	private BufferedImage bufferedImage;
	private Graphics2D measureGraphics;
	private FontRenderContext frc;
	private Rectangle textBounds = new Rectangle();
	
	private Vector2 textSize = new Vector2();
	private Vector2 textOffset = new Vector2();
	
	public SimpleFont(String text) {
		this(text, DEFAULT_FONT);
	}
	
	public SimpleFont(String text, Font f){
		super(null);
		
		initBufferedImage(1024, 128);
		initEntity(PhysicsBody.EBodyType.NON_INTERACTIVE);
		this.text = text;
		setFont(f);
	}
	
	@Override
	public void destroy() {
		super.destroy();
		measureGraphics.dispose();
	}
	
	public Font getFont(){
		return font;
	}
	
	public static Font getDefaultFont(){
		return DEFAULT_FONT;
	}
	
	public String getText() {
		return text;
	}
	
	public Vector2 getTextOffset(){
		return textOffset;
	}
	
	public Vector2 getTextSize(){
		return textSize;
	}
	
	private void initBufferedImage(int w, int h){
		bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		measureGraphics = (Graphics2D)bufferedImage.getGraphics();
		measureGraphics.setColor(java.awt.Color.WHITE);
		measureGraphics.setBackground(new java.awt.Color(0, true));
		
		measureGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		measureGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	}
	
	private void prerenderText(){
		if (text == null || text.isEmpty()){
			return;
		}
		
		Color oldColor = null;
		if (sprite != null){
			oldColor = sprite.getColor();
			sprite.destroy();
		}
		
		// Prerender text to a texture using default java tools, 
		// because dealing with fonts is a nightmare
		
		GlyphVector gv = font.createGlyphVector(frc, text);
		Rectangle rect = gv.getVisualBounds().getBounds();
		
		Vector2.pixelCoordsToNormal(textSize.set(rect.width, rect.height));
		Vector2.pixelCoordsToNormal(textOffset.set(rect.x, rect.y));
		
		int newWidth = FastMath.nextPowerOfTwo(rect.width + rect.x);
		int newHeight = FastMath.nextPowerOfTwo(rect.height);
		int newX = (int)((newWidth - rect.width - rect.x) * 0.5f);
		int newY = (int)((newHeight - rect.height) * 0.5f);
		
		if (newWidth > bufferedImage.getWidth() || newHeight > bufferedImage.getHeight()){
			measureGraphics.dispose();
			initBufferedImage(newWidth, newHeight);
			measureGraphics.setFont(font);
		}
		else
		{
			measureGraphics.clearRect(0, 0, textBounds.width, textBounds.height);
		}
		
		textBounds.setBounds(0, 0, newWidth, newHeight);
		
		measureGraphics.drawString(text, newX, -rect.y + newY);
		BufferedImage textSubImage = bufferedImage.getSubimage(0, 0, newWidth, newHeight);
		sprite = new Sprite(TextureLoader.getInstance().getTexture(textSubImage));
		sprite.setColor(oldColor);
	}
	
	public void setFont(Font f){
		if (font != null && f.equals(font)){
			return;
		}
		
		font = f;
		measureGraphics.setFont(f);
		frc = measureGraphics.getFontMetrics().getFontRenderContext();
		prerenderText();
	}
	
	public void setText(String text) {
		if (this.text != null && text.equals(this.text)){
			return;
		}
		
		this.text = text;
		prerenderText();
	}
}
