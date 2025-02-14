/* Copyright (c) 2022 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.drive;

import static java.lang.Thread.sleep;

import android.annotation.SuppressLint;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.Auton.RedShort;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.openftc.apriltag.AprilTagDetection;


/**
 * This file works in conjunction with the External Hardware Class sample called: ConceptExternalHardwareClass.java
 * Please read the explanations in that Sample about how to use this class definition.
 *
 * This file defines a Java Class that performs all the setup and configuration for a sample robot's hardware (motors and sensors).
 *
 * This one file/class can be used by ALL of your OpModes without having to cut & paste the code each time.
 *
 * Where possible, the actual hardware objects are "abstracted" (or hidden) so the OpMode code just makes calls into the class,
 * rather than accessing the internal hardware directly. This is why the objects are declared "private".
 *
 */

public class Constants {
    /*
     * These are motor constants that should be listed online for your motors.
     */

    public static final double TICKS_PER_REV = 537.6; //537.6
    public static final double MAX_RPM = 312.5; //

    /*
     * Set RUN_USING_ENCODER to true to enable built-in hub velocity control using drive encoders.
     * Set this flag to false if drive encoders are not present and an alternative localization
     * method is in use (e.g., tracking wheels).
     *
     * If using the built-in motor velocity PID, update MOTOR_VELO_PID with the tuned coefficients
     * from DriveVelocityPIDTuner.
     */
    public static final boolean RUN_USING_ENCODER = false;
    public static PIDFCoefficients MOTOR_VELO_PID = new PIDFCoefficients(0, 0, 0,
            getMotorVelocityF(MAX_RPM / 60 * TICKS_PER_REV));

    /*
     * These are physical constants that can be determined from your robot (including the track
     * width; it will be tune empirically later although a rough estimate is important). Users are
     * free to chose whichever linear distance unit they would like so long as it is consistently
     * used. The default values were selected with inches in mind. Road runner uses radians for
     * angular distances although most angular parameters are wrapped in Math.toRadians() for
     * convenience. Make sure to exclude any gear ratio included in MOTOR_CONFIG from GEAR_RATIO.
     */
    public static double WHEEL_RADIUS = 1.8898; // in
    public static double GEAR_RATIO = 0.05208; // output (wheel) speed / input (motor) speed 1/19.2
    public static double TRACK_WIDTH = 14.35; // in

    /*
     * These are the feedforward parameters used to model the drive motor behavior. If you are using
     * the built-in velocity PID, *these values are fine as is*. However, if you do not have drive
     * motor encoders or have elected not to use them for velocity control, these values should be
     * empirically tuned.
     */
//    public static double kV = 0.016 / rpmToVelocity(MAX_RPM);
    public static double kV = 0.0165;
    public static double kA = 0.0015;
    public static double kStatic = 0.035;

    /*
     * These values are used to generate the trajectories for you robot. To ensure proper operation,
     * the constraints should never exceed ~80% of the robot's actual capabilities. While Road
     * Runner is designed to enable faster autonomous motion, it is a good idea for testing to start
     * small and gradually increase them later after everything is working. All distance units are
     * inches.
     */
    public static double MAX_VEL = 52; //in/s
    public static double MAX_ACCEL = 30; //in/s^2
    public static double MAX_ANG_VEL = Math.toRadians(215); //degrees/s
    public static double MAX_ANG_ACCEL = Math.toRadians(45); //degrees/s^2

    /*
     * Adjust the orientations here to match your robot. See the FTC SDK documentation for details.
     */
    public static double encoderTicksToInches(double ticks) {
        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
    }

    public static double rpmToVelocity(double rpm) {
        return rpm * GEAR_RATIO * 2 * Math.PI * WHEEL_RADIUS / 60.0;
    }

    public static double getMotorVelocityF(double ticksPerSecond) {
        // see https://docs.google.com/document/d/1tyWrXDfMidwYyP_5H4mZyVgaEswhOC35gvdmP-V-5hA/edit#heading=h.61g9ixenznbx
        return 32767 / ticksPerSecond;
    }

    /* Declare OpMode members. */
    public LinearOpMode controlFreaks;   // gain access to methods in the calling OpMode.
    // Define a constructor that allows the OpMode to pass a reference to itself.
    public Constants(TeleOpFieldOriented opmode) {controlFreaks = opmode;}
    public Constants(RedShort redShort) {controlFreaks = redShort;}

    public Commands commands;
    public Constants(Utilities utilities) {controlFreaks = utilities;}

    public void ConceptAprilTag (TeleOpFieldOriented opmode) { controlFreaks = opmode;}

