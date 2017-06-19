package com.ovl.controls;

import java.util.ArrayList;

import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import com.ovl.utils.Config;
import com.ovl.utils.ConfigManager;
import com.ovl.utils.Pair;
import com.ovl.utils.Paths;

public class ControllerManager {
	private static final ControllerManager INSTANCE = new ControllerManager();

	private ArrayList<Controller> allControllers = new ArrayList<Controller>();
	private ArrayList<Controller> activeControllers = new ArrayList<Controller>();
	private ArrayList<Pair<Integer, Integer>> allowedUsbProductVendorList;
	private Context libUsbContext;
	private DeviceList usbDeviceList;

	private ControllerManager() {
		libUsbContext = new Context();
		int result = LibUsb.init(libUsbContext);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to initialize libusb.", result);
		}
		loadAllowedUsbDeviceList(Paths.getAllowedDevices());
		loadUsbDevices();
	}

	public void addController(Controller controller){
		if (!allControllers.contains(controller)){
			allControllers.add(controller);
		}
	}
	
	public void controllerStarted(Controller controller){
		if (!activeControllers.contains(controller)){
			activeControllers.add(controller);
		}
	}
	
	public void controllerStopped(Controller controller){
		activeControllers.remove(controller);
	}
	
	public void destroyManager() {
		for (Controller c : allControllers) {
			c.destroyController();
		}
		
		if (usbDeviceList != null) {
			LibUsb.freeDeviceList(usbDeviceList, true);
			LibUsb.exit(libUsbContext);
		}
	}

	private void filterUsbDevices() {
		if (allowedUsbProductVendorList == null) {
			allowedUsbProductVendorList = new ArrayList<Pair<Integer, Integer>>();
			loadAllowedUsbDeviceList(Paths.getAllowedDevices());
		}

		int index = 0;
		for (Device device : usbDeviceList) {
			DeviceDescriptor descriptor = new DeviceDescriptor();
			int result = LibUsb.getDeviceDescriptor(device, descriptor);
			if (result != LibUsb.SUCCESS) {
				throw new LibUsbException("Unable to read device descriptor", result);
			}

			for (Pair<Integer, Integer> pair : allowedUsbProductVendorList) {
				if (descriptor.idProduct() == pair.key && descriptor.idVendor() == pair.value) {
					String bp = LibUsb.getBusNumber(device) + ":" + LibUsb.getPortNumber(device);
					allControllers.add(new UsbController(index, bp, device, pair));
					++index;
				}
			}
		}
	}

	public Controller getController(Controller.Type type) {
		return getController(type, 0);
	}

	public Controller getController(Controller.Type type, int index) {
		for (Controller c : allControllers){
			if (c.getType() == type && c.getIndex() == index){
				return c;
			}
		}

		return null;
	}

	public static ControllerManager getInstance() {
		return INSTANCE;
	}

	public int getNumControllers(Controller.Type type) {
		int numControllers = 0;
		while (getController(type, numControllers) != null){
			++numControllers;
		}
		return numControllers;
	}

	public void loadAllowedUsbDeviceList(String path) {
		if (path == null) {
			return;
		}

		allowedUsbProductVendorList = new ArrayList<Pair<Integer, Integer>>();
		Config<String, String> cfg = ConfigManager.loadConfigAsPairs(path);
		for (Pair<String, String> pair : cfg.contents) {
			if (pair.key.startsWith("0x")){
				pair.key = pair.key.substring(2);
			}
			
			if (pair.value.startsWith("0x")){
				pair.value = pair.value.substring(2);
			}
			
			allowedUsbProductVendorList.add(new Pair<Integer, Integer>(Integer.parseInt(pair.key, 16), Integer.parseInt(pair.value, 16)));
		}
	}
	
	private void loadUsbDevices() {
		int index = 0;
		Controller c = null;
		while ((c = getController(Controller.Type.TYPE_USB, index)) != null){
			allControllers.remove(c);
			++index;
		}
		
		if (usbDeviceList != null) {
			LibUsb.freeDeviceList(usbDeviceList, true);
		}

		usbDeviceList = new DeviceList();
		int result = LibUsb.getDeviceList(libUsbContext, usbDeviceList);
		if (result < 0) {
			throw new LibUsbException("Unable to get device list", result);
		}

		filterUsbDevices();
	}

	public void pollControllers() {
		for (Controller c : activeControllers) {
			c.pollController();
		}
	}

	public String printUsbDeviceList(boolean isPrintedToStdOut) {
		StringBuilder deviceListStringBuilder = new StringBuilder();
		for (Device device : usbDeviceList) {
			DeviceDescriptor descriptor = new DeviceDescriptor();
			int result = LibUsb.getDeviceDescriptor(device, descriptor);
			if (result != LibUsb.SUCCESS) {
				throw new LibUsbException("Unable to read device descriptor", result);
			}
			deviceListStringBuilder.append(descriptor.toString());
		}
		if (isPrintedToStdOut) {
			System.out.print(deviceListStringBuilder.toString());
		}
		return deviceListStringBuilder.toString();
	}
}
