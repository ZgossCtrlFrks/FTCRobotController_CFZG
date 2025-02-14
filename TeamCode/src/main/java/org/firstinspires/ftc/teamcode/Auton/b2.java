package org.firstinspires.ftc.teamcode.Auton;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.drive.Commands;
import org.firstinspires.ftc.teamcode.drive.Constants;
import org.firstinspires.ftc.teamcode.drive.MecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

@Autonomous

public class b2 extends LinearOpMode {
//    @Override
    public Constants constants;
    public Commands commands;
    private TrajectorySequence chosenSequence;


    public void runOpMode() throws InterruptedException {
        //Instantiate the drive system
        MecanumDrive drivetrain = new MecanumDrive(hardwareMap);

        //Provide the initial pose
        Pose2d startPose = new Pose2d(-36, 60, Math.toRadians(270)); //tell the robot where it starts

        //Occupy the initial pose
        drivetrain.setPoseEstimate(startPose);

        //TODO: Build contingency sequences
        TrajectorySequence Left = drivetrain.trajectorySequenceBuilder(startPose)
                .lineTo(new Vector2d(-36,36))
                .build();

        TrajectorySequence Center = drivetrain.trajectorySequenceBuilder(startPose)
                .lineTo(new Vector2d(-36,36))
                .build();

        TrajectorySequence Right = drivetrain.trajectorySequenceBuilder(startPose)
                .lineTo(new Vector2d(-36,36))
                .build();

        //Make a variable for the ending pose of each of the contingencies
        Pose2d newPose = null; //null for now, we'll give it a value later

        //Init the aprilTag pipeline


        //read april tags (during init)


        //TODO: Establish which sequence we will run based on aprilTag bearing
        if(commands.aprilTagLocation > 400) {
            newPose = Left.end(); // newPose is the end of the first sequence
            chosenSequence = Left;

        } else if (commands.aprilTagLocation < 300){
            newPose = Right.end();
            chosenSequence = Right;

        } else {
            newPose = Center.end();
            chosenSequence = Center;
        }

        // Build the sequences we will run after dropping the first pixel




        waitForStart();

        if (!isStopRequested()) {

            drivetrain.followTrajectorySequence(chosenSequence);
//            drivetrain.followTrajectorySequence(tragic);
        }
    }
}
