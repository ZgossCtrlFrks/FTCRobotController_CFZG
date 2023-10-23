package com.example.meepmeeptesting;

        import com.acmerobotics.roadrunner.geometry.Pose2d;
        import com.acmerobotics.roadrunner.geometry.Vector2d;
        import com.acmerobotics.roadrunner.trajectory.Trajectory;
        import com.noahbres.meepmeep.MeepMeep;
        import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
        import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);
//        Pose2d startPose = new Pose2d(-36, -60, Math.toRadians(90));

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(52, 30, Math.toRadians(215), Math.toRadians(45), 14.35)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(new Pose2d(-36, -60, Math.toRadians(90)))
//                                .forward(30)
//                                .turn(Math.toRadians(90))
//                                .forward(30)
//                                .turn(Math.toRadians(90))
//                                .forward(30)
//                                .turn(Math.toRadians(90))
//                                .forward(30)
//                                .turn(Math.toRadians(90))
                                .lineTo(new Vector2d(-36,36))
                                .waitSeconds(2.5)
                                .lineToSplineHeading(new Pose2d(-36,45,Math.toRadians(0)))
//                                .splineTo(new Vector2d(12,58),Math.toRadians(180))
                                .splineToLinearHeading(new Pose2d(18,58),Math.toRadians(0))
                                .lineToLinearHeading(new Pose2d(50,36,Math.toRadians(180)))
                                .splineTo(new Vector2d(38,7),Math.toRadians(180))
                                .splineTo(new Vector2d(-62,12),Math.toRadians(180))
                                  .build());



        meepMeep.setBackground(MeepMeep.Background.FIELD_CENTERSTAGE_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}