        // Define Motor and Servo objects  (Make them private so they can't be accessed externally)
    DcMotor leftFront         = null;
    DcMotor rightFront        = null;
    DcMotor rightRear         = null;
    DcMotor leftRear          = null;
    public DcMotor slide_motor       = null; //deploys and retracts the elevator
    public DcMotor e_tilt            = null; //controls the tilt angle of the elevator
    DcMotor hanger            = null;
//    public BNO055IMU imu      = null;      // Control/Expansion Hub IMU
//    public BHI260IMU imu      = null;
    YawPitchRollAngles orientation;
    AngularVelocity angularVelocity;
    public IMU imu;
    public Servo claw         = null      ; //Claw servo
    public Servo p_tilt              = null; //controls the tilt angle of the pixel delivery (claw)
    Servo drone               = null; //release the drone

//    // Initialize Touch Sensors
//    // Touch sensor for tilt of elevator CH 0-1
//    TouchSensor e_tilt_stop;
//    // Touch sensor for lower limit of elevator CH 2-3
//    TouchSensor e_stop;
//    // Touch sensor for tilt upper limit of elevator CH 4-5
//    TouchSensor e_tilt_zero;

    private double robotHeading  = 0;
    private double headingOffset = 0;
    private double headingError  = 0;


    private int scoreYExtension; //the encoder value for each of the scoring positions: L, M, H, T(Top)
    private int e_tiltPickUp = 200; //The tilt position for picking up a pixel
    private int e_tiltStowed = 100; //The tilt position for moving across the field
    private double p_tiltPickup = 0; //The tilt position of the claw mechanism for picking up a pixel
    private double p_tiltScore = 0.75; //The tilt position of the claw mechanism for scoring a pixel

    // These variable are declared here (as class members) so they can be updated in various methods,
    // but still be displayed by sendTelemetry()
    private double  targetHeading       = 0;
    private double  driveSpeed          = 0;
    private double  turnSpeed           = 0;
    private double  leftSpeed           = 0;
    private double  rightSpeed          = 0;
    private int     leftFrontTarget     = 0;
    private int     leftRearTarget      = 0;
    private int     rightFrontTarget    = 0;
    private int     rightRearTarget     = 0;
    boolean         bugOut          = false;
    boolean         tryToGetACone   = false;
    int             counter             = 0;
    boolean         START_LEFT;
    double          TURN_SPEED;
    double          DRIVE_SPEED;

    // Define Drive constants.  Make them public so they CAN be used by the calling OpMode
    static final double     COUNTS_PER_MOTOR_REV    = 28.0 ;     // REV HD Hex
    static final double     DRIVE_GEAR_REDUCTION    = 15.0 ;     // No External Gearing.
    static final double     WHEEL_DIAMETER_INCHES   = 3.78 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);

    // These constants define the desired driving/control characteristics
    // They can/should be tweaked to suit the specific robot drive train.
    static final double     HEADING_THRESHOLD       = 1.0 ;    // How close must the heading get to the target before moving to next step.
    // Requiring more accuracy (a smaller number) will often make the turn take longer to get into the final position.
    // Define the Proportional control coefficient (or GAIN) for "heading control".
    // We define one value when Turning (larger errors), and the other is used when Driving straight (smaller errors).
    // Increase these numbers if the heading does not correct strongly enough (eg: a heavy robot or using tracks)
    // Decrease these numbers if the heading does not settle on the correct value (eg: very agile robot with omni wheels)
    static final double     P_TURN_GAIN            = 0.02;     // Larger is more responsive, but also less stable
    static final double     P_DRIVE_GAIN           = 0.03;     // Larger is more responsive, but also less stable

    static final double FEET_PER_METER = 3.28084;



//    OpenCvCamera camera;
//    AprilTagDetectionPipeline aprilTagDetectionPipeline;

