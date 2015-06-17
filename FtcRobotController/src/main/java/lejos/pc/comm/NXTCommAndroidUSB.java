package lejos.pc.comm;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

//import com.google.common.base.Preconditions;
//import com.segwayapp.AppContext;

import android.content.Context;
import android.hardware.usb.UsbManager;

import lejos.AppContext;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Our implementation of NXTComm for USB.
 * For communication between Android device and NXT block.
 *
 */
public class NXTCommAndroidUSB extends NXTCommUSB {

	protected UsbManager manager;
	protected UsbDeviceConnection connection;
	protected UsbEndpoint outgoingEndpoint;
	protected UsbEndpoint incomingEndpoint;


	public NXTCommAndroidUSB() {
		manager = AppContext.getUSBManager();
		// manager = (UsbManager) getSystemService(Context.USB_SERVICE);
	}
	/**
	 * Searches for available NXT devices.
	 * @return Vector of NXTInfo devices which are available
	 */
	@Override
	Vector<NXTInfo> devFind() {
		HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		Vector<NXTInfo> nxtInfos = new Vector<NXTInfo>();
		if (!deviceIterator.hasNext()) {
			//No devices found
		} else {
			while (deviceIterator.hasNext()) {
				UsbDevice device = deviceIterator.next();
				if (device.getProductId() == 2 && device.getVendorId() == 1684) {
					NXTInfo info = new NXTInfo();
					info.name = device.getDeviceName();
					info.protocol = NXTCommFactory.USB;
					nxtInfos.addElement(info);
				}
			}
		}
		return nxtInfos;
	}
	/**
	 * Connects to NXT device.
	 * @param nxt NXT block to connect to
	 */
	@Override
	long devOpen(NXTInfo nxt) {
		HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		UsbDevice device;
		if (!deviceIterator.hasNext()) {
			return 0;
		} else {
			while (deviceIterator.hasNext()) {
				device = deviceIterator.next();
				if (device.getDeviceName().equals(nxt.name)) {
					UsbInterface intf;
					intf = device.getInterface(0);
					connection = manager.openDevice(device);
					
		//			Preconditions.checkState(connection.claimInterface(intf, true));
					for (int i = 0; i < intf.getEndpointCount(); i++) {
						UsbEndpoint endpoint = intf.getEndpoint(i);
						if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
							if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
								outgoingEndpoint = endpoint;
							} else {
								incomingEndpoint = endpoint;
							}
						}
					}
					if (outgoingEndpoint == null || incomingEndpoint == null) {
						throw new IllegalArgumentException(
								"Not all endpoints found.");
					}
					return 1;
				}
			}
		}
		return 0;
	}
	
	void devClose(long nxt) {
		connection.close();
	}

	int devWrite(long nxt, byte[] message, int offset, int len) {
		byte [] part = Arrays.copyOfRange(message, offset, offset + len);
		int written = connection.bulkTransfer(outgoingEndpoint, part, part.length, 0);
		return written;
	}

	int devRead(long nxt, byte[] data, int offset, int len) {
		int read = connection.bulkTransfer(incomingEndpoint, data, len, 0);
		data = Arrays.copyOfRange(data, offset, offset + len);
		return read;
		
	}

	/**
	 * Android is always valid!
	 */
	boolean devIsValid(NXTInfo nxt) {
		return true;
	}
}
