package lejos;

import android.content.Context;
import android.hardware.usb.UsbManager;

import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;

/**
 * Class holding application context.
 */
public class AppContext {

    private static FtcRobotControllerActivity context;

    /**
     * Application context setter
     */
    public static void setContext(FtcRobotControllerActivity ctx) {
        context = ctx;
    }

    /**
     * Application context getter
     */
    public static FtcRobotControllerActivity getContext() {
        return context;
    }

    /**
     * Returns usb manager according to the application context.
     */
    public static UsbManager getUSBManager() {
        return (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

}
