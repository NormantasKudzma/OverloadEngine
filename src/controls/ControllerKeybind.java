package controls;

public class ControllerKeybind {
	private long bitmask; // Bitmask formatted as long
	private int intmask; // Bitmask formatted as int
	private ControllerEventListener callback;

	public ControllerKeybind(long bitmask, ControllerEventListener callback) {
		setBitmask(bitmask);
		setCallback(callback);
	}

	public long getBitmask() {
		return bitmask;
	}

	public int getIntmask() {
		return intmask;
	}

	public ControllerEventListener getCallback() {
		return callback;
	}

	public void setBitmask(long bitmask) {
		this.bitmask = bitmask;
		intmask = (int) bitmask;
	}

	public void setCallback(ControllerEventListener callback) {
		this.callback = callback;
	}
}
