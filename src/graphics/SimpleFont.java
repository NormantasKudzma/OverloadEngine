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
import engine.Entity;

public class SimpleFont extends Entity<Sprite2D> {
	public static final Font DEFAULT_FONT = new Font("Consolas", Font.PLAIN, 32);

	private String text;
	private Font font;
	
	private BufferedImage bufferedImage;
	private Graphics2D measureGraphics;
	private FontRenderContext frc;
	private Rectangle textBounds = new Rectangle();
	
	public SimpleFont(String text) {
		this(text, DEFAULT_FONT);
	}
	
	public SimpleFont(String text, Font f){
		super(null);
		
		bufferedImage = new BufferedImage(1024, 256, BufferedImage.TYPE_INT_ARGB);
		measureGraphics = (Graphics2D)bufferedImage.getGraphics();
		measureGraphics.setColor(java.awt.Color.WHITE);
		measureGraphics.setBackground(new java.awt.Color(0, true));
		
		measureGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		measureGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
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
	
	public String getText() {
		return text;
	}
	
	private void prerenderText(){
		if (text == null || text.isEmpty()){
			return;
		}
		
		if (sprite != null){
			sprite.destroy();
		}
		
		// Prerender text to a texture using default java tools, 
		// because dealing with fonts is a nightmare

		measureGraphics.clearRect(0, 0, textBounds.width, textBounds.height);
		
		GlyphVector gv = font.createGlyphVector(frc, text);
		Rectangle rect = gv.getVisualBounds().getBounds();
		
		int newWidth = FastMath.nextPowerOfTwo(rect.width + rect.x);
		int newHeight = FastMath.nextPowerOfTwo(rect.height);
		int newX = (int)((newWidth - rect.width - rect.x) * 0.5f);
		int newY = (int)((newHeight - rect.height) * 0.5f);
		textBounds.setBounds(0, 0, newWidth, newHeight);
		
		measureGraphics.drawString(text, newX, -rect.y + newY);
		BufferedImage textSubImage = bufferedImage.getSubimage(0, 0, newWidth, newHeight);
		sprite = new Sprite2D(TextureLoader.getInstance().getTexture(textSubImage));
		
		// For debugging
		/*try {
			ImageIO.write(bufferedImage, "png", new File("C:\\Users\\Nor-Vartotojas\\Desktop\\textimage" + bufferedImage.hashCode() + ".png"));
		}
		catch (Exception e){
			e.printStackTrace();
		}*/
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
