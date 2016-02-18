package controls;

public interface IController {
	public void addKeybind(long bitmask, ControllerEventListener callback);

	public ControllerKeybind clearKeybind(long bitmask);

	public void clearKeybinds();

	public Iterable<ControllerKeybind> getAllKeybinds();

	public long getDefaultBitmaskValue();

	public void pollController();

	public ControllerKeybind removeUnmaskedCallback();

	public void setOneClickCallback(ControllerEventListener callback);

	public void setUnmaskedCallback(ControllerEventListener callback);
}
