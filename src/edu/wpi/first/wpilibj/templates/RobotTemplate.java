/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;


import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Relay;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

public class RobotTemplate extends IterativeRobot {
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    
    public void robotInit() {
        SmartDashboard.putNumber("CheckpointStart", 0);
        SmartDashboard.putString("error", "");
        Global.driveShift.set(DoubleSolenoid.Value.kReverse);
        Global.cameraLight.set(Relay.Value.kOn);
        SmartDashboard.putNumber("CheckpointStart", 1);
        SmartDashboard.putBoolean("AutonomousInit", false);
        SmartDashboard.putBoolean("AutonomousPeriodic", false);
        SmartDashboard.putBoolean("TeleopInit", false);
        SmartDashboard.putBoolean("TeleopPeriodic", false);
        SmartDashboard.putNumber("Checkpoint", 0);
        SmartDashboard.putNumber("CheckpointGo", 0);
        Global.ShootEnc.reset();
        Global.frontDist.setAutomaticMode(true);
        try {
            Vision.VisionStart();
        } catch (Exception ex) {
            ex.printStackTrace();
                SmartDashboard.putString("error", "RobotInit: "+ex.toString());
        }
        SmartDashboard.putNumber("CheckpointStart", 2);
        SmartDashboard.putNumber("WallDistance", 7);
        SmartDashboard.putBoolean("overrideShoot",false);
        try{
        SmartDashboard.putNumber("ShootingDistance", Global.pref.getInt("shootingDistance",120));
        SmartDashboard.putNumber("ShootAngle", Global.pref.getDouble("shootAngle", 80));
        SmartDashboard.putNumber("AutoShootLPerc", Global.pref.getDouble("autoShootLPerc",0.1));
        SmartDashboard.putNumber("AutoShootLS", Global.pref.getDouble("autoShootLS",0.5));
        SmartDashboard.putNumber("AutoShootHS", Global.pref.getDouble("autoShootHS",0.9));
        SmartDashboard.putNumber("maxShootAngle",Global.pref.getInt("MaxShootEnc",130));
        }
        catch(Exception ex){
            savepref();
            SmartDashboard.putString("error", ex.toString());
        }
        SmartDashboard.putNumber("CheckpointStart", 3);
        
    }

    public void disabledInit() {
        Global.comp.stop();
        Global.t1.stop();
        Global.drive.stopMotor();
        Global.driveShift.set(DoubleSolenoid.Value.kOff);
        Global.Shooter.stop();
        Global.pref.save();
        Global.cameraLight.set(Relay.Value.kOff);
        Global.Shooter.stop();
        Global.pickup.stopMotor();
        Global.ShootEnc.stop();
        SmartDashboard.putBoolean("AutonomousInit", false);
        SmartDashboard.putBoolean("AutonomousPeriodic", false);
        SmartDashboard.putBoolean("TeleopInit", false);
        SmartDashboard.putBoolean("TeleopPeriodic", false);
        Global.cameraLight.set(Relay.Value.kOff);
        savepref();
    }
    public void teleopInit(){
        Teleop.teleopStart();
    }
    public void teleopPeriodic(){
        Teleop.teleop();
    }
    public void autonomousInit(){
        Autonomous.autonomousStart();
    }
    public void autonomousPeriodic(){
        Autonomous.autonomous();
    }
    public void savepref(){
        try{
        Global.pref.putBoolean("cosShooter",SmartDashboard.getBoolean("CosShooter",false));
        Global.pref.putDouble("autoShootLPerc",SmartDashboard.getNumber("AutoShootLPerc",0.1));
        Global.pref.putDouble("autoShootLS",SmartDashboard.getNumber("AutoShootLS", 0.5));
        Global.pref.putDouble("autoShootHS",SmartDashboard.getNumber("AutoShootHS", 0.9));
        Global.pref.putInt("MaxShootEnc", (int)SmartDashboard.getNumber("maxShootAngle",120));
        Global.pref.putDouble("shootAngle", SmartDashboard.getNumber("ShootAngle",100));
        }
        catch(Exception ex){
            SmartDashboard.putString("error", "savepref: "+ex.toString());
        }
    }
}
