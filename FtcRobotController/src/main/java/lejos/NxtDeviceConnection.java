package lejos;

/**
 * Created by pbrier on 17-6-15.
 */

import com.qualcomm.ftccommon.DbgLog;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.remote.NXTCommRequest;
import lejos.nxt.remote.NXTCommand;
import lejos.nxt.remote.NXTProtocol;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTConnector;

import java.lang.Thread;

/**
 * Class for controlling the NXT. Sends data and commands to NXT block.
 * Handles NXT connection establishment.
 */
public class NxtDeviceConnection {

    private NXTConnector conn = new NXTConnector();
    private DataInputStream inDat;
    private DataOutputStream outDat;
    private boolean connected = false;

    public NxtDeviceConnection() {

    }


    /**
     * Sends data packets to the NXT. Accounts for the fact that it might be called from different threads.
     * @param header simple int header. Represents action should be performed on packet receive.
     *
     * @param body data to be send
     */
    public synchronized boolean sendPacket(int header, double body) {
        if (!connected) {
            return false;
        }
        else {
            try {
                outDat.writeByte(header);
                if (header == 1) {
                    outDat.writeFloat((float)body);
                }
                outDat.flush();
                return true;
            }
            catch (IOException e) {
                nxtDeviceDeattached();
                return false;
            }
        }
    }

    /**
     * Returns true if application is connected to the NXT.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Hadnler called on NXT attached event. Creates data streams, enables gyro sensor
     * and informs the application.
     */
    private void nxtDeviceAttached() {
        try {
            new NXTCommand(conn.getNXTComm()).playTone(3000, 10);
            new NXTCommand(conn.getNXTComm()).startProgram("SegwayApp.nxj");

        } catch (Exception e) {

        }

        inDat = new DataInputStream(conn.getInputStream());
        outDat = new DataOutputStream(conn.getOutputStream());
        connected = true;
        //AppInfo.informText("NXT connected");
    }

    /**
     * Hadnler called on NXT deattached event. Closes data streams, disables gyro sensor
     * and informs the application.
     */
    private void nxtDeviceDeattached() {
        connected = false;
        //AppInfo.informText("NXT disconnected");

        try {
            inDat.close();
            outDat.close();
            conn.close();
        } catch (IOException e) {}
    }

    /**
     * Tries to connect over USB. Timeout is 1 second. No exceptions are thrown.
     */
    public void connect()
    {
        DbgLog.msg("Connect NXT..");
        try {
            for (int i =0; i<10; i++) {
                DbgLog.msg(String.format("NXT Connect Attempt: %d", i));
                if (conn.connectTo("usb://")) {
                    DbgLog.msg("Found NXT..");
                    nxtDeviceAttached();
                    DbgLog.msg("Attached to NXT.");
                    return;
                }
                Thread.sleep(100);
            }
        }
        catch (InterruptedException e) {
            DbgLog.logStacktrace(e);
            DbgLog.msg("Failed to open connection to the NXT" + e.toString());
        }
    }

    /**
     * Disconnects from NXT block.
     */
    public void disconnect() {
        nxtDeviceDeattached();
    }

    /**
     * Destructor closing connections on object deletion.
     */
    @Override
    protected void finalize() throws Throwable {
        nxtDeviceDeattached();
        super.finalize();
    }

}

