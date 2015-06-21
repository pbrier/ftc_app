/* Copyright (c) 2014 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;

import android.widget.Toast;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.text.SimpleDateFormat;
import java.util.Date;

import lejos.AppContext;
import lejos.NxtDeviceConnection;


/**
 * TeleOp Mode
 * <p>
 *Enables control of the robot via the gamepad
 */
public class pbrier extends OpMode {

  private String startDate;
  private ElapsedTime runtime = new ElapsedTime();
  private NxtDeviceConnection nxt = new NxtDeviceConnection();
  private boolean FirstLoop = true;
  /**
   * Constructor
   */
  public pbrier() {

  }

  /*
   * Code to run when the op mode is first enabled goes here
   * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
   */
  @Override
  public void start() {
    startDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
    runtime.reset();
    DbgLog.msg("Attempting to connect to NXT");

    nxt.connect();
    DbgLog.msg((nxt.isConnected() ? "NXT Connected" : "NXT Not Connected"));
    nxt.sendPacket(255, 1.0); // start

  }

  /*
   * This method will be called repeatedly in a loop
   * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#loop()
   */
  @Override
  public void loop() {
    telemetry.addData("1 Start", "NullOp started at " + startDate);
    telemetry.addData("2 Status", "running for " + runtime.toString());
    telemetry.addData("gamepad1", gamepad1.toString());
    telemetry.addData("gamepad2", gamepad2.toString());
    telemetry.addData("left_y", String.format("%f", (float) gamepad1.left_stick_y));


    // Send joystick state: TODO: send all in one packet
    nxt.sendPacket(1, getRuntime());
    nxt.sendPacket(2, gamepad1.left_stick_y);
    nxt.sendPacket(3, gamepad1.right_stick_y);
    nxt.sendPacket(4, gamepad1.left_trigger);
    nxt.sendPacket(5, gamepad1.right_trigger);
    nxt.sendPacket(6, (gamepad1.left_bumper ? 1.0 : 0.0));
    nxt.sendPacket(7, (gamepad1.right_bumper ? 1.0 : 0.0));

    telemetry.addData("nxt", (nxt.isConnected() ? "NXT Connected" : "NXT Not Connected"));
    if ( FirstLoop ) {
     // AppContext.getContext().showToast(Toast.makeText(AppContext.getContext(), "Bla", Toast.LENGTH_LONG));
      FirstLoop = false;
    }
  }

  /*
   * Code to run when the op mode is first disabled goes here
   * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
   */
  @Override
  public void stop() {
    nxt.sendPacket(255, 0.0); // start
    nxt.disconnect();
  }
}