//    // Lens intrinsics
//    // UNITS ARE PIXELS
//    // NOTE: this calibration is for the C920 webcam at 800x448.
//    // You will need to do your own calibration for other configurations!
//    double fx = 578.272;
//    double fy = 578.272;
//    double cx = 402.145;
//    double cy = 221.506;
//
//    // UNITS ARE METERS
//    double tagsize = 0.166;
//
//    int LEFT    = 35; // Tag ID from the 36h11 family
//    int MIDDLE  = 36;
//    int RIGHT   = 37;
//
//    AprilTagDetection tagOfInterest = null;
private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera

    /**
     * The variable to store our instance of the AprilTag processor.
     */
    private AprilTagProcessor aprilTag;

    /**
     * The variable to store our instance of the vision portal.
     */
    private VisionPortal visionPortal;
    /**
     * Initialize all the robot's hardware.
     * This method must be called ONCE when the OpMode is initialized.
     *
     * All of the hardware devices are accessed via the hardware map, and initialized.
     */
    public void init()    {
        // Initialize Motors (note: need to use reference to actual OpMode).
        leftFront   = controlFreaks.hardwareMap.get(DcMotor.class, "leftFront");
        rightFront  = controlFreaks.hardwareMap.get(DcMotor.class, "rightFront");
        leftRear    = controlFreaks.hardwareMap.get(DcMotor.class, "leftRear");
        rightRear   = controlFreaks.hardwareMap.get(DcMotor.class, "rightRear");
        slide_motor = controlFreaks.hardwareMap.get(DcMotor.class, "slide_motor");
        e_tilt      = controlFreaks.hardwareMap.get(DcMotor.class, "e_tilt");
        hanger      = controlFreaks.hardwareMap.get(DcMotor.class,"hanger");

        e_tilt.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        e_tilt.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        e_tilt.setTargetPosition(0);
        e_tilt.setPower(0.5);
        e_tilt.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        hanger.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hanger.setTargetPosition(0);
        hanger.setPower(0.5);
        hanger.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        slide_motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slide_motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slide_motor.setTargetPosition(0);
        slide_motor.setPower(1.0);
        slide_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Set motor directions
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftRear.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightRear.setDirection(DcMotor.Direction.REVERSE);


        // Initialize Servos
        claw        = controlFreaks.hardwareMap.get(Servo.class, "claw");
        p_tilt      = controlFreaks.hardwareMap.get(Servo.class, "p_tilt");
        drone       = controlFreaks.hardwareMap.get(Servo.class, "drone");


                  // define initialization values for IMU, and then initialize it.
//        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
//        parameters.angleUnit            = BNO055IMU.AngleUnit.DEGREES;
//        parameters.accelUnit            = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        //TODO:
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection  usbDirection  = RevHubOrientationOnRobot.UsbFacingDirection.RIGHT;

        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);

        // Now initialize the IMU with this mounting orientation
        // Note: if you choose two conflicting directions, this initialization will cause a code exception.
        imu = controlFreaks.hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(orientationOnRobot));
//        imu = controlFreaks.hardwareMap.get(BHI260IMU.class, "imu");
//        imu.initialize(parameters);

        controlFreaks.telemetry.addData(">", "Hardware Initialized");
        controlFreaks.telemetry.update();
    }


