package controls;

public enum EController {
	INVALID_CONTROLLER, USB_CONTROLLER, KEYBOARD_CONTROLLER, MOUSE_CONTROLLER;

	public static EController getFromString(String name) {
		EController types[] = EController.values();
		for (EController i : types) {
			if (i.toString().equalsIgnoreCase(name)) {
				return i;
			}
		}

		return INVALID_CONTROLLER;
	}
}
