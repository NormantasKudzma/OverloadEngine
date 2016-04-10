package graphics;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import physics.PhysicsBody;
import engine.Entity;

public class SimpleFont extends Entity<Sprite2D> {
	public static final Font DEFAULT_FONT = new Font("Serif", Font.PLAIN, 32);

	private String text;
	private Font font;
	
	private BufferedImage measureImage;
	private Graphics measureGraphics;
	private FontRenderContext frc;

	static {
		
	}
	
	public SimpleFont(String text) {
		this(text, DEFAULT_FONT);
	}
	
	public SimpleFont(String text, Font f){
		super(null);
		initEntity(PhysicsBody.EBodyType.NON_INTERACTIVE);
		setFont(f);
		setText(text);
	}
	
	public String getText() {
		return text;
	}
	
	public void setFont(Font f){
		if (font != null && f.equals(font)){
			return;
		}
		
		font = f;
		
		measureImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		measureGraphics = measureImage.getGraphics();
		measureGraphics.setFont(f);	
		frc = measureGraphics.getFontMetrics().getFontRenderContext();
	}
	
	public void setText(String text) {
		if (this.text != null && text.equals(this.text)){
			return;
		}
		
		this.text = text;

		if (sprite != null){
			sprite.destroy();
		}

		// Prerender text to a texture using default java tools, 
		// because dealing with fonts is a nightmare
		GlyphVector gv = font.createGlyphVector(frc, text);
		Rectangle rect = gv.getVisualBounds().getBounds();
		BufferedImage textImage = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = textImage.getGraphics();
		g.setFont(font);
		g.setColor(java.awt.Color.WHITE);
		g.drawString(text, -rect.x, -rect.y);

		// For debugging
		/*try {
			ImageIO.write(textImage, "png", new File("C:\\Users\\Nor-Vartotojas\\Desktop\\textimage.png"));
		}
		catch (Exception e){
			e.printStackTrace();
		}*/
		
		sprite = new Sprite2D(TextureLoader.getInstance().getTexture(textImage));
	}
}