//    public void runAprilTag(){
//        int cameraMonitorViewId = controlFreaks.hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", controlFreaks.hardwareMap.appContext.getPackageName());
//        camera = OpenCvCameraFactory.getInstance().createWebcam(controlFreaks.hardwareMap.get(WebcamName.class, "Webcam 2"), cameraMonitorViewId);
//        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);
//
//        camera.setPipeline(aprilTagDetectionPipeline);
//        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
//
//        {
//            @Override
//            public void onOpened()
//            {
//                camera.startStreaming(1920,1080, OpenCvCameraRotation.UPRIGHT);
//            } //640x480, 1280x720, 1024x768, 800x448, 960x720, 960x544, 864x480, 848x480, 800x600, 800x448, 640x360, 352x288, 320x240, 1920x1080
//
//            @Override
//            public void onError(int errorCode)
//            {
//
//            }
//        });
//
//        controlFreaks.telemetry.setMsTransmissionInterval(50);
//
//
//        while (!controlFreaks.isStarted() && !controlFreaks.isStopRequested())
//        {
//            ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();
//
//            if(currentDetections.size() != 0)
//            {
//                boolean tagFound = false;
//
//                for(AprilTagDetection tag : currentDetections)
//                {
//                    if(tag.id == LEFT || tag.id == MIDDLE || tag.id == RIGHT)
//                    {
//                        tagOfInterest = tag;
//                        tagFound = true;
//                        break;
//                    }
//                }
//
//                if(tagFound)
//                {
//                    controlFreaks.telemetry.addLine("Tag of interest is in sight!\n\nLocation data:");
//                    tagToTelemetry(tagOfInterest);
//                }
//                else
//                {
//                    controlFreaks.telemetry.addLine("Don't see tag of interest :(");
//
//                    if(tagOfInterest == null)
//                    {
//                        controlFreaks.telemetry.addLine("(The tag has never been seen)");
//                    }
//                    else
//                    {
//                        controlFreaks.telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
//                        tagToTelemetry(tagOfInterest);
//                    }
//                }
//
//            }
//            else
//            {
//                controlFreaks.telemetry.addLine("Don't see tag of interest :(");
//
//                if(tagOfInterest == null)
//                {
//                    controlFreaks.telemetry.addLine("(The tag has never been seen)");
//                }
//                else
//                {
//                    controlFreaks.telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
//                    tagToTelemetry(tagOfInterest);
//                }
//
//            }
//
//            controlFreaks.telemetry.update();
//            controlFreaks.sleep(20);
//        }
//
//        /*
//         * The START command just came in: now work off the latest snapshot acquired
//         * during the init loop.
//         */
//
//        /* Update the telemetry */
//        camera.closeCameraDevice(); //shut off the camera to preserve battery
//
//        if(tagOfInterest != null)
//        {
//            controlFreaks.telemetry.addLine("Tag snapshot:\n");
//            tagToTelemetry(tagOfInterest);
//            controlFreaks.telemetry.update();
//        }
//        else
//        {
//            controlFreaks.telemetry.addLine("No tag snapshot available, it was never sighted during the init loop :(");
//            controlFreaks.telemetry.update();
//        }
//    }

    public void runWithEncoders(){
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Set the encoders for closed loop speed control, and reset the heading.
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void floatChassis(){
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    public void brakeChassis(){
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    // **********  LOW Level driving functions.  ********************


//    public void getCone() {
//        tryToGetACone = true;
//        //pick up cone
//        slide_motor.setTargetPosition(450);
//        Intake();
//        ElapsedTime bugoutTimer = new ElapsedTime();
//        bugoutTimer.reset();
//
//        while (touch.getState() && tryToGetACone) { // no cone collected yet
//            counter += 1;
//            // TODO: 12/15/2022
////            if (bugoutTimer.time() >= 1500){ // something went wrong, just go park
////                bugOutAndPark();
////                return;
////            }
//            if (counter > 950){ // something went wrong, just go park
//                bugOutAndPark();
//                return;
//            }
//        }
//        if (!touch.getState()) { // cone activates the switch
//            tryToGetACone = false;
//            Back.setPosition(.5);
//            Front.setPosition(.5);
//            slide_motor.setTargetPosition(slideMiddlePosition);//put slide up
//            while (slide_motor.getCurrentPosition() < slideLowPosition){
//                //let slide go up before moving to clear cone stack
//            }
//        }
//    }


    private void sendTelemetry(boolean straight) {

        if (straight) {
            controlFreaks.telemetry.addData("Motion", "Drive Straight");
            controlFreaks.telemetry.addData("Target Pos L:R",  "%7d:%7d",      leftFrontTarget,  rightFrontTarget);
            controlFreaks.telemetry.addData("Actual Pos L:R",  "%7d:%7d",      leftFront.getCurrentPosition(),
                    rightFront.getCurrentPosition());
        } else {
            controlFreaks.telemetry.addData("Motion", "Turning");
        }

        controlFreaks.telemetry.addData("Angle Target:Current", "%5.2f:%5.0f", targetHeading, robotHeading);
        controlFreaks.telemetry.addData("Error:Steer",  "%5.1f:%5.1f", headingError, turnSpeed);
        controlFreaks.telemetry.addData("Wheel Speeds L:R.", "%5.2f : %5.2f", leftSpeed, rightSpeed);
        controlFreaks.telemetry.update();
    }

    /**
     * read the raw (un-offset Gyro heading) directly from the IMU
     */
    public double getRawHeading() {
//        Orientation angles   = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        Orientation angles = imu.getRobotOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return angles.firstAngle;
        //YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        //AngularVelocity angularVelocity = imu.getRobotAngularVelocity(AngleUnit.DEGREES);
    }

    /**
     * Reset the "offset" heading back to zero
     */
    public void resetHeading() {
        // Save a new heading offset equal to the current raw heading.
        headingOffset = getRawHeading();
        robotHeading = 0;
    }

    @SuppressLint("DefaultLocale")
    void tagToTelemetry(AprilTagDetection detection)
    {
        controlFreaks.telemetry.addLine(String.format("\nDetected tag ID=%d", detection.id));
        controlFreaks.telemetry.addLine(String.format("Translation X: %.2f feet", detection.pose.x*FEET_PER_METER));
        controlFreaks.telemetry.addLine(String.format("Translation Y: %.2f feet", detection.pose.y*FEET_PER_METER));
        controlFreaks.telemetry.addLine(String.format("Translation Z: %.2f feet", detection.pose.z*FEET_PER_METER));
        controlFreaks.telemetry.addLine(String.format("Rotation Yaw: %.2f degrees", Math.toDegrees(detection.pose.x)));
        controlFreaks.telemetry.addLine(String.format("Rotation Pitch: %.2f degrees", Math.toDegrees(detection.pose.y)));
        controlFreaks.telemetry.addLine(String.format("Rotation Roll: %.2f degrees", Math.toDegrees(detection.pose.z)));
    }
}
