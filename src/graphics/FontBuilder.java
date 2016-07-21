package graphics;

public interface FontBuilder {
	public CustomFont buildFont(String name, int style, int size);
	public SimpleFont createFontObject();
}
