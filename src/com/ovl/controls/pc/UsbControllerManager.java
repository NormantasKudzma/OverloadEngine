package com.ovl.controls.pc;

import java.util.ArrayList;

import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import com.ovl.controls.Controller;
import com.ovl.controls.ControllerManager;
import com.ovl.utils.Config;
import com.ovl.utils.ConfigManager;
import com.ovl.utils.Pair;
import com.ovl.utils.Paths;

public class UsbControllerManager {
	private static ArrayList<Pair<Integer, Integer>> allowedUsbProductVendorList;
	private static Context libUsbContext;
	private static DeviceList usbDeviceList;
	
	private UsbControllerManager(){
		
	}
	
	public static void init(){
		libUsbContext = new Context();
		int result = LibUsb.init(libUsbContext);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to initialize libusb.", result);
		}
		
		loadAllowedUsbDeviceList(Paths.getAllowedDevices());
		loadUsbDevices();
	}
	
	public static void destroy(){		
		if (usbDeviceList != null) {
			LibUsb.freeDeviceList(usbDeviceList, true);
			LibUsb.exit(libUsbContext);
		}
	}
	
	private static void filterUsbDevices() {
		if (allowedUsbProductVendorList == null) {
			allowedUsbProductVendorList = new ArrayList<Pair<Integer, Integer>>();
			loadAllowedUsbDeviceList(Paths.getAllowedDevices());
		}

		ControllerManager cm = ControllerManager.getInstance();
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
					cm.addController(new UsbController(index, bp, device, pair));
					++index;
				}
			}
		}
	}
	
	private static void loadAllowedUsbDeviceList(String path) {
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
	
	private static void loadUsbDevices() {
		int index = 0;
		Controller c = null;
		ControllerManager cm = ControllerManager.getInstance();
		while ((c = cm.getController(Controller.Type.TYPE_USB, index)) != null){
			cm.removeController(c);
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
}
