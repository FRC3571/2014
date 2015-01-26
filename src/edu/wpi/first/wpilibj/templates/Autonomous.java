/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author TomasR
 */
public class Autonomous {
    static Vision.VisionObjects vo[];
    static boolean pickupextend=false,hot=false,done1=false;
    static double asp=0.1,asls=0.5,ashs=0.9;
    public static void autonomousStart(){
        pickupextend=false;
        SmartDashboard.putNumber("Checkpoint", 1);
        Global.t1.reset();
        Global.t1.start();
        SmartDashboard.putNumber("Checkpoint", 2);
        Global.cameraLight.set(Relay.Value.kForward);
        Global.driveShift.set(DoubleSolenoid.Value.kReverse);
        SmartDashboard.putBoolean("AutonomousInit", true);
        Global.ShootEnc.start();
        Global.pref.putInt("ShootingDistance",(int)SmartDashboard.getNumber("ShootingDistance"));
        hot=false;
        try {
            Vision.VisionStart();
        } catch (Exception ex) {
            ex.printStackTrace();
                SmartDashboard.putString("error", "AutonomousInit: "+ex.toString());
        }
    }
    public static void autonomous() {
        try{
            Global.pref.putInt("ShootingDistance", (int)SmartDashboard.getNumber("ShootingDistance"));
            SmartDashboard.putNumber("Checkpoint", 3);
            vo=Vision.VisionProcessing();
            if (vo.length>1) {
                hot=true;
            }
            SmartDashboard.putNumber("Checkpoint", 4);
            SmartDashboard.putNumber("Vission", vo.length);
            //if (Global.frontDist.getRangeInches()<SmartDashboard.getNumber("ShootingDistance")+5 && !pickupextend) {
            if (Global.t1.get()>4 && !pickupextend) {
                pickupextend=true;
                Global.pickupSol.set(DoubleSolenoid.Value.kForward);
            }
            asp=SmartDashboard.getNumber("AutoShootLPerc");
            asls=SmartDashboard.getNumber("AutoShootLS", 0.5);
            ashs=SmartDashboard.getNumber("AutoShootHS", 0.9);
            if ((hot || Global.t1.get()>5) && Global.ShootEnc.get()<SmartDashboard.getNumber("ShootAngle") && Global.pickupExtended.get() && !done1)
            {
                Global.Shooter.set((Global.ShootEnc.get()<((int)(SmartDashboard.getNumber("ShootAngle")*asp))?asls:ashs));
                if (Global.ShootEnc.get()>SmartDashboard.getNumber("ShootAngle")/2) {
                    done1=true;
                }
            }
            else Global.Shooter.stop();
            for (int i = 0; i < vo.length; i++) {
                SmartDashboard.putString("object "+i, "X="+vo[i].centerX+" Y="+vo[i].centerY+" Height="+vo[i].height+" Width="+vo[i].width);
            }
            //if(Global.t1.get()<5 && Global.frontDist.getRangeInches()>SmartDashboard.getNumber("ShootingDistance")){
            if(Global.t1.get()<2.8 ){
                Global.drive.arcadeDrive(-0.8, 0);
            }
            else Global.drive.stopMotor();
            SmartDashboard.putBoolean("AutonomousPeriodic", true);
            SmartDashboard.putNumber("ShootEnc", Global.ShootEnc.get());
        }
        catch(Exception ex){
            SmartDashboard.putString("error", "AutonomousPeriodic: "+ex.toString());
        }
    }
}
