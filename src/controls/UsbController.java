package controls;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.usb4java.BufferUtils;
import org.usb4java.ConfigDescriptor;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceHandle;
import org.usb4java.InterfaceDescriptor;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import utils.Pair;

public class UsbController extends AbstractController {
	private ByteBuffer buffer;
	private int dataLength = 8;
	private byte endpoint = (byte) 0x81;
	private int interfaceNum = 0;
	private IntBuffer transferred = BufferUtils.allocateIntBuffer();

	private String busPort;
	private Context context;
	private Device device;
	private DeviceHandle deviceHandle = new DeviceHandle();
	private boolean isDeviceAttachedToKernel;
	private Pair<Short, Short> productVendor;

	public UsbController(String bp, Context ctx, Device device,
			Pair<Short, Short> pv) {
		busPort = bp;
		context = ctx;
		this.device = device;
		productVendor = pv;
		isActive = false;
		isStopped = false;

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

	public Pair<Short, Short> getProductVendor() {
		return productVendor;
	}

	public void startController() {
		if (isStopped) {
			return;
		}

		isActive = true;

		boolean isDeviceAttachedToKernel = LibUsb.hasCapability(LibUsb.CAP_SUPPORTS_DETACH_KERNEL_DRIVER) && LibUsb.kernelDriverActive(deviceHandle, interfaceNum) == 1;

		int result;
		if (isDeviceAttachedToKernel) {
			result = LibUsb.detachKernelDriver(deviceHandle, interfaceNum);
			if (result != LibUsb.SUCCESS)
				throw new LibUsbException("Unable to detach kernel driver", result);
		}

		result = LibUsb.claimInterface(deviceHandle, interfaceNum);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to claim interface", result);
		}

		eventThread = new Thread() {
			@Override
			public void run() {
				while (isActive) {
					int result = LibUsb.waitForEvent(context, DEFAULT_POLL_TIMEOUT_MCS);
					if (result != LibUsb.SUCCESS) {
						throw new LibUsbException("Unable to handle events", result);
					}

					read(deviceHandle, dataLength);
				}
				destroyController();
			}
		};
		defaultDataValue = read(deviceHandle, dataLength);
		eventThread.start();
	}

	protected void destroyController() {
		if (deviceHandle != null) {
			LibUsb.releaseInterface(deviceHandle, interfaceNum);
			LibUsb.close(deviceHandle);

			if (isDeviceAttachedToKernel) {
				int result = LibUsb.attachKernelDriver(deviceHandle, interfaceNum);
				if (result != LibUsb.SUCCESS)
					throw new LibUsbException("Unable to re-attach kernel driver", result);
			}
		}
	}

	public void pollController() {
		// STUB
		//System.err.println("UNIMPLEMENTED::USB_CONTROLLER_POLL");
	}

	private long read(DeviceHandle handle, int size) {
		int result = LibUsb.bulkTransfer(deviceHandle, endpoint, buffer, transferred, DEFAULT_POLL_TIMEOUT_MCS);
		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to read data", result);
		}

		long bitmask = 0;
		if (buffer.remaining() == dataLength) {
			bitmask = buffer.getLong() ^ defaultDataValue;
			if (bitmask != 0) {
				if (defaultCallback != null) {
					defaultCallback.getCallback().handleEvent(bitmask, null);
				}

				if (oneClickCallback != null) {
					oneClickCallback.getCallback().handleEvent(bitmask, null);
					oneClickCallback = null;
				}

				for (ControllerKeybind bind : keyBindings) {
					if ((bind.getBitmask() & bitmask) != 0) {
						bind.getCallback().handleEvent(bitmask, null);
					}
				}
			}
		}

		buffer.rewind();
		return bitmask;
	}
}
