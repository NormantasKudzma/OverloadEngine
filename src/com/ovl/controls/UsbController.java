package com.ovl.controls;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.usb4java.BufferUtils;
import org.usb4java.ConfigDescriptor;
import org.usb4java.Device;
import org.usb4java.DeviceHandle;
import org.usb4java.InterfaceDescriptor;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import com.ovl.utils.Pair;

public class UsbController extends Controller {
	public static final long DEFAULT_POLL_TIMEOUT_MCS = 20000; // Poll sleep time in microseconds	
	
	private ByteBuffer buffer;
	private int dataLength = 8;
	private byte endpoint = (byte) 0x81;
	private int interfaceNum = 0;
	private IntBuffer transferred = BufferUtils.allocateIntBuffer();

	private String busPort;
	private Device device;
	private DeviceHandle deviceHandle = new DeviceHandle();
	private boolean isDeviceAttachedToKernel;
	private Pair<Integer, Integer> productVendor;
	private long lastBitmask = 0;

	public UsbController(int index, String bp, Device device, Pair<Integer, Integer> pv) {
		super(index);
		type = Type.TYPE_USB;
		busPort = bp;
		this.device = device;
		productVendor = pv;

		int result = LibUsb.open(device, deviceHandle);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to open device handle!", result);
		}

		ConfigDescriptor descriptor = new ConfigDescriptor();
		LibUsb.getActiveConfigDescriptor(device, descriptor);
		InterfaceDescriptor ifaceDescriptor = descriptor.iface()[0].altsetting()[0];
		interfaceNum = ifaceDescriptor.bInterfaceNumber();
		endpoint = ifaceDescriptor.endpoint()[0].bEndpointAddress();
		dataLength = ifaceDescriptor.endpoint()[0].wMaxPacketSize();
		buffer = BufferUtils.allocateByteBuffer(dataLength).order(ByteOrder.BIG_ENDIAN);
	}

	public String getBusPort() {
		return busPort;
	}

	public Device getDevice() {
		return device;
	}

	public Pair<Integer, Integer> getProductVendor() {
		return productVendor;
	}

	public boolean startController() {
		if (isActive() || !super.startController()){
			return false;
		}

		boolean isDeviceAttachedToKernel = LibUsb.hasCapability(LibUsb.CAP_SUPPORTS_DETACH_KERNEL_DRIVER) && LibUsb.kernelDriverActive(deviceHandle, interfaceNum) == 1;

		int result;
		if (isDeviceAttachedToKernel) {
			result = LibUsb.detachKernelDriver(deviceHandle, interfaceNum);
			if (result != LibUsb.SUCCESS){
				throw new LibUsbException("Unable to detach kernel driver", result);
			}
		}

		result = LibUsb.claimInterface(deviceHandle, interfaceNum);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to claim interface", result);
		}

		eventThread = new Thread() {
			@Override
			public void run() {
				while (UsbController.this.isActive()) {
					read();
				}
			}
		};
		eventThread.start();
		
		return true;
	}

	protected void destroyController() {
		stopController();
		
		try {
			eventThread.join();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		if (deviceHandle != null) {
			LibUsb.releaseInterface(deviceHandle, interfaceNum);
			LibUsb.close(deviceHandle);

			if (isDeviceAttachedToKernel) {
				int result = LibUsb.attachKernelDriver(deviceHandle, interfaceNum);
				if (result != LibUsb.SUCCESS)
					throw new LibUsbException("Unable to re-attach kernel driver", result);
			}
		}
		super.destroyController();
	}

	public void pollController() {
		if (!isActive()){
			return;
		}
		
		if (lastBitmask != 0) {
			if (defaultCallback != null) {
				defaultCallback.getCallback().handleEvent(lastBitmask, null);
			}

			if (oneClickCallback != null) {
				oneClickCallback.getCallback().handleEvent(lastBitmask, null);
				oneClickCallback = null;
			}

			for (ControllerKeybind bind : keyBindings) {
				if ((bind.getBitmask() & lastBitmask) != 0) {
					bind.getCallback().handleEvent(lastBitmask, null);
				}
			}
		}
		lastBitmask = 0;
	}

	private long read() {
		int result = LibUsb.bulkTransfer(deviceHandle, endpoint, buffer, transferred, DEFAULT_POLL_TIMEOUT_MCS);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to read data", result);
		}

		long bitmask = 0;
		if (transferred.get() == dataLength) {
			bitmask = buffer.getLong() ^ defaultDataValue;
			
			if (defaultDataValue == 0){
				defaultDataValue = bitmask;
			}
			
			lastBitmask = bitmask;
		}

		transferred.rewind();
		buffer.rewind();
		return bitmask;
	}
}
