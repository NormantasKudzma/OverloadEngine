package controls;

public enum EController {
	INVALIDCONTROLLER, USBCONTROLLER, LWJGLKEYBOARDCONTROLLER, LWJGLMOUSECONTROLLER;

	public static EController getFromString(String name) {
		EController types[] = EController.values();
		for (EController i : types) {
			if (i.toString().equalsIgnoreCase(name)) {
				return i;
			}
		}

		return INVALIDCONTROLLER;
	}
}